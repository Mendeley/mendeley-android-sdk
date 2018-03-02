package com.mendeley.sdk.exceptions;

import java.util.Date;


public class DeletedMendeleyUserException extends MendeleyException{

    public DeletedMendeleyUserException(String detailMessage) {
        this(detailMessage, null);
    }

    public DeletedMendeleyUserException(String detailMessage, Throwable throwable) {
        super(detailMessage + " (" + new Date(System.currentTimeMillis()).toString() + ") ", throwable);
    }
}
