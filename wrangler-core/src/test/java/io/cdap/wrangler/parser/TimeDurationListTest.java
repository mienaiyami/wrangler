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

import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TimeDurationList;
import io.cdap.wrangler.api.parser.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link TimeDurationList} class.
 */
public class TimeDurationListTest {

    @Test
    public void testConstructor() {
        List<String> values = Arrays.asList("100ms", "2s", "3m");
        TimeDurationList durationList = new TimeDurationList(values);

        // Test that the values are stored correctly
        Assert.assertEquals(values, durationList.value());

        // Test that TimeDuration objects are created correctly
        List<TimeDuration> timeDurations = durationList.getTimeDurations();
        Assert.assertEquals(3, timeDurations.size());

        Assert.assertEquals(100L, timeDurations.get(0).getMilliseconds());
        Assert.assertEquals(2 * 1000L, timeDurations.get(1).getMilliseconds());
        Assert.assertEquals(3 * 60 * 1000L, timeDurations.get(2).getMilliseconds());
    }

    @Test
    public void testType() {
        List<String> values = Arrays.asList("100ms", "2s");
        TimeDurationList durationList = new TimeDurationList(values);

        // Token type should be TIME_DURATION
        Assert.assertEquals(TokenType.TIME_DURATION, durationList.type());
    }

    @Test
    public void testToJson() {
        List<String> values = Arrays.asList("100ms", "2s");
        TimeDurationList durationList = new TimeDurationList(values);

        JsonObject json = durationList.toJson().getAsJsonObject();

        // Check that the type is correct
        Assert.assertEquals("time-duration-list", json.get("type").getAsString());

        // Check that the values array is correct
        JsonArray valuesArray = json.getAsJsonArray("values");
        Assert.assertEquals(2, valuesArray.size());
        Assert.assertEquals("100ms", valuesArray.get(0).getAsString());
        Assert.assertEquals("2s", valuesArray.get(1).getAsString());

        // Check that the time-durations array is correct
        JsonArray durationsArray = json.getAsJsonArray("time-durations");
        Assert.assertEquals(2, durationsArray.size());

        // First TimeDuration
        JsonObject firstDuration = durationsArray.get(0).getAsJsonObject();
        Assert.assertEquals("time-duration", firstDuration.get("type").getAsString());
        Assert.assertEquals("100ms", firstDuration.get("value").getAsString());
        Assert.assertEquals(100L, firstDuration.get("milliseconds").getAsLong());

        // Second TimeDuration
        JsonObject secondDuration = durationsArray.get(1).getAsJsonObject();
        Assert.assertEquals("time-duration", secondDuration.get("type").getAsString());
        Assert.assertEquals("2s", secondDuration.get("value").getAsString());
        Assert.assertEquals(2 * 1000L, secondDuration.get("milliseconds").getAsLong());
    }
}
