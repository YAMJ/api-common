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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.client.protocol.RequestTargetAuthentication;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPoolingHttpClient extends AbstractHttpClient implements CommonHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPoolingHttpClient.class);
    // Default settings for the connections
    private static final int DEFAULT_TIMEOUT_CONNECTION = 25000;
    private static final int DEFAULT_TIMEOUT_SOCKET = 90000;
    private static final int DEFAULT_CONN_ROUTE = 1;
    private static final int DEFAULT_CONN_MAX = 20;

    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private int connectionTimeout;
    private int socketTimeout;
    private int connectionsMaxPerRoute;
    private int connectionsMaxTotal;

    public AbstractPoolingHttpClient(ClientConnectionManager connectionManager, HttpParams httpParams) {
        super(connectionManager, httpParams);

        // Set the defaults for the proxy
        this.proxyHost = null;
        this.proxyPort = 0;
        this.proxyUsername = null;
        this.proxyPassword = null;

        // Set the defaults for the connections
        this.connectionTimeout = DEFAULT_TIMEOUT_CONNECTION;
        this.socketTimeout = DEFAULT_TIMEOUT_SOCKET;
        this.connectionsMaxPerRoute = DEFAULT_CONN_ROUTE;
        this.connectionsMaxTotal = DEFAULT_CONN_MAX;
    }

    @Override
    public void setProxy(String host, int port, String username, String password) {
        setProxyHost(host);
        setProxyPort(port);
        setProxyUsername(username);
        setProxyPassword(password);
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public void setTimeouts(int connectionTimeout, int socketTimeout) {
        setConnectionTimeout(connectionTimeout);
        setSocketTimeout(socketTimeout);
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionsMaxPerRoute(int connectionsMaxPerRoute) {
        this.connectionsMaxPerRoute = connectionsMaxPerRoute;
    }

    public void setConnectionsMaxTotal(int connectionsMaxTotal) {
        this.connectionsMaxTotal = connectionsMaxTotal;
    }

    @Override
    protected HttpParams createHttpParams() {
        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, Consts.UTF_8.name());
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        // set timeouts
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
        HttpConnectionParams.setSoTimeout(params, socketTimeout);

        // set default proxy
        if (StringUtils.isNotBlank(proxyHost) && proxyPort > 0) {
            if (StringUtils.isNotBlank(proxyUsername) && StringUtils.isNotBlank(proxyPassword)) {
                getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }

            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            ConnRouteParams.setDefaultProxy(params, proxy);
        }

        return params;
    }

    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new RequestDefaultHeaders());
        // Required protocol interceptors
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        // Recommended protocol interceptors
        httpproc.addInterceptor(new RequestClientConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        // HTTP state management interceptors
        httpproc.addInterceptor(new RequestAddCookies());
        httpproc.addInterceptor(new ResponseProcessCookies());
        // HTTP authentication interceptors
        httpproc.addInterceptor(new RequestAuthCache());
        httpproc.addInterceptor(new RequestTargetAuthentication());
        httpproc.addInterceptor(new RequestProxyAuthentication());
        return httpproc;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        PoolingClientConnectionManager clientManager = new PoolingClientConnectionManager();
        clientManager.setDefaultMaxPerRoute(connectionsMaxPerRoute);
        clientManager.setMaxTotal(connectionsMaxTotal);
        return clientManager;
    }

    protected String readContent(HttpResponse response, Charset charset) throws IOException {
        StringWriter content = new StringWriter(10 * 1024);
        InputStream is = response.getEntity().getContent();
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            if (charset == null) {
                isr = new InputStreamReader(is, Charset.defaultCharset());
            } else {
                isr = new InputStreamReader(is, charset);
            }
            br = new BufferedReader(isr);

            String line = br.readLine();
            while (line != null) {
                content.write(line);
                line = br.readLine();
            }

            content.flush();
            return content.toString();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    LOG.trace("Failed to close BufferedReader", ex);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ex) {
                    LOG.trace("Failed to close InputStreamReader", ex);
                }
            }
            try {
                content.close();
            } catch (IOException ex) {
                LOG.trace("Failed to close StringWriter", ex);
            }
            try {
                is.close();
            } catch (IOException ex) {
                LOG.trace("Failed to close InputStream", ex);
            }
        }
    }

    public void setRoute(HttpRoute httpRoute, int maxRequests) {
        ClientConnectionManager conMan = this.getConnectionManager();
        if (conMan instanceof PoolingClientConnectionManager) {
            PoolingClientConnectionManager poolMan = (PoolingClientConnectionManager) conMan;
            poolMan.setMaxPerRoute(httpRoute, maxRequests);
        }
    }
}
