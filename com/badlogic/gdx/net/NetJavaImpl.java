/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.net;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetJavaImpl {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    final ObjectMap<Net.HttpRequest, HttpURLConnection> connections = new ObjectMap();
    final ObjectMap<Net.HttpRequest, Net.HttpResponseListener> listeners = new ObjectMap();
    final Lock lock = new ReentrantLock();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendHttpRequest(final Net.HttpRequest httpRequest, final Net.HttpResponseListener httpResponseListener) {
        if (httpRequest.getUrl() == null) {
            httpResponseListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
            return;
        }
        try {
            URL url;
            String method = httpRequest.getMethod();
            if (method.equalsIgnoreCase("GET")) {
                String queryString = "";
                String value = httpRequest.getContent();
                if (value != null && !"".equals(value)) {
                    queryString = "?" + value;
                }
                url = new URL(httpRequest.getUrl() + queryString);
            } else {
                url = new URL(httpRequest.getUrl());
            }
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            final boolean doingOutPut = method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT");
            connection.setDoOutput(doingOutPut);
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            HttpURLConnection.setFollowRedirects(httpRequest.getFollowRedirects());
            this.lock.lock();
            this.connections.put(httpRequest, connection);
            this.listeners.put(httpRequest, httpResponseListener);
            this.lock.unlock();
            for (Map.Entry<String, String> header : httpRequest.getHeaders().entrySet()) {
                connection.addRequestProperty(header.getKey(), header.getValue());
            }
            connection.setConnectTimeout(httpRequest.getTimeOut());
            connection.setReadTimeout(httpRequest.getTimeOut());
            this.executorService.submit(new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    try {
                        if (doingOutPut) {
                            String contentAsString = httpRequest.getContent();
                            if (contentAsString != null) {
                                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                                try {
                                    writer.write(contentAsString);
                                }
                                finally {
                                    StreamUtils.closeQuietly(writer);
                                }
                            }
                            InputStream contentAsStream = httpRequest.getContentStream();
                            if (contentAsStream != null) {
                                OutputStream os = connection.getOutputStream();
                                try {
                                    StreamUtils.copyStream(contentAsStream, os);
                                }
                                finally {
                                    StreamUtils.closeQuietly(os);
                                }
                            }
                        }
                        connection.connect();
                        HttpClientResponse clientResponse = new HttpClientResponse(connection);
                        try {
                            NetJavaImpl.this.lock.lock();
                            Net.HttpResponseListener listener = NetJavaImpl.this.listeners.get(httpRequest);
                            if (listener != null) {
                                listener.handleHttpResponse(clientResponse);
                                NetJavaImpl.this.listeners.remove(httpRequest);
                            }
                            NetJavaImpl.this.connections.remove(httpRequest);
                        }
                        finally {
                            connection.disconnect();
                            NetJavaImpl.this.lock.unlock();
                        }
                    }
                    catch (Exception e) {
                        connection.disconnect();
                        NetJavaImpl.this.lock.lock();
                        try {
                            httpResponseListener.failed(e);
                        }
                        finally {
                            NetJavaImpl.this.connections.remove(httpRequest);
                            NetJavaImpl.this.listeners.remove(httpRequest);
                            NetJavaImpl.this.lock.unlock();
                        }
                    }
                }
            });
        }
        catch (Exception e) {
            this.lock.lock();
            try {
                httpResponseListener.failed(e);
            }
            finally {
                this.connections.remove(httpRequest);
                this.listeners.remove(httpRequest);
                this.lock.unlock();
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancelHttpRequest(Net.HttpRequest httpRequest) {
        try {
            this.lock.lock();
            Net.HttpResponseListener httpResponseListener = this.listeners.get(httpRequest);
            if (httpResponseListener != null) {
                httpResponseListener.cancelled();
                this.connections.remove(httpRequest);
                this.listeners.remove(httpRequest);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    static class HttpClientResponse
    implements Net.HttpResponse {
        private HttpURLConnection connection;
        private HttpStatus status;

        public HttpClientResponse(HttpURLConnection connection) throws IOException {
            this.connection = connection;
            try {
                this.status = new HttpStatus(connection.getResponseCode());
            }
            catch (IOException e) {
                this.status = new HttpStatus(-1);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte[] getResult() {
            InputStream input = this.getInputStream();
            try {
                byte[] arrby = StreamUtils.copyStreamToByteArray(input, this.connection.getContentLength());
                return arrby;
            }
            catch (IOException e) {
                byte[] arrby = StreamUtils.EMPTY_BYTES;
                return arrby;
            }
            finally {
                StreamUtils.closeQuietly(input);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getResultAsString() {
            InputStream input = this.getInputStream();
            try {
                String string = StreamUtils.copyStreamToString(input, this.connection.getContentLength());
                return string;
            }
            catch (IOException e) {
                String string = "";
                return string;
            }
            finally {
                StreamUtils.closeQuietly(input);
            }
        }

        @Override
        public InputStream getResultAsStream() {
            return this.getInputStream();
        }

        @Override
        public HttpStatus getStatus() {
            return this.status;
        }

        @Override
        public String getHeader(String name) {
            return this.connection.getHeaderField(name);
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            return this.connection.getHeaderFields();
        }

        private InputStream getInputStream() {
            try {
                return this.connection.getInputStream();
            }
            catch (IOException e) {
                return this.connection.getErrorStream();
            }
        }
    }

}

