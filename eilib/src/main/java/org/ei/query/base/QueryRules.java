package org.ei.query.base;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
*
*
**/

public final class QueryRules {

    public static final String YEAR_RULE = "YR";
    public static final String AUTHOR_RULE = "AU";

    private String yearField = "-";
    private int minYear;
    private int maxYear;

    private static Hashtable<String, QueryRules> rulesTable = new Hashtable<String, QueryRules>();
    private Hashtable<String, FieldRule> fields = new Hashtable<String, FieldRule>();

    public static synchronized QueryRules getInstance(String rulesFile) throws QueryException {
        QueryRules q = null;

        if (rulesTable.containsKey(rulesFile)) {
            q = (QueryRules) rulesTable.get(rulesFile);
        } else {

            q = new QueryRules(rulesFile);
            rulesTable.put(rulesFile, q);
        }

        return q;
    }

    public static void main(String args[]) throws Exception {
        QueryRules rules = QueryRules.getInstance("c:\\choice\\test.xml");

        FieldRule aRule = rules.getFieldRule("TI");
        String[] mappings = aRule.getDatabaseMappings();
        for (int x = 0; x < mappings.length; ++x) {
            System.out.println("Mapping:" + mappings[x]);
        }

        FieldRule rule = rules.getYearRule();
        System.out.println("Name:" + rule.getDisplayName());
        System.out.println("Value:" + rule.getShortDisplayName());
        Properties options = rule.getOptions();

        System.out.println("NumYears:" + options.size());
        System.out.println("MinYear:" + rules.getMinYear());
        System.out.println("MaxYear:" + rules.getMaxYear());
        mappings = rule.getDatabaseMappings();
        for (int x = 0; x < mappings.length; ++x) {
            System.out.println("Mapping:" + mappings[x]);
        }

        FieldRule lRule = rules.getFieldRule("LA");
        Properties lProps = lRule.getOptions();
        Enumeration<?> keys = lProps.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            System.out.println("Lang Ops:" + key + "," + lProps.getProperty(key));
        }
    }

    private QueryRules(String config) throws QueryException {
        try {
            DOMParser parser = new DOMParser();
            Document doc = parser.parse(config);
            NodeList topNode = doc.getChildNodes();
            Node queryRulesNode = topNode.item(0);
            NodeList queryRules = queryRulesNode.getChildNodes();
            Node constrainedNode = queryRules.item(5);
            loadUnconstrained(constrainedNode);
            Node unconstrainedNode = queryRules.item(7);
            loadConstrained(unconstrainedNode);
        } catch (Exception e) {
            throw new QueryException(e);
        }
    }

    private void loadUnconstrained(Node node) {
        NodeList ufields = node.getChildNodes();
        for (int x = 1; x < ufields.getLength(); x = x + 2) {
            Node ufield = ufields.item(x);
            FieldRule rule = new FieldRule();
            NamedNodeMap at = ufield.getAttributes();
            Node snameNode = at.getNamedItem("SHORTNAME");
            Node dnameNode = at.getNamedItem("DISPLAYNAME");
            Node autoStemNode = at.getNamedItem("AUTOSTEM");
            rule.setShortDisplayName(snameNode.getNodeValue());
            rule.setDisplayName(dnameNode.getNodeValue());
            if (autoStemNode != null) {
                rule.setAutoStem(true);
            } else {
                rule.setAutoStem(false);
            }

            if (ufield.hasChildNodes()) {
                NodeList ch = ufield.getChildNodes();
                Node databaseMapNode = ch.item(1);
                NodeList dmaps = databaseMapNode.getChildNodes();
                String[] mappings = new String[(dmaps.getLength() - 1) / 2];
                int m = 0;
                for (int z = 1; z < dmaps.getLength(); z = z + 2) {
                    Node map = dmaps.item(z);
                    mappings[m] = map.getFirstChild().getNodeValue();
                    ++m;
                }
                rule.setDatabaseMappings(mappings);
            }

            fields.put(rule.getShortDisplayName(), rule);
        }
    }

    private void loadConstrained(Node node) {
        NodeList ufields = node.getChildNodes();
        for (int x = 1; x < ufields.getLength(); x = x + 2) {
            Node ufield = ufields.item(x);
            FieldRule rule = new FieldRule();
            rule.setConstrained(true);
            NamedNodeMap at = ufield.getAttributes();
            Node snameNode = at.getNamedItem("SHORTNAME");
            Node dnameNode = at.getNamedItem("DISPLAYNAME");
            rule.setShortDisplayName(snameNode.getNodeValue());
            rule.setDisplayName(dnameNode.getNodeValue());

            NodeList ch = ufield.getChildNodes();
            Node optionsNode = ch.item(1);
            NodeList options = optionsNode.getChildNodes();
            Properties props = new Properties();

            boolean isYear = false;
            if (rule.getShortDisplayName().equals(YEAR_RULE)) {
                isYear = true;
            }

            Node nameNode = null;
            Node valueNode = null;

            for (int z = 1; z < options.getLength(); z = z + 2) {
                Node option = options.item(z);
                NamedNodeMap at1 = option.getAttributes();
                nameNode = at1.getNamedItem("NAME");
                valueNode = at1.getNamedItem("VALUE");
                props.put(valueNode.getNodeValue(), nameNode.getNodeValue());
                if (z == 1 && isYear) {
                    setMinYear(Integer.parseInt(valueNode.getNodeValue()));
                }
            }

            if (isYear) {
                setMaxYear(Integer.parseInt(nameNode.getNodeValue()));
            }

            rule.setOptions(props);

            if (ch.getLength() > 3) {

                Node databaseMapNode = ch.item(3);
                NodeList dmaps = databaseMapNode.getChildNodes();
                String[] mappings = new String[(dmaps.getLength() - 1) / 2];
                int m = 0;
                for (int l = 1; l < dmaps.getLength(); l = l + 2) {
                    Node map = dmaps.item(l);
                    Node theNode = map.getFirstChild();

                    mappings[m] = theNode.getNodeValue();
                    ++m;
                }
                rule.setDatabaseMappings(mappings);
            }

            if (isYear) {
                setYearRule(rule.getShortDisplayName(), rule);
            } else {
                fields.put(rule.getShortDisplayName(), rule);
            }
        }

    }

    public FieldRule getFieldRule(String fieldID) {
        FieldRule rule = null;
        Object o = fields.get(fieldID);
        if (o != null) {
            rule = (FieldRule) o;
        }

        return rule;
    }

    public void setFieldRule(String fieldID, FieldRule rule) {
        this.fields.put(fieldID, rule);
    }

    public FieldRule getYearRule() {
        return getFieldRule(this.yearField);
    }

    public void setYearRule(String fieldID, FieldRule rule) {
        this.yearField = fieldID;
        this.fields.put(fieldID, rule);
    }

    public int getMaxYear() {
        return this.maxYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    public int getMinYear() {
        return this.minYear;
    }
}
