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

import java.io.*;
import java.nio.charset.Charset;
import org.apache.http.HttpResponse;

public class DigestedResponseReader {

    private static final int SW_BUFFER_10K = 10240;

    private DigestedResponseReader() {
        throw new UnsupportedOperationException("Class cannot be instantiated");
    }

    public static DigestedResponse readContent(final HttpResponse response, final Charset charset) throws IOException {
        final DigestedResponse digestedResponse = new DigestedResponse();
        digestedResponse.setStatusCode(response.getStatusLine().getStatusCode());
    
        if (response.getEntity() != null) {
            try (StringWriter content = new StringWriter(SW_BUFFER_10K);
                 InputStream is = response.getEntity().getContent();
                 InputStreamReader isr = new InputStreamReader(is, (charset == null ? Charset.defaultCharset() : charset));
                 BufferedReader br = new BufferedReader(isr))
            {
                String line = br.readLine();
                while (line != null) {
                    content.write(line);
                    line = br.readLine();
                }
    
                content.flush();
                digestedResponse.setContent(content.toString());
            }
        }
        return digestedResponse;
    }
}
