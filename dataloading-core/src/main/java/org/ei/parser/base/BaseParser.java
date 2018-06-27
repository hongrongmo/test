package org.ei.parser.base;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Stack;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;

/**
 * <pre>
 * BooleanQuery:   BooleanPhrase | Expression | AndQuery | OrQuery | NotQuery |'(' BooleanQuery ')'
 * AndQuery:       BooleanQuery BooleanAnd BooleanQuery
 * OrQuery:        BooleanQuery BooleanOr BooleanQuery
 * NotQuery:       BooleanQuery BooleanNot BooleanQuery
 * ----------
 * Expression:     BooleanPhrase 'WN' Field
 * Field:          Literal
 * ----------
 * BooleanPhrase:  Phrase | AndPhrase | OrPhrase | NotPhrase | '('BooleanPhrase')'
 * AndPhrase:      BooleanPhrase BooleanAnd BooleanPhrase
 * OrPhrase:       BooleanPhrase BooleanOr BooleanPhrase
 * NotPhrase:      BooleanPhrase BooleanNot BooleanPhrase
 * ----------
 * Phrase:         Term | Phrase Space Phrase | Phrase ProximityOperator Phrase
 * ----------
 * Term:           Literal | Regex | ExactTerm
 * ExactTerm:      '{' *.* '}'
 * Regex:          Literal.WildcardSymbol | StemSymbol.Literal
 * literal:        {?,0,..,9,a,...,z,A,...Z}*
 * WildcardSymbol: '*'
 * StemSymbol:     '$'
 * ----------
 * ProximityOperator: OrderedNear | UnorderedNear
 * ----------
 * 
 * OrderedNear       'ONEAR' | 'ONEAR/Distance'
 * UnorderedNear     'NEAR'  | 'NEAR/Distance'
 * Distance          [0-9]*
 * 
 * BooleanAnd:     '&' | 'AND'
 * BooleanOr :     '|' | 'OR'
 * BooleanNot:     '!' | 'NOT'
 * 
 * 
 * Precedence Rules
 * 
 * 1) Parenthesis
 * 2) Phrase Rules
 * 3) Expression Rules
 * 4) Boolean Rules
 * </pre>
 **/

public class BaseParser extends BaseNodeVisitor {

	private Stack<BaseNode> stack = new Stack();
	private static BooleanAnd bAND = new BooleanAnd("AND");
	private static BooleanOr bOR = new BooleanOr("OR");
	private static BooleanNot bNOT = new BooleanNot("NOT");
	private static KeywordWITHIN keywordWN = new KeywordWITHIN("WN");
	private static OpenParen openParen = new OpenParen("(");
	private static CloseParen closeParen = new CloseParen(")");
	private Perl5Util perl = new Perl5Util();

	private Exception e;

	private int parenCount = 0;
	protected StreamTokenizer scanner;

	private int BEGIN = 1;
	private int FINISH = 2;

	private int state = BEGIN;
	private BaseNode lastToken;

	/**
	 * Parse the search string.  
	 * @param searchString
	 * @return
	 * @throws InfrastructureException with error code PARSE_ERROR
	 */
	public BaseNode parse(String searchString) throws InfrastructureException {
		BaseNode node = null;
		BaseNode nextToken = null;
		scanner = scannerInit(searchString);

		try {
			while ((nextToken = getNextToken()) != null && state != FINISH) {
				stack.push(nextToken);
				reduce(stack);
			}
		} catch (IOException e1) {
			throw new InfrastructureException(SystemErrorCodes.PARSE_ERROR, "Stack IO failed", e);
		}

		if (e != null) {
			throw new InfrastructureException(SystemErrorCodes.PARSE_ERROR, "Exception occurred", e);
		}

		node = (BaseNode) stack.pop();
		if (stack.size() > 0 || !node.getType().equals(BooleanQuery.TYPE)) {
			throw new InfrastructureException(SystemErrorCodes.PARSE_ERROR, "Stack size invalid");
		}

		return node;
	}

