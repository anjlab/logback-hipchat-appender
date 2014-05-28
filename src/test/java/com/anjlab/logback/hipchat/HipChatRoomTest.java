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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HipChatRoomTest
{
    private static final Logger logger = LoggerFactory.getLogger(HipChatRoomTest.class);
    
    @Test
    public void driverForLogError() throws InterruptedException
    {
        logger.error("Test Error Message", new RuntimeException("Test Exception"));
        Thread.sleep(1000); //  Wait for AsyncAppender
    }
    
    @Test
    public void driverForLongLogError() throws InterruptedException
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < HipChatMessage.MAX_MESSAGE_LENGTH; i++)
        {
            builder.append(i);
        }
        logger.error(builder.toString(), new RuntimeException("Test Exception"));
        Thread.sleep(5000); //  Wait for AsyncAppender
    }
    
    @Test
    public void driverForLogInfo() throws InterruptedException
    {
        logger.info("Test Info");
        Thread.sleep(1000); //  Wait for AsyncAppender
    }
}
