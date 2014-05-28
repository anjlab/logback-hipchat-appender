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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.anjlab.logback.hipchat.LineChunkenizer.ChunkCallback;

public class LineChunkenizerTest
{
    private static class ChunkCollector implements ChunkCallback
    {
        public final List<String> chunks = new ArrayList<String>();
        public final List<Boolean> hasMoreChunks = new ArrayList<Boolean>();
        
        @Override
        public void gotChunk(String chunk, boolean hasMoreChunks)
        {
            this.chunks.add(chunk);
            this.hasMoreChunks.add(hasMoreChunks);
        }
    }
    
    @Test
    public void testChunksForLongLine()
    {
        String input = "1234567890";
        LineChunkenizer chunkenizer = new LineChunkenizer(input, 3);
        ChunkCollector collector = new ChunkCollector();
        chunkenizer.chunkenize(collector);
        Assert.assertEquals(4, collector.chunks.size());
        Assert.assertEquals(Arrays.asList("123", "456", "789", "0"), collector.chunks);
        Assert.assertEquals(Arrays.asList(true, true, true, false), collector.hasMoreChunks);
    }
    
    @Test
    public void testChunksForMultilines()
    {
        String input = "123\n456\n789\n0";
        LineChunkenizer chunkenizer = new LineChunkenizer(input, 8);
        ChunkCollector collector = new ChunkCollector();
        chunkenizer.chunkenize(collector);
        Assert.assertEquals(2, collector.chunks.size());
        Assert.assertEquals(Arrays.asList("123\n456", "789\n0"), collector.chunks);
        Assert.assertEquals(Arrays.asList(true, false), collector.hasMoreChunks);
    }
    

    @Test
    public void testChunksForMultilinesWithLongLinesInBetween()
    {
        String input = "123\n1234567890\n123\n0";
        LineChunkenizer chunkenizer = new LineChunkenizer(input, 8);
        ChunkCollector collector = new ChunkCollector();
        chunkenizer.chunkenize(collector);
        Assert.assertEquals(4, collector.chunks.size());
        Assert.assertEquals(Arrays.asList("123", "12345678", "90", "123\n0"), collector.chunks);
        Assert.assertEquals(Arrays.asList(true, true, true, false), collector.hasMoreChunks);
    }
}
