package com.moviejukebox.api.common.http;

import org.apache.http.client.protocol.*;
import org.apache.http.protocol.*;

import java.io.*;
import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;

public abstract class AbstractPoolingHttpClient extends AbstractHttpClient implements CommonHttpClient {

    private String proxyHost = null;
    private int proxyPort = 0;
    private String proxyUsername = null;
    private String proxyPassword = null;
    private int connectionTimeout = 25000;
    private int socketTimeout = 90000;
    private int connectionsMaxPerRoute = 1;
    private int connectionsMaxTotal = 20;
    
    public AbstractPoolingHttpClient(ClientConnectionManager connectionManager, HttpParams httpParams) {
        super(connectionManager, httpParams);
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

            String line;
            while ((line = br.readLine()) != null) {
                content.write(line);
            }

            content.flush();
            return content.toString();
        } finally {
            if (br != null) {
                try  {
                    br.close();
                } catch (Exception ignore) {}
            }
            if (isr != null) {
                try  {
                    isr.close();
                } catch (Exception ignore) {}
            }
            try  {
                content.close();
            } catch (Exception ignore) {}
            try  {
                is.close();
            } catch (Exception ignore) {}
        }
    }
    
    public void setRoute(HttpRoute httpRoute, int maxRequests) {
        ClientConnectionManager conMan = this.getConnectionManager();
        if (conMan instanceof PoolingClientConnectionManager) {
            PoolingClientConnectionManager poolMan = (PoolingClientConnectionManager)conMan;
            poolMan.setMaxPerRoute(httpRoute, maxRequests);
        }
    }
}