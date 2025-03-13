package com.bookstore.exception;

public class InvalidBookTypeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidBookTypeException(String message) {
		super(message);
	}
}
