package org.yamj.api.common.exception;

import javax.xml.ws.WebServiceException;

import org.yamj.api.common.http.DigestedResponse;

public class ClientAPIException extends WebServiceException {

    private static final long serialVersionUID = 278660717634380289L;

    private final DigestedResponse response;

    public ClientAPIException(final DigestedResponse response) {

        super();
        this.response = response;
    }

    public ClientAPIException(final DigestedResponse response,
                              final Throwable cause) {

        super(cause);
        this.response = response;
    }

    public DigestedResponse getResponse() {

        return response;
    }
}
