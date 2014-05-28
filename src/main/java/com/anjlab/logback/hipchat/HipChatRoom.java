/**
 * Copyright 2014 AnjLab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anjlab.logback.hipchat;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import com.google.gson.Gson;

public class HipChatRoom implements Closeable
{
    private final CloseableHttpClient closeableHttpClient;
    private final Gson gson;
    
    private final String roomUri;
    private final Header authorizationHeader;
    private final Header contentTypeHeader;
    
    public HipChatRoom(String room, String apiKey)
    {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        
        closeableHttpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        
        gson = new Gson();
        
        roomUri = "https://api.hipchat.com/v2/room/" + room + "/notification";
        authorizationHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    }
    
    /**
     * https://www.hipchat.com/docs/apiv2/method/send_room_notification
     * 
     * @param message
     */
    public void sendMessage(HipChatMessage message)
    {
        String json = gson.toJson(message);
        
        HttpPost request = new HttpPost(roomUri);
        request.addHeader(contentTypeHeader);
        request.addHeader(authorizationHeader);
        request.setEntity(new StringEntity(json, Charset.defaultCharset()));
        
        CloseableHttpResponse response = null;
        try
        {
            response = closeableHttpClient.execute(request);
            
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NO_CONTENT)
            {
                logError(message, response.getStatusLine());
            }
        }
        catch (IOException e)
        {
            logError(message, e);
        }
        finally
        {
            IOUtils.closeQuietly(response);
        }
    }

    private void logError(HipChatMessage message, Object cause)
    {
        //  TODO Use SLF4J logger if invoked not from HipChatRoomAppender
        System.err.println("Error sending HipChat message: " + message.message + "\nCaused by: " + cause);
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }
    
    @Override
    public void close() throws IOException
    {
        closeableHttpClient.close();
    }
}