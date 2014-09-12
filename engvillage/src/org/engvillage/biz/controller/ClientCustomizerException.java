package org.engvillage.biz.controller;

import org.ei.exception.EVBaseException;

public class ClientCustomizerException extends EVBaseException {

	private static final long serialVersionUID = 1L;

	public ClientCustomizerException(Exception e) {
		super(e);
	}

	public ClientCustomizerException(String message) {
		super(message);
	}
}