	public StreamTokenizer scannerInit(String searchString) {
		StreamTokenizer s = new StreamTokenizer(new QueryFilterReader((new StringReader(searchString))));
		s.resetSyntax();
		s.wordChars(97, 122); // 'a-z'
		s.wordChars(65, 90); // 'A-Z'
		s.wordChars(63, 63); // '?'
		s.wordChars(48, 57); // '0-9'
		s.wordChars(45, 47); // '-./'
		s.wordChars(39, 39); // '''
		s.wordChars(36, 36); // '$'
		s.wordChars(42, 42); // '*'
		s.wordChars(44, 44); // ','
		s.quoteChar(34); // '"'
		s.whitespaceChars(32, 32);
		s.whitespaceChars(9, 13);
		return s;
	}

	private void reduce(Stack<BaseNode> stack) {
		BaseNode topNode = (BaseNode) stack.elementAt(stack.size() - 1);
		topNode.accept(this);
	}

	private void pushBackToken() {
		scanner.pushBack();
	}

	private BaseNode getNextToken() throws IOException {
		BaseNode p = null;

		int t;

		t = scanner.nextToken();

		if (t == StreamTokenizer.TT_EOF) {
			state = FINISH;
		} else {
			if (t == StreamTokenizer.TT_WORD) {

				if (scanner.sval.equalsIgnoreCase("AND")) {
					p = bAND;
				} else if (scanner.sval.equalsIgnoreCase("OR")) {
					p = bOR;
				} else if (scanner.sval.equalsIgnoreCase("NOT")) {
					p = bNOT;
				} else if (scanner.sval.equalsIgnoreCase("WN")) {

					p = keywordWN;
				} else if (perl.match("/(\\bNEAR)\\/?(\\d*)/i", scanner.sval)) {
					String sd = perl.group(2);
					// System.out.println("Distance:"+ sd);
					int distance = 4;
					if (sd != null && sd.length() > 0) {
						distance = Integer.parseInt(sd);
					}

					p = new ProximityOperator(new UnorderedNear("NEAR", distance));
				} else if (perl.match("/(\\bONEAR)\\/?(\\d*)/i", scanner.sval)) {
					String sd = perl.group(2);
					int distance = 4;
					if (sd != null && sd.length() > 0) {
						distance = Integer.parseInt(sd);
					}

					p = new ProximityOperator(new OrderedNear("ONEAR", distance));
				} else if (scanner.sval.indexOf("$") == 0) {
					p = new Term(new StemmedTerm(new Literal(scanner.sval.substring(1, scanner.sval.length()))));
				} else if (scanner.sval.indexOf("*") > -1 || scanner.sval.indexOf("?") > -1) {
					// System.out.println("Got regex");
					p = new Term(new Regex(scanner.sval));
				} else {
					// //System.out.println("Token:"+scanner.sval);
					if (lastToken != null && lastToken.getType().equals(KeywordWITHIN.TYPE)) {
						p = new Field(scanner.sval);
					} else {
						p = new Term(new Literal(scanner.sval));
					}

				}
			} else if (t == StreamTokenizer.TT_NUMBER) {
				// System.out.println("PROBLEMMMMMMMMMMMM");
				double d = (scanner.nval);
				int di = (new Double(d)).intValue();
				double d2 = (new Integer(di)).doubleValue();
				if (d == d2) {
					p = new Literal(Integer.toString(di));
				} else {
					p = new Literal(Double.toString(d));
				}
			} else {

				char ch = (char) scanner.ttype;

				if (ch == '(') {
					p = openParen;
				} else if (ch == ')') {
					p = closeParen;
				} else if (ch == '"') {
					// We have string constant
					ExactTerm elit = new ExactTerm(scanner.sval);
					p = new Term(elit);
				}
			}
		}
		lastToken = p;
		return p;
	}

