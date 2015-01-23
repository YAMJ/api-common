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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class HttpClientWrapper implements HttpClient {

    private static final String INVALID_URL = "Invalid URL ";
    private static final int HTTP_STATUS_503 = 503;
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientWrapper.class);

    private final HttpClient httpClient;
    protected boolean randomUserAgent = false;
    
    
    public HttpClientWrapper(HttpClient httpCLient) {
        this.httpClient = httpCLient;
    }
    
    public final void setRandomUserAgent(boolean randomUserAgent) {
      this.randomUserAgent = randomUserAgent;
  }

    public DigestedResponse requestContent(URL url) throws IOException {
        return requestContent(url, null);
    }
  
    public DigestedResponse requestContent(URL url, Charset charset) throws IOException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(INVALID_URL + url, ex);
        }
  
        return requestContent(uri, charset);
    }
  
    public DigestedResponse requestContent(String uri) throws IOException {
        return requestContent(uri, null);
    }
  
    public DigestedResponse requestContent(String uri, Charset charset) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }
  
    public DigestedResponse requestContent(URI uri) throws IOException {
        return requestContent(uri, null);
    }
  
    public DigestedResponse requestContent(URI uri, Charset charset) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestContent(httpGet, charset);
    }
  
    public DigestedResponse requestContent(HttpGet httpGet) throws IOException {
        return requestContent(httpGet, null);
    }
  
    public DigestedResponse requestContent(HttpGet httpGet, Charset charset) throws IOException {
        if (randomUserAgent) {
            httpGet.setHeader(HTTP.USER_AGENT, UserAgentSelector.randomUserAgent());
        }
  
        try {
            return DigestedResponseReader.readContent(execute(httpGet), charset);
        } catch (ConnectTimeoutException | SocketTimeoutException ex) {
            LOG.trace("Timeout exception", ex);
            
            httpGet.releaseConnection();
            // a timeout should result in a 503 error
            // to signal that the service is temporarily not available
            return new DigestedResponse(HTTP_STATUS_503, StringUtils.EMPTY);
        } catch (IOException ioe) {
            httpGet.releaseConnection();
            throw ioe;
        }
    }
  
    public HttpEntity requestResource(URL url) throws IOException {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(INVALID_URL + url, ex);
        }
  
        return requestResource(uri);
    }
  
    public HttpEntity requestResource(String uri) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }
  
    public HttpEntity requestResource(URI uri) throws IOException {
        final HttpGet httpGet = new HttpGet(uri);
        return requestResource(httpGet);
    }
  
    public HttpEntity requestResource(HttpGet httpGet) throws IOException {
        if (randomUserAgent) {
            httpGet.setHeader(HTTP.USER_AGENT, UserAgentSelector.randomUserAgent());
        }
        return execute(httpGet).getEntity();
    }

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        return httpClient.execute(request);
    }

    @Override
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return httpClient.execute(request, context);
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        return httpClient.execute(target, request);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return httpClient.execute(request, responseHandler);
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return httpClient.execute(target, request, context);
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        return httpClient.execute(request, responseHandler, context);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return httpClient.execute(target, request, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
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
}
