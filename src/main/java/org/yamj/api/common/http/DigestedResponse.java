package org.yamj.api.common.http;

/**
 * Contains the content of the digested response stream body and its HTTP status code.
 */
public class DigestedResponse {

    private int statusCode;

    private String content;

    public DigestedResponse() {

        super();
    }

    public DigestedResponse(final int statusCode,
                            final String content) {

        super();
        this.statusCode = statusCode;
        this.content = content;
    }

    public int getStatusCode() {

        return statusCode;
    }

    public void setStatusCode(final int statusCode) {

        this.statusCode = statusCode;
    }

    public String getContent() {

        return content;
    }

    public void setContent(final String content) {

        this.content = content;
    }

    @Override
    public String toString() {

        return new StringBuilder().append(this.statusCode).append(" ").append(this.content).toString();
    }
}
