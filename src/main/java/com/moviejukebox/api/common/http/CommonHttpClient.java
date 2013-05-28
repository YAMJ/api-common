package com.moviejukebox.api.common.http;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.client.HttpClient;

public interface CommonHttpClient extends HttpClient {

    void setProxy(String host, int port, String username, String password);

    void setTimeouts(int connectionTimeout, int socketTimeout);
    
    String requestContent(URL url) throws IOException;

    String requestContent(URL url, Charset charset) throws IOException;

    String requestContent(String uri) throws IOException;

    String requestContent(String uri, Charset charset) throws IOException;

    String requestContent(URI uri) throws IOException;

    String requestContent(URI uri, Charset charset) throws IOException;
}