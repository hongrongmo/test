package org.ei.xml;

/** This is a Utility Class for xml parsing
 *  This class excepts root element as an arg to constructor.
 *  
 *  @author Ravi Kumar Gullapalli
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlParser {
    private Element element;

    public XmlParser() {
    }

    public XmlParser(Element aElement) {
        element = aElement;
    }

    /**
     * This Method returns a Vector of Child Nodes for the root element.
     * 
     * @return List
     */
    public List<Node> getNodes() {
        NodeList childNodeList = element.getChildNodes();
        List<Node> listOfNodes = Collections.synchronizedList(new ArrayList<Node>());
        int len = childNodeList.getLength();
        for (int i = 0; i < len; i++) {
            if (!(childNodeList.item(i).getNodeType() == (Node.TEXT_NODE))) {
                listOfNodes.add(childNodeList.item(i));
            }
        }
        return listOfNodes;
    }

    /**
     * This method returns a node by matching attr value
     * 
     * @param matchAttr
     *            --name of attribute that is to be matched
     * @param listOfNodes
     *            --list of nodes
     * @return Node
     */
    public Node getNodeByMatchingAttr(String matchAttr, List<Node> listOfNodes) {
        for (int j = 0; j < listOfNodes.size(); j++) {
            Node node = listOfNodes.get(j);
            NamedNodeMap nnm = node.getAttributes();
            if (matchAttr.equals(nnm.item(0).getNodeValue())) {
                return node;
            }
        }
        return null;
    }

    /**
     * This method returns a vector of subnodes of a node. The node which you got from getNodes method.
     * 
     * @param node
     * @return Vector
     */
    public List<Node> getSubNodes(Node node) {
        List<Node> listOfSubNodes = Collections.synchronizedList(new ArrayList<Node>());
        try {
            Element element = (Element) node;
            NodeList nodeList = element.getChildNodes();
            int len = nodeList.getLength();
            for (int r = 0; r < len; r++) {
                if (!(nodeList.item(r).getNodeType() == (Node.TEXT_NODE))) {
                    listOfSubNodes.add(nodeList.item(r));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfSubNodes;
    }

    /**
     * This method returns Node value from the Node. If Node is <test>ravi</test> then the return of this method is "ravi". If you don't have value for a
     * particular node then return space.
     * 
     * @param node
     * @return String
     */
    public String getElementNodeValue(Node node) {
        if (node.getChildNodes().item(0) == null) {
            return " ";
        }
        return node.getChildNodes().item(0).getNodeValue();
    }

    /**
     * This method returns Node name from the Node.
     * 
     * @param node
     * @return String
     */
    public String getElementNodeName(Node node) {
        return node.getNodeName();
    }

}// end of class

