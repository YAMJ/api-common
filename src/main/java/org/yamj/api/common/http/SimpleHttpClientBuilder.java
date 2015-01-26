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
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

public class SimpleHttpClientBuilder {

    private int maxConnTotal = 20;
    private int maxConnPerRoute = 1;
    private boolean systemProperties = false;
    private int connectionRequestTimeout = 15000;
    private int connectTimeout = 25000;
    private int socketTimeout = 90000;
    private String proxyHost;
    private int proxyPort = 0;
    private String proxyUsername;
    private String proxyPassword;
  
    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }
  
    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }
  
    public void setSystemProperties(boolean systemProperties) {
        this.systemProperties = systemProperties;
    }
  
    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }
  
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
  
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
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

    public CloseableHttpClient build() {
        // create proxy
        HttpHost proxy = null;
        CredentialsProvider credentialsProvider = null;
        
        if (StringUtils.isNotBlank(proxyHost) && proxyPort > 0) {
            proxy = new HttpHost(proxyHost, proxyPort);
          
            if (StringUtils.isNotBlank(proxyUsername) && StringUtils.isNotBlank(proxyPassword)) {
                if (systemProperties) {
                    credentialsProvider = new SystemDefaultCredentialsProvider();
                } else {
                    credentialsProvider = new BasicCredentialsProvider();
                }
                credentialsProvider.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }
        }
      
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnTotal(maxConnTotal)
                .setMaxConnPerRoute(maxConnPerRoute)
                .setProxy(proxy)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(connectionRequestTimeout)
                        .setConnectTimeout(connectTimeout)
                        .setSocketTimeout(socketTimeout)
                        .setProxy(proxy)
                        .build());
                

        // use system properties
        if (systemProperties) {
            builder.useSystemProperties();
        }

        // build the http client
        return builder.build();
    }
}