	public void visitWith(BooleanQuery topNode) {
		if (stack.size() > 2) {
			BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
			BaseNode node3 = (BaseNode) stack.elementAt(stack.size() - 3);
			if (node3.getType().equals(BooleanQuery.TYPE) || node3.getType().equals(BooleanPhrase.TYPE)) {
				BooleanQuery bq = null;

				if (node3.getType().equals(BooleanPhrase.TYPE)) {
					bq = new BooleanQuery((BooleanPhrase) node3);
					// System.out.println("Reducing BooleanQuery:"+
					// bq.getValue());
				} else {
					bq = (BooleanQuery) node3;
				}

				if (node2.getType().equals(BooleanAnd.TYPE)) {
					AndQuery aquery = new AndQuery((BooleanQuery) bq, (BooleanAnd) node2, topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(aquery);
					// System.out.println("Reduced AndQuery:"+
					// aquery.getValue());
					reduce(stack);
				} else if (node2.getType().equals(BooleanOr.TYPE)) {
					OrQuery oquery = new OrQuery((BooleanQuery) bq, (BooleanOr) node2, topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(oquery);
					// System.out.println("Reduced OrQuery:"+
					// oquery.getValue());
					reduce(stack);
				} else if (node2.getType().equals(BooleanNot.TYPE)) {
					NotQuery nquery = new NotQuery((BooleanQuery) bq, (BooleanNot) node2, topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(nquery);
					// System.out.println("Reduced NotQuery:"+nquery.getValue());
					reduce(stack);
				}
			}
		}

	}

	public void visitWith(AndQuery topNode) {
		BooleanQuery bquery = new BooleanQuery(topNode);
		stack.pop();
		stack.push(bquery);
		// System.out.println("Reduced AndQuery to BooleanQuery:"+
		// bquery.getValue());
		reduce(stack);
	}

	public void visitWith(OrQuery topNode) {
		BooleanQuery bquery = new BooleanQuery(topNode);
		stack.pop();
		stack.push(bquery);
		// System.out.println("Reduced OrQuery to BooleanQuery:"+
		// bquery.getValue());
		reduce(stack);
	}

	public void visitWith(NotQuery topNode) {
		BooleanQuery bquery = new BooleanQuery(topNode);
		stack.pop();
		stack.push(bquery);
		// System.out.println("Reduced NotQuery to BooleanQuery:"+
		// bquery.getValue());
		reduce(stack);
	}

	public void visitWith(AndPhrase topNode) {
		BooleanPhrase bphrase = new BooleanPhrase(topNode);
		stack.pop();
		stack.push(bphrase);
		// System.out.println("Reduced AndPhrase to BooleanPhrase:"+
		// bphrase.getValue());
		reduce(stack);
	}

	public void visitWith(OrPhrase topNode) {
		BooleanPhrase bphrase = new BooleanPhrase(topNode);
		stack.pop();
		stack.push(bphrase);
		// System.out.println("Reduced OrPhrase to BooleanPhrase:"+
		// bphrase.getValue());
		reduce(stack);
	}

	public void visitWith(NotPhrase topNode) {
		BooleanPhrase bphrase = new BooleanPhrase(topNode);
		stack.pop();
		stack.push(bphrase);
		// System.out.println("Reduced NotPhrase to BooleanPhrase:"+
		// bphrase.getValue());
		reduce(stack);
	}

	public void visitWith(Field topNode) {
		BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
		if (stack.size() >= 3 && node2.getType().equals(KeywordWITHIN.TYPE)) {
			BaseNode node3 = (BaseNode) stack.elementAt(stack.size() - 3);
			if (node3.getType().equals(BooleanPhrase.TYPE)) {
				Expression ex = new Expression((BooleanPhrase) node3, (KeywordWITHIN) node2, topNode);
				stack.pop();
				stack.pop();
				stack.pop();
				stack.push(ex);
				// System.out.println("Reducing BooleanPhrase to Expression:"+
				// ex.getValue());
				reduce(stack);
			} else {
				this.e = new Exception("<DISPLAY>Query Error, Left side of expression must be a BooleanPhrase</DISPLAY>");
			}
		} else {
			this.e = new Exception("<DISPLAY>Query Error, Invalid Expression</DISPLAY>");
		}
	}

	public void visitWith(CloseParen topNode) {
		--parenCount;

		if (stack.size() < 3) {
			this.e = new Exception("<DISPLAY>Query Error, Unbalanced Parenthesis</DISPLAY>");
			return;
		}

		BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
		BaseNode node3 = (BaseNode) stack.elementAt(stack.size() - 3);

		if (node2.getType().equals(BooleanQuery.TYPE) && node3.getType().equals(OpenParen.TYPE)) {

			BooleanQuery q = new BooleanQuery((OpenParen) node3, (BooleanQuery) node2, topNode);
			// System.out.println("Reducing BooleanQuery inside Parens:"+
			// q.getValue());
			stack.pop();
			stack.pop();
			stack.pop();
			stack.push(q);
			reduce(stack);
		} else if (node2.getType().equals(BooleanPhrase.TYPE) && node3.getType().equals(OpenParen.TYPE)) {

			BooleanPhrase q = new BooleanPhrase((OpenParen) node3, (BooleanPhrase) node2, topNode);
			// System.out.println("Reducing BooleanPhrase inside Parens:"+
			// q.getValue());
			stack.pop();
			stack.pop();
			stack.pop();
			stack.push(q);
			reduce(stack);
		} else {
			this.e = new Exception("<DISPLAY>Query Error, Unbalanced Parenthesis</DISPLAY>");
		}
	}

	public void visitWith(OpenParen topNode) {
		++parenCount;
	}

	public void visitWith(ExactTerm topNode) {
		Term t = new Term(topNode);
		stack.pop();
		stack.push(t);
		// System.out.println("Reducing ExactTerm to Term:"+ t.getValue());
		reduce(stack);
	}

	public void visitWith(Term topNode) {

		BaseNode tok = null;

		try {
			if (state != FINISH && (tok = getNextToken()) != null) {
				if (tok.getType().equals(ProximityOperator.TYPE)) {
					stack.push(tok);
					reduce(stack);
					return;
				} else {
					pushBackToken();
				}
			}
		} catch (Exception ev) {
			this.e = ev;
			return;
		}

		if (stack.size() > 2) {
			BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
			BaseNode node3 = (BaseNode) stack.elementAt(stack.size() - 3);
			if (node2.getType().equals(ProximityOperator.TYPE)) {
				if (node3.getType().equals(Term.TYPE)) {
					ProximityPhrase pphrase = new ProximityPhrase((Term) node3, (ProximityOperator) node2, (Term) topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(pphrase);
					// System.out.println("Reducing ProximityPhrase:"+
					// pphrase.getValue());
					reduce(stack);
				}
			} else {
				Phrase p = new Phrase(topNode);
				// System.out.println("Reducing Term to a Phrase:"
				// +p.getValue());
				stack.pop();
				stack.push(p);
				reduce(stack);
			}
		} else {
			Phrase p = new Phrase(topNode);
			// System.out.println("Reducing Term to a Phrase:" +p.getValue());
			stack.pop();
			stack.push(p);
			reduce(stack);
		}
	}

	public void visitWith(ProximityPhrase topNode) {

		if (stack.size() > 2) {
			BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
			BaseNode node3 = (BaseNode) stack.elementAt(stack.size() - 3);
			if (node2.getType().equals(ProximityOperator.TYPE)) {
				if (node3.getType().equals(Term.TYPE)) {
					ProximityPhrase pphrase = new ProximityPhrase((Term) node3, (ProximityOperator) node2, (ProximityPhrase) topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(pphrase);
					// System.out.println("Reducing ProximityPhrase:"+
					// pphrase.getValue());
					reduce(stack);
				}
			} else {
				Phrase p = new Phrase(topNode);
				// System.out.println("Reducing ProximityPhrase to Phrase:"
				// +p.getValue());
				stack.pop();
				stack.push(p);
				reduce(stack);
			}
		} else {
			Phrase p = new Phrase(topNode);
			// System.out.println("Reducing ProximityPhrase to Phrase:"
			// +p.getValue());
			stack.pop();
			stack.push(p);
			reduce(stack);
		}
	}

	public void visitWith(Expression topNode) {
		BooleanQuery bq = new BooleanQuery(topNode);
		stack.pop();
		stack.push(bq);
		// System.out.println("Reducing Expression to BooleanQuery:"+
		// bq.getValue());
		reduce(stack);
	}

	public void visitWith(BooleanPhrase topNode) {

		boolean boost = false;
		BaseNode tok = null;

		try {
			// Make the BooleanPhrase greedy for an expression.
			if (state != FINISH && (tok = getNextToken()) != null) {
				if (tok.getType().equals(KeywordWITHIN.TYPE)) {
					stack.push(tok);
					reduce(stack);
					return;
				} else {
					pushBackToken();
				}
			}
		} catch (Exception ev) {
			this.e = ev;
			return;
		}

		if (stack.size() > 2) {
			BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
			BaseNode node3 = (BaseNode) stack.elementAt(stack.size() - 3);
			if (node3.getType().equals(BooleanPhrase.TYPE)) {
				if (node2.getType().equals(BooleanAnd.TYPE)) {
					AndPhrase aphrase = new AndPhrase((BooleanPhrase) node3, (BooleanAnd) node2, topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(aphrase);
					// System.out.println("Reducing AndPhrase:"+
					// aphrase.getValue());
					reduce(stack);
				} else if (node2.getType().equals(BooleanOr.TYPE)) {

					OrPhrase ophrase = new OrPhrase((BooleanPhrase) node3, (BooleanOr) node2, topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.push(ophrase);
					// System.out.println("Reducing OrPhrase:"+
					// ophrase.getValue());
					reduce(stack);
				} else if (node2.getType().equals(BooleanNot.TYPE)) {
					NotPhrase nphrase = new NotPhrase((BooleanPhrase) node3, (BooleanNot) node2, topNode);
					stack.pop();
					stack.pop();
					stack.pop();
					// System.out.println("Reducing NotPhrase:"+
					// nphrase.getValue());
					stack.push(nphrase);
					reduce(stack);
				}
			} else if (node3.getType().equals(BooleanQuery.TYPE)) {
				BooleanQuery bquery = new BooleanQuery(topNode);
				// System.out.println("Reducing BooleanQuery from BooleanPhrase Joel:"+
				// bquery.getValue());
				stack.pop();
				stack.push(bquery);
				reduce(stack);
			}
		} else {
			// There is no more to this BooleanPhrase so we can
			// Boost it up to a BooleanQuery

			if (parenCount == 0) {
				BooleanQuery bquery = new BooleanQuery(topNode);
				stack.pop();
				stack.push(bquery);
				// System.out.println("Reducing BooleanQuery from BooleanPhrase 2:"+
				// bquery.getValue());
				reduce(stack);
			}
		}
	}

	public void visitWith(Phrase topNode) {
		BaseNode tok = null;
		try {
			if (state != FINISH && (tok = getNextToken()) != null) {
				if (tok.getType().equals(Term.TYPE)) {
					stack.push(tok);
					reduce(stack);
					return;
				} else {
					pushBackToken();
				}
			}
		} catch (Exception ev) {
			this.e = ev;
			return;
		}

		if (stack.size() >= 2) {
			BaseNode node2 = (BaseNode) stack.elementAt(stack.size() - 2);
			if (node2.getType().equals(Phrase.TYPE)) {
				Phrase pnode = new Phrase((Phrase) node2, topNode);
				// System.out.println("Reduced Phrase: "+ pnode.getValue());
				stack.pop();
				stack.pop();
				stack.push(pnode);
				reduce(stack);
			} else {
				// This phrase is complete so we can boost
				// it up to a BooleanPhrase
				BooleanPhrase bphrase = new BooleanPhrase(topNode);
				stack.pop();
				stack.push(bphrase);
				// System.out.println("Reduced BooleanPhrase from Phrase 1:"+bphrase.getValue());
				reduce(stack);
			}
		} else {
			BooleanPhrase bphrase = new BooleanPhrase(topNode);
			stack.pop();
			stack.push(bphrase);
			// System.out.println("Reduced BooleanPhrase from Phrase 2:"+bphrase.getValue());
			reduce(stack);
		}
	}
}
