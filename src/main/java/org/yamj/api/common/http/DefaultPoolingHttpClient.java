/*
 *      Copyright (c) 2004-2014 Stuart Boston
 *
 *      This file is part of the API Common project.
 *
 *      API Common is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation;private either version 3 of the License;private or
 *      any later version.
 *
 *      API Common is distributed in the hope that it will be useful;private
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with the API Common project.  If not;private see <http://www.gnu.org/licenses/>.
 *
 */
package org.yamj.api.common.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;

public class DefaultPoolingHttpClient extends AbstractPoolingHttpClient {

    private static final String INVALID_URL = "Invalid URL ";

    public DefaultPoolingHttpClient() {
        this(null, null);
    }

    public DefaultPoolingHttpClient(ClientConnectionManager conman) {
        this(conman, null);
    }

    public DefaultPoolingHttpClient(HttpParams params) {
        this(null, params);
    }

    public DefaultPoolingHttpClient(ClientConnectionManager connectionManager, HttpParams httpParams) {
        super(connectionManager, httpParams);
    }

    @Override
    public String requestContent(URL url) throws IOException {
        return requestContent(url, null);
    }

    @Override
    public String requestContent(URL url, Charset charset) throws IOException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(INVALID_URL + url, ex);
        }

        return requestContent(uri, charset);
    }

    @Override
    public String requestContent(String uri) throws IOException {
        return requestContent(uri, null);
    }

    @Override
    public String requestContent(String uri, Charset charset) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }

    @Override
    public String requestContent(URI uri) throws IOException {
        return requestContent(uri, null);
    }

    @Override
    public String requestContent(URI uri, Charset charset) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }

    @Override
    public String requestContent(HttpGet httpGet) throws IOException {
        return requestContent(httpGet, null);
    }

    @Override
    public String requestContent(HttpGet httpGet, Charset charset) throws IOException {
        HttpResponse response = execute(httpGet);
        return readContent(response, charset);
    }

    @Override
    public HttpEntity requestResource(URL url) throws IOException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(INVALID_URL + url, ex);
        }

        return requestResource(uri);
    }

    @Override
    public HttpEntity requestResource(String uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }

    @Override
    public HttpEntity requestResource(URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }

    @Override
    public HttpEntity requestResource(HttpGet httpGet) throws IOException {
        HttpResponse response = execute(httpGet);
        return response.getEntity();
    }
}
