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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

@Deprecated
public class DefaultPoolingHttpClient extends AbstractPoolingHttpClient {

    protected boolean randomUserAgent = false;

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

    public final void setRandomUserAgent(boolean randomUserAgent) {
        this.randomUserAgent = randomUserAgent;
    }

    @Override
    public DigestedResponse requestContent(URL url) throws IOException {
        return requestContent(url, null);
    }

    @Override
    public DigestedResponse requestContent(URL url, Charset charset) throws IOException {
        return requestContent(toURI(url), charset);
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
        prepareRequest(httpGet);
        return DigestedResponseReader.requestContent(this, httpGet, charset);
    }

    @Override
    public DigestedResponse postContent(URL url, HttpEntity entity) throws IOException {
        return postContent(url, entity, null);
    }

    @Override
    public DigestedResponse postContent(URL url, HttpEntity entity, Charset charset) throws IOException {
        return postContent(toURI(url), entity, charset);
    }

    @Override
    public DigestedResponse postContent(String uri, HttpEntity entity) throws IOException {
        return postContent(uri, entity, null);
    }

    @Override
    public DigestedResponse postContent(String uri, HttpEntity entity, Charset charset) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postContent(httpPost, charset);
    }

    @Override
    public DigestedResponse postContent(URI uri, HttpEntity entity) throws IOException {
        return postContent(uri, entity, null);
    }

    @Override
    public DigestedResponse postContent(URI uri, HttpEntity entity, Charset charset) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postContent(httpPost, charset);
    }

    @Override
    public DigestedResponse postContent(HttpPost httpPost) throws IOException {
        return postContent(httpPost, null);
    }

    @Override
    public DigestedResponse postContent(HttpPost httpPost, Charset charset) throws IOException {
        prepareRequest(httpPost);
        return DigestedResponseReader.postContent(this, httpPost, charset);
    }

    @Override
    public DigestedResponse deleteContent(URL url) throws IOException {
        return deleteContent(url, null);
    }

    @Override
    public DigestedResponse deleteContent(URL url, Charset charset) throws IOException {
        return deleteContent(toURI(url), charset);
    }

    @Override
    public DigestedResponse deleteContent(String uri) throws IOException {
        return deleteContent(uri, null);
    }

    @Override
    public DigestedResponse deleteContent(String uri, Charset charset) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteContent(httpDelete, charset);
    }

    @Override
    public DigestedResponse deleteContent(URI uri) throws IOException {
        return deleteContent(uri, null);
    }

    @Override
    public DigestedResponse deleteContent(URI uri, Charset charset) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteContent(httpDelete, charset);
    }

    @Override
    public DigestedResponse deleteContent(HttpDelete httpDelete) throws IOException {
        return deleteContent(httpDelete, null);
    }

    @Override
    public DigestedResponse deleteContent(HttpDelete httpDelete, Charset charset) throws IOException {
        prepareRequest(httpDelete);
        return DigestedResponseReader.deleteContent(this, httpDelete, charset);
    }

    @Override
    public HttpEntity requestResource(URL url) throws IOException {
        return requestResource(toURI(url));
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
        prepareRequest(httpGet);
        return execute(httpGet).getEntity();
    }

    @Override
    public HttpEntity postResource(URL url, HttpEntity entity) throws IOException {
        return postResource(toURI(url), entity);
    }

    @Override
    public HttpEntity postResource(String uri, HttpEntity entity) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postResource(httpPost);
    }

    @Override
    public HttpEntity postResource(URI uri, HttpEntity entity) throws IOException {
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        return postResource(httpPost);
    }

    @Override
    public HttpEntity postResource(HttpPost httpPost) throws IOException {
        prepareRequest(httpPost);
        return execute(httpPost).getEntity();
    }

    @Override
    public HttpEntity deleteResource(URL url) throws IOException {
        return deleteResource(toURI(url));
    }

    @Override
    public HttpEntity deleteResource(String uri) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteResource(httpDelete);
    }

    @Override
    public HttpEntity deleteResource(URI uri) throws IOException {
        final HttpDelete httpDelete = new HttpDelete(uri);
        return deleteResource(httpDelete);
    }

    @Override
    public HttpEntity deleteResource(HttpDelete httpDelete) throws IOException {
        prepareRequest(httpDelete);
        return execute(httpDelete).getEntity();
    }

    protected void prepareRequest(HttpRequestBase request) {
        if (randomUserAgent) {
            request.setHeader(HTTP.USER_AGENT, UserAgentSelector.randomUserAgent());
        }
    }
    
    protected static URI toURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL: " + url, ex);
        }
    }
}
