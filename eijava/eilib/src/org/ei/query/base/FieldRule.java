package org.ei.query.base;

import java.util.Properties;

public class FieldRule
{
	private String displayName;
	private String shortDisplayName;
	private String[] databaseMappings;
	private Properties options;
	private boolean autoStem = false;
	private boolean constrained;

	public void setConstrained(boolean constrained)
	{
		this.constrained = constrained;
	}

	public boolean getConstrained()
	{
		return this.constrained;
	}
	
	public boolean getAutoStem()
	{
		return this.autoStem;
	}

	public void setAutoStem(boolean autoStem)
	{
		this.autoStem = autoStem;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getShortDisplayName()
	{
		return this.shortDisplayName;
	}

	public void setShortDisplayName(String shortDisplayName)
	{
		this.shortDisplayName = shortDisplayName;
	}

	public String[] getDatabaseMappings()
	{
		return this.databaseMappings;
	}

	public void setDatabaseMappings(String[] databaseMappings)
	{
		this.databaseMappings = databaseMappings;
	}

	public void setOptions(Properties options)
	{
		this.options = options;
	}

	public Properties getOptions()
	{
		return this.options;
	}


}
