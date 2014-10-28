package com.mendeley.api.exceptions;

/**
 * Base class for all the exceptions that are generated by the Mendeley API and should be
 * caught by the application.
 */
public class MendeleyException extends Exception {
	public MendeleyException(String message) {
		super(message);
	}

    public MendeleyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
