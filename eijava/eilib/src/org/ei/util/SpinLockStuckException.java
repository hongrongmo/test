package org.ei.util;

import org.ei.exception.MajorSystemProblem;

public class SpinLockStuckException extends MajorSystemProblem
{
	

	public SpinLockStuckException()
	{
		super(new Exception("Spin Lock Is Stuck !!!"));
	}




}
