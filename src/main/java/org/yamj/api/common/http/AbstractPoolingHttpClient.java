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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.protocol.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.apache.http.protocol.RequestExpectContinue;

@Deprecated
public abstract class AbstractPoolingHttpClient extends AbstractHttpClient implements CommonHttpClient {

    // Default settings for the connections
    private static final int DEFAULT_TIMEOUT_CONNECTION = 25000;
    private static final int DEFAULT_TIMEOUT_SOCKET = 90000;
    private static final int DEFAULT_CONN_ROUTE = 1;
    private static final int DEFAULT_CONN_MAX = 20;
    private static final int SOCKET_BUFFER_8K = 8192;

    // Variables
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
    public final void setProxy(final String host, final int port, final String username, final String password) {
        setProxyHost(host);
        setProxyPort(port);
        setProxyUsername(username);
        setProxyPassword(password);
    }

    public final void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public final void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public final void setProxyUsername(final String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public final void setProxyPassword(final String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public final void setTimeouts(final int connectionTimeout, final int socketTimeout) {
        setConnectionTimeout(connectionTimeout);
        setSocketTimeout(socketTimeout);
    }

    public final void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public final void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public final void setConnectionsMaxPerRoute(final int connectionsMaxPerRoute) {
        this.connectionsMaxPerRoute = connectionsMaxPerRoute;
    }

    public final void setConnectionsMaxTotal(final int connectionsMaxTotal) {
        this.connectionsMaxTotal = connectionsMaxTotal;
    }

    @Override
    protected HttpParams createHttpParams() {
        final HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, Consts.UTF_8.name());
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_8K);

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

            final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
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
        final PoolingClientConnectionManager clientManager = new PoolingClientConnectionManager();
        clientManager.setDefaultMaxPerRoute(connectionsMaxPerRoute);
        clientManager.setMaxTotal(connectionsMaxTotal);
        return clientManager;
    }

    public void setRoute(final HttpRoute httpRoute, final int maxRequests) {
        final ClientConnectionManager conMan = this.getConnectionManager();
        if (conMan instanceof PoolingClientConnectionManager) {
            PoolingClientConnectionManager poolMan = (PoolingClientConnectionManager) conMan;
            poolMan.setMaxPerRoute(httpRoute, maxRequests);
        }
    }
}
