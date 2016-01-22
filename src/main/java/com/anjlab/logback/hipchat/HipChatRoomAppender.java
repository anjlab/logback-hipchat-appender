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

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

import com.anjlab.logback.hipchat.HipChatMessage.Color;
import com.anjlab.logback.hipchat.HipChatMessage.MessageFormat;
import com.anjlab.logback.hipchat.LineChunkenizer.ChunkCallback;

public class HipChatRoomAppender<E> extends AppenderBase<E>
{
    private Layout<E> layout;
    private String room;
    private String apiKey;
    private Color color = Color.red;
    private boolean notify = true;
    private boolean printChunkHeader = true;
    
    private HipChatRoom hipChatRoom;
    
    @Override
    public void start()
    {
        boolean hasErrors = guardNotNull(layout, "Layout")
                         || guardNotNull(room, "HipChat room")
                         || guardNotNull(apiKey, "HipChat room API key")
                         || guardNotNull(color, "Color");
        
        if (hasErrors)
        {
            return;
        }
        
        hipChatRoom = new HipChatRoom(room, apiKey);
        super.start();
    }
    
    private boolean guardNotNull(Object value, String name)
    {
        if (value == null)
        {
            addError(name + " == null or not specified, but is a mandatory");
        }
        return value == null;
    }

    @Override
    public void stop()
    {
        super.stop();
        IOUtils.closeQuietly(hipChatRoom);
    }
    
    private final AtomicLong eventCounter = new AtomicLong();
    
    @Override
    protected void append(E eventObject)
    {
        final long eventId = eventCounter.incrementAndGet();
        
        String rawMessage = layout.doLayout(eventObject);
        
        int maxWrapperLength = 100;
        
        final int maxChunkSize = HipChatMessage.MAX_MESSAGE_LENGTH - maxWrapperLength;
        
        new LineChunkenizer(rawMessage, maxChunkSize).chunkenize(new ChunkCallback()
        {
            private int chunkId = 0;
            
            @Override
            public void gotChunk(String chunk, boolean hasMoreChunks)
            {
                chunkId++;
                
                //  Wrap chunk with HTML (see maxWrapperLength above)
                StringBuilder htmlMessage = new StringBuilder(maxChunkSize);
                if (printChunkHeader) {
                    htmlMessage
                            .append("<b>")
                            .append(eventId)
                            .append(':')
                            .append(hasMoreChunks
                                    ? chunkId
                                    : chunkId == 1
                                    ? "single"
                                    : chunkId)
                            .append(hasMoreChunks
                                    ? "+more"
                                    : chunkId > 1
                                    ? "-last"
                                    : "")
                            .append("</b>");
                }
                htmlMessage
                    .append("<pre>")
                    .append(chunk)
                    .append("</pre>");
                
                hipChatRoom.sendMessage(
                        new HipChatMessage(
                                htmlMessage.toString(),
                                MessageFormat.html,
                                color,
                                notify));
            }
        });
    }

    public void setLayout(Layout<E> layout)
    {
        this.layout = layout;
    }
    public void setRoom(String room)
    {
        this.room = room;
    }
    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }
    public void setColor(Color color)
    {
        this.color = color;
    }
    public void setNotify(boolean notify)
    {
        this.notify = notify;
    }
    public void setPrintChunkHeader(boolean printChunkHeader)
    {
        this.printChunkHeader = printChunkHeader;
    }
}
