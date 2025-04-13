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

import com.google.gson.JsonObject;

import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TokenType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ByteSize} class.
 */
public class ByteSizeTest {

    @Test
    public void testBytesUnit() {
        ByteSize size = new ByteSize("100B");
        Assert.assertEquals(100L, size.getBytes());
        Assert.assertEquals(100.0 / 1024.0, size.getKilobytes(), 0.0001);
        Assert.assertEquals(100.0 / (1024.0 * 1024.0), size.getMegabytes(), 0.0001);
    }

    @Test
    public void testKilobytesUnit() {
        ByteSize size = new ByteSize("10KB");
        Assert.assertEquals(10 * 1024L, size.getBytes());
        Assert.assertEquals(10.0, size.getKilobytes(), 0.0001);
        Assert.assertEquals(10.0 / 1024.0, size.getMegabytes(), 0.0001);

        // Test the K shorthand
        ByteSize sizeShort = new ByteSize("10K");
        Assert.assertEquals(10 * 1024L, sizeShort.getBytes());
    }

    @Test
    public void testMegabytesUnit() {
        ByteSize size = new ByteSize("1.5MB");
        long expectedBytes = (long) (1.5 * 1024 * 1024);
        Assert.assertEquals(expectedBytes, size.getBytes());
        Assert.assertEquals(1.5 * 1024, size.getKilobytes(), 0.0001);
        Assert.assertEquals(1.5, size.getMegabytes(), 0.0001);

        // Test the M shorthand
        ByteSize sizeShort = new ByteSize("1.5M");
        Assert.assertEquals(expectedBytes, sizeShort.getBytes());
    }

    @Test
    public void testGigabytesUnit() {
        ByteSize size = new ByteSize("2GB");
        long expectedBytes = 2L * 1024 * 1024 * 1024;
        Assert.assertEquals(expectedBytes, size.getBytes());
        Assert.assertEquals(2.0, size.getGigabytes(), 0.0001);

        // Test the G shorthand
        ByteSize sizeShort = new ByteSize("2G");
        Assert.assertEquals(expectedBytes, sizeShort.getBytes());
    }

    @Test
    public void testTerabytesUnit() {
        ByteSize size = new ByteSize("0.5TB");
        long expectedBytes = (long) (0.5 * 1024 * 1024 * 1024 * 1024);
        Assert.assertEquals(expectedBytes, size.getBytes());
        Assert.assertEquals(0.5, size.getTerabytes(), 0.0001);

        // Test the T shorthand
        ByteSize sizeShort = new ByteSize("0.5T");
        Assert.assertEquals(expectedBytes, sizeShort.getBytes());
    }

    @Test
    public void testPetabytesUnit() {
        ByteSize size = new ByteSize("0.01PB");
        double expectedBytes = 0.01 * 1024 * 1024 * 1024 * 1024 * 1024;
        Assert.assertEquals((long) expectedBytes, size.getBytes());
        Assert.assertEquals(0.01, size.getPetabytes(), 0.0001);

        // Test the P shorthand
        ByteSize sizeShort = new ByteSize("0.01P");
        Assert.assertEquals((long) expectedBytes, sizeShort.getBytes());
    }

    @Test
    public void testSpaceBetweenNumberAndUnit() {
        // Test with space between number and unit
        ByteSize size = new ByteSize("100 KB");
        Assert.assertEquals(100 * 1024L, size.getBytes());
    }

    @Test
    public void testCaseInsensitiveUnit() {
        // Test case-insensitive unit parsing
        ByteSize size1 = new ByteSize("100kb");
        ByteSize size2 = new ByteSize("100KB");
        Assert.assertEquals(size1.getBytes(), size2.getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFormat() {
        // Test invalid format (no unit)
        new ByteSize("100");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUnit() {
        // Test invalid unit
        new ByteSize("100XB");
    }

    @Test
    public void testValue() {
        // Test value() method
        String original = "100MB";
        ByteSize size = new ByteSize(original);
        Assert.assertEquals(original, size.value());
    }

    @Test
    public void testType() {
        // Test type() method
        ByteSize size = new ByteSize("100MB");
        Assert.assertEquals(TokenType.BYTE_SIZE, size.type());
    }

    @Test
    public void testToJson() {
        // Test toJson() method
        ByteSize size = new ByteSize("100MB");
        JsonObject json = size.toJson().getAsJsonObject();
        Assert.assertEquals("byte-size", json.get("type").getAsString());
        Assert.assertEquals("100MB", json.get("value").getAsString());
        Assert.assertEquals(100 * 1024 * 1024, json.get("bytes").getAsLong());
    }

    @Test
    public void testToString() {
        // Test toString() method
        String original = "100MB";
        ByteSize size = new ByteSize(original);
        Assert.assertEquals(original, size.toString());
    }
}
