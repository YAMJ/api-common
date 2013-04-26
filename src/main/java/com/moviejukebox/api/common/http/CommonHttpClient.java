package com.moviejukebox.api.common.http;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.http.client.HttpClient;

public interface CommonHttpClient extends HttpClient {

    String requestContent(String uri) throws IOException;

    String requestContent(String uri, Charset charset) throws IOException;
}