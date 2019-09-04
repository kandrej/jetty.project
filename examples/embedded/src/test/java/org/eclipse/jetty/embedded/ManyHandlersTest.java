//
//  ========================================================================
//  Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.embedded;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class ManyHandlersTest
{
    private Server server;

    @BeforeEach
    public void startServer() throws Exception
    {
        server = ManyHandlers.createServer(0);
        server.start();
    }

    @AfterEach
    public void stopServer() throws Exception
    {
        server.stop();
    }

    @Test
    public void testGetParams() throws IOException
    {
        URI uri = server.getURI().resolve("/params?a=b&foo=bar");
        HttpURLConnection http = (HttpURLConnection)uri.toURL().openConnection();
        http.setRequestProperty("Accept-Encoding", "gzip");
        assertThat("HTTP Response Status", http.getResponseCode(), is(HttpURLConnection.HTTP_OK));

        // HttpUtil.dumpResponseHeaders(http);

        // test gzip
        HttpUtil.assertGzippedResponse(http);

        // test response content
        String responseBody = HttpUtil.getGzippedResponseBody(http);
        Object jsonObj = JSON.parse(responseBody);
        Map jsonMap = (Map)jsonObj;
        assertThat("Response JSON keys.size", jsonMap.keySet().size(), is(2));
    }

    @Test
    public void testGetHello() throws IOException
    {
        URI uri = server.getURI().resolve("/hello");
        HttpURLConnection http = (HttpURLConnection)uri.toURL().openConnection();
        http.setRequestProperty("Accept-Encoding", "gzip");
        assertThat("HTTP Response Status", http.getResponseCode(), is(HttpURLConnection.HTTP_OK));

        // HttpUtil.dumpResponseHeaders(http);

        // test gzip
        HttpUtil.assertGzippedResponse(http);

        // test expected header from wrapper
        String welcome = http.getHeaderField("X-Welcome");
        assertThat("X-Welcome header", welcome, containsString("Greetings from WelcomeWrapHandler"));

        // test response content
        String responseBody = HttpUtil.getGzippedResponseBody(http);
        assertThat("Response Content", responseBody, containsString("Hello"));
    }
}
