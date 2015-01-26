/*
 *      Copyright (c) 2004-2015 Stuart Boston
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

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

@SuppressWarnings("deprecation")
public class HttpClientWrapper implements CommonHttpClient, Closeable {

    private static final String INVALID_URL = "Invalid URL ";
    
    private final HttpClient httpClient;
    private boolean randomUserAgent = false;
    
    public HttpClientWrapper(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setRandomUserAgent(boolean randomUserAgent) {
        this.randomUserAgent = randomUserAgent;
    }

    @SuppressWarnings("unused")
    protected void prepareRequest(HttpUriRequest request) throws ClientProtocolException {
        if (randomUserAgent) {
            request.setHeader(HTTP.USER_AGENT, UserAgentSelector.randomUserAgent());
        }
    }

    @SuppressWarnings("unused")
    protected void prepareRequest(HttpHost target, HttpRequest request) throws ClientProtocolException {
        if (randomUserAgent) {
          request.setHeader(HTTP.USER_AGENT, UserAgentSelector.randomUserAgent());
        }
    }

    protected static HttpHost determineTarget(HttpUriRequest request) throws ClientProtocolException {
        HttpHost target = null;
        URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = URIUtils.extractHost(requestURI);
            if (target == null) {
                throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
            }
        }
        return target;
    }

    @Override
    public DigestedResponse requestContent(URL url) throws IOException {
        return requestContent(url, null);
    }
  
    @Override
    public DigestedResponse requestContent(URL url, Charset charset) throws IOException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(INVALID_URL + url, ex);
        }
        return requestContent(uri, charset);
    }
  
    @Override
    public DigestedResponse requestContent(String uri) throws IOException {
        return requestContent(uri, null);
    }
  
    @Override
    public DigestedResponse requestContent(String uri, Charset charset) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }
  
    @Override
    public DigestedResponse requestContent(URI uri) throws IOException {
        return requestContent(uri, null);
    }
  
    @Override
    public DigestedResponse requestContent(URI uri, Charset charset) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }
  
    @Override
    public DigestedResponse requestContent(HttpGet httpGet) throws IOException {
        return requestContent(httpGet, null);
    }
  
    @Override
    public DigestedResponse requestContent(HttpGet httpGet, Charset charset) throws IOException {
        return DigestedResponseReader.requestContent(this, httpGet, charset);
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
        final HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }
  
    @Override
    public HttpEntity requestResource(URI uri) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }
  
    @Override
    public HttpEntity requestResource(HttpGet httpGet) throws IOException {
        return execute(httpGet).getEntity();
    }

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        prepareRequest(request);
        return httpClient.execute(request);
    }
    
    @Override
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        prepareRequest(request);
        return httpClient.execute(request, context);
    }
    
    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        prepareRequest(target, request);
        return httpClient.execute(target, request);
    }
    
    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        prepareRequest(request);
        return httpClient.execute(request, responseHandler);
    }
    
    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        prepareRequest(target, request);
        return httpClient.execute(target, request, context);
    }
    
    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        prepareRequest(request);
        return httpClient.execute(request, responseHandler, context);
    }
    
    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        prepareRequest(target, request);
        return httpClient.execute(target, request, responseHandler);
    }
    
    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        prepareRequest(target, request);
        return httpClient.execute(target, request, responseHandler, context);
    }
    
    @Override
    @Deprecated
    public ClientConnectionManager getConnectionManager() {
        return httpClient.getConnectionManager();
    }
    
    @Override
    @Deprecated
    public HttpParams getParams() {
        return httpClient.getParams();
    }

    @Override
    @Deprecated
    public void setProxy(String host, int port, String username, String password) {
        // must be done by HTTP client builder
    }

    @Override
    @Deprecated
    public void setTimeouts(int connectionTimeout, int socketTimeout) {
        // must be done by HTTP client builder
    }

    @Override
    public void close() throws IOException {
        if (httpClient instanceof Closeable) {
            ((Closeable)this.httpClient).close();
        }
    }
}
