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

import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link TimeDuration} class.
 */
public class TimeDurationTest {

    @Test
    public void testMillisecondsUnit() {
        TimeDuration duration = new TimeDuration("500ms");
        Assert.assertEquals(500L, duration.getMilliseconds());
        Assert.assertEquals(0.5, duration.getSeconds(), 0.0001);
        Assert.assertEquals(0.5 / 60.0, duration.getMinutes(), 0.0001);
    }

    @Test
    public void testSecondsUnit() {
        TimeDuration duration = new TimeDuration("2.5s");
        long expectedMs = (long) (2.5 * 1000);
        Assert.assertEquals(expectedMs, duration.getMilliseconds());
        Assert.assertEquals(2.5, duration.getSeconds(), 0.0001);
        Assert.assertEquals(2.5 / 60.0, duration.getMinutes(), 0.0001);
    }

    @Test
    public void testMinutesUnit() {
        TimeDuration duration = new TimeDuration("5m");
        long expectedMs = 5 * 60 * 1000;
        Assert.assertEquals(expectedMs, duration.getMilliseconds());
        Assert.assertEquals(5.0 * 60.0, duration.getSeconds(), 0.0001);
        Assert.assertEquals(5.0, duration.getMinutes(), 0.0001);
        Assert.assertEquals(5.0 / 60.0, duration.getHours(), 0.0001);
    }

    @Test
    public void testHoursUnit() {
        TimeDuration duration = new TimeDuration("1.5h");
        long expectedMs = (long) (1.5 * 60 * 60 * 1000);
        Assert.assertEquals(expectedMs, duration.getMilliseconds());
        Assert.assertEquals(1.5 * 60 * 60, duration.getSeconds(), 0.0001);
        Assert.assertEquals(1.5 * 60, duration.getMinutes(), 0.0001);
        Assert.assertEquals(1.5, duration.getHours(), 0.0001);
    }

    @Test
    public void testDaysUnit() {
        TimeDuration duration = new TimeDuration("2d");
        long expectedMs = 2 * 24 * 60 * 60 * 1000;
        Assert.assertEquals(expectedMs, duration.getMilliseconds());
        Assert.assertEquals(2.0 * 24.0 * 60.0 * 60.0, duration.getSeconds(), 0.0001);
        Assert.assertEquals(2.0 * 24.0 * 60.0, duration.getMinutes(), 0.0001);
        Assert.assertEquals(2.0 * 24.0, duration.getHours(), 0.0001);
        Assert.assertEquals(2.0, duration.getDays(), 0.0001);
    }

    @Test
    public void testSpaceBetweenNumberAndUnit() {
        // Test with space between number and unit
        TimeDuration duration = new TimeDuration("100 ms");
        Assert.assertEquals(100L, duration.getMilliseconds());
    }

    @Test
    public void testCaseInsensitiveUnit() {
        // Test case-insensitive unit parsing
        TimeDuration duration1 = new TimeDuration("100MS");
        TimeDuration duration2 = new TimeDuration("100ms");
        Assert.assertEquals(duration1.getMilliseconds(), duration2.getMilliseconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFormat() {
        // Test invalid format (no unit)
        new TimeDuration("100");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUnit() {
        // Test invalid unit
        new TimeDuration("100xs");
    }

    @Test
    public void testValue() {
        // Test value() method
        String original = "500ms";
        TimeDuration duration = new TimeDuration(original);
        Assert.assertEquals(original, duration.value());
    }

    @Test
    public void testType() {
        // Test type() method
        TimeDuration duration = new TimeDuration("500ms");
        Assert.assertEquals(TokenType.TIME_DURATION, duration.type());
    }

    @Test
    public void testToJson() {
        // Test toJson() method
        TimeDuration duration = new TimeDuration("2.5s");
        JsonObject json = duration.toJson().getAsJsonObject();
        Assert.assertEquals("time-duration", json.get("type").getAsString());
        Assert.assertEquals("2.5s", json.get("value").getAsString());
        Assert.assertEquals(2500, json.get("milliseconds").getAsLong());
    }

    @Test
    public void testToString() {
        // Test toString() method
        String original = "2.5s";
        TimeDuration duration = new TimeDuration(original);
        Assert.assertEquals(original, duration.toString());
    }
}
