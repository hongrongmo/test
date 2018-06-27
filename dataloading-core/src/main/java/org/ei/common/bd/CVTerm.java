package org.ei.common.bd;
import java.util.*;
//import org.ei.data.bd.loadtime.BdParser;
public class CVTerm
{
	private String term ="";
	private String prefix="";
	private String postfix="";

	public CVTerm(String term,
				  String prefix,
				  String postfix)
	{
	   if (term != null) this.term = term;
	   if (prefix != null) this.prefix = prefix;
	   if (postfix != null) this.postfix = postfix;
	}

	public CVTerm()
	{

	}

	public String getTerm() {
		return term;
	}
	public void setTerm(String term)
	{
		this.term = term;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPostfix() {
		return postfix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}


}
