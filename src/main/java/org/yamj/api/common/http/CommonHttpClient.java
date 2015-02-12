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
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public interface CommonHttpClient extends HttpClient {

    @Deprecated
    void setProxy(String host, int port, String username, String password);

    @Deprecated
    void setTimeouts(int connectionTimeout, int socketTimeout);

    DigestedResponse requestContent(URL url) throws IOException;

    DigestedResponse requestContent(URL url, Charset charset) throws IOException;

    DigestedResponse requestContent(String uri) throws IOException;

    DigestedResponse requestContent(String uri, Charset charset) throws IOException;

    DigestedResponse requestContent(URI uri) throws IOException;

    DigestedResponse requestContent(URI uri, Charset charset) throws IOException;

    DigestedResponse requestContent(HttpGet httpGet) throws IOException;

    DigestedResponse requestContent(HttpGet httpGet, Charset charset) throws IOException;

    HttpEntity requestResource(URL url) throws IOException;

    HttpEntity requestResource(String uri) throws IOException;

    HttpEntity requestResource(URI uri) throws IOException;

    HttpEntity requestResource(HttpGet httpGet) throws IOException;

    HttpEntity postResource(URL url, HttpEntity entity) throws IOException;

    HttpEntity postResource(String uri, HttpEntity entity) throws IOException;

    HttpEntity postResource(URI uri, HttpEntity entity) throws IOException;

    HttpEntity postResource(HttpPost httpPost) throws IOException;

    HttpEntity deleteResource(URL url) throws IOException;

    HttpEntity deleteResource(String uri) throws IOException;

    HttpEntity deleteResource(URI uri) throws IOException;

    HttpEntity deleteResource(HttpDelete httpDelete) throws IOException;
}
