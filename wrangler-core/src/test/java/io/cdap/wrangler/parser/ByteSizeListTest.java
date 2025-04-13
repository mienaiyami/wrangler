/*
 * Copyright © 2023 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ByteSizeList;
import io.cdap.wrangler.api.parser.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link ByteSizeList} class.
 */
public class ByteSizeListTest {

    @Test
    public void testConstructor() {
        List<String> values = Arrays.asList("100KB", "2MB", "3GB");
        ByteSizeList sizeList = new ByteSizeList(values);

        // Test that the values are stored correctly
        Assert.assertEquals(values, sizeList.value());

        // Test that ByteSize objects are created correctly
        List<ByteSize> byteSizes = sizeList.getByteSizes();
        Assert.assertEquals(3, byteSizes.size());

        Assert.assertEquals(100 * 1024L, byteSizes.get(0).getBytes());
        Assert.assertEquals(2 * 1024 * 1024L, byteSizes.get(1).getBytes());
        Assert.assertEquals(3 * 1024 * 1024 * 1024L, byteSizes.get(2).getBytes());
    }

    @Test
    public void testType() {
        List<String> values = Arrays.asList("100KB", "2MB");
        ByteSizeList sizeList = new ByteSizeList(values);

        // Token type should be BYTE_SIZE
        Assert.assertEquals(TokenType.BYTE_SIZE, sizeList.type());
    }

    @Test
    public void testToJson() {
        List<String> values = Arrays.asList("100KB", "2MB");
        ByteSizeList sizeList = new ByteSizeList(values);

        JsonObject json = sizeList.toJson().getAsJsonObject();

        // Check that the type is correct
        Assert.assertEquals("byte-size-list", json.get("type").getAsString());

        // Check that the values array is correct
        JsonArray valuesArray = json.getAsJsonArray("values");
        Assert.assertEquals(2, valuesArray.size());
        Assert.assertEquals("100KB", valuesArray.get(0).getAsString());
        Assert.assertEquals("2MB", valuesArray.get(1).getAsString());

        // Check that the byte-sizes array is correct
        JsonArray byteSizesArray = json.getAsJsonArray("byte-sizes");
        Assert.assertEquals(2, byteSizesArray.size());

        // First ByteSize
        JsonObject firstByteSize = byteSizesArray.get(0).getAsJsonObject();
        Assert.assertEquals("byte-size", firstByteSize.get("type").getAsString());
        Assert.assertEquals("100KB", firstByteSize.get("value").getAsString());
        Assert.assertEquals(100 * 1024L, firstByteSize.get("bytes").getAsLong());

        // Second ByteSize
        JsonObject secondByteSize = byteSizesArray.get(1).getAsJsonObject();
        Assert.assertEquals("byte-size", secondByteSize.get("type").getAsString());
        Assert.assertEquals("2MB", secondByteSize.get("value").getAsString());
        Assert.assertEquals(2 * 1024 * 1024L, secondByteSize.get("bytes").getAsLong());
    }
}
