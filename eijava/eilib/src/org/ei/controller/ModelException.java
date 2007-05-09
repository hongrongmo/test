package org.ei.controller;


public class ModelException extends Exception
{
	private String ID;

	public ModelException(String ID)
	{
		this.ID = ID;
	}

	public String toString()
	{
		return this.ID;
	}
}
