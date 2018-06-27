package org.ei.gui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

public class ListBoxOption extends Component implements Comparable<ListBoxOption> {
	/**
	 * Constructor for ListBoxOption.  
	 * @param selected
	 * @param name
	 * @param value
	 */
	public ListBoxOption(String selected, String name, String value) {
		this.name = name;
		this.value = value;
		this.selected = selected;
	}
	
	private String selected;
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}

	private Map<String,String> attributes = new HashMap<String,String>();
	public void addAttribute(String key, String value) {
		this.attributes.put(key, value);
	}
	
	@Override
	public String render() {
		return toString();
	}
	
	@Override
	public String toString() {
		StringBuffer outputString = new StringBuffer();
		outputString.append("<option value=\"" + name + "\"");
		if (!GenericValidator.isBlankOrNull(name) && name.equals(selected)) {
			outputString.append(" selected=\"selected\"");
		}
		if (this.attributes.size() > 0) {
			for (String key : this.attributes.keySet()) {
				outputString.append(" " + key + "=\"" + this.attributes.get(key) + "\"");
			}
		}
		outputString.append(">" + value + "</option>");
		return outputString.toString();
	}
	
    @Override
    public int compareTo(ListBoxOption o) {
        return value.compareTo(o.getValue());
    }
}
