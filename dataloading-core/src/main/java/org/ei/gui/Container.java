package org.ei.gui;

import java.util.Vector;

public class Container extends Component {

	protected Vector vComponents;
	
	public Container() {
		vComponents = new Vector();
	}
	public void add(IComponent c) {
		vComponents.addElement(c);
	}
	public void remove(IComponent c) {
		vComponents.removeElement(c);
	}
	public void removeAllElements(){
		vComponents.removeAllElements();
	}
	public Vector getComponents() {
		return vComponents;
	}
	public String render() {
		
		int n = vComponents.size();

		StringBuffer toHtmlBuffer = new StringBuffer();

		for (int i=0; i<n; i+=1) {
			IComponent c = (IComponent) vComponents.elementAt(i);
			toHtmlBuffer.append(c.render());
			
		}	
		return toHtmlBuffer.toString();
	}
}