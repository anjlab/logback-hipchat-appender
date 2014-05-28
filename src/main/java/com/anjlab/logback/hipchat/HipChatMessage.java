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

import com.google.gson.annotations.SerializedName;


public class HipChatMessage
{
    public static final int MAX_MESSAGE_LENGTH = 10000;
    
    public final String message;
    @SerializedName("message_format")
    public final MessageFormat format;
    public final Color color;
    public final boolean notify;
    
    public HipChatMessage(String message)
    {
        this(message, MessageFormat.html, Color.yellow, false);
    }
    
    public HipChatMessage(String message, MessageFormat format, Color color, boolean notify)
    {
        this.message = message != null && message.length() > MAX_MESSAGE_LENGTH
                     ? message.substring(0, MAX_MESSAGE_LENGTH)
                     : message;
        
        this.format = format;
        this.color = color;
        this.notify = notify;
    }
    
    //  Names of enums are in lower-case because it's easier to serialize them to JSON
    //  with default Gson configuration: they will appear in lower-case in output JSON
    //  as it's required by HipChat API
    
    public enum Color
    {
        yellow, red, green, purple, gray, random;
    }
    
    public enum MessageFormat
    {
        html, text;
    }

}