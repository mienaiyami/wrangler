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

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.ArrayList;
import java.util.List;

/**
 * The TimeDurationList class represents a list of time duration values with unit suffixes.
 * This type is associated with comma-separated time duration values.
 * Example: "100ms, 5s, 2m"
 */
@PublicEvolving
public class TimeDurationList implements Token {
  private final List<String> values;
  private final List<TimeDuration> durations;

  /**
   * Constructor for TimeDurationList from string representations.
   *
   * @param values List of time duration strings with unit suffixes
   */
  public TimeDurationList(List<String> values) {
    this.values = values;
    this.durations = new ArrayList<>();
    for (String value : values) {
      durations.add(new TimeDuration(value));
    }
  }

  /**
   * Returns the original string values
   *
   * @return List of time duration strings
   */
  @Override
  public List<String> value() {
    return values;
  }

  /**
   * Returns the list of parsed TimeDuration objects.
   *
   * @return List of TimeDuration objects
   */
  public List<TimeDuration> getTimeDurations() {
    return durations;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", "time-duration-list");
    JsonArray array = new JsonArray();
    for (String value : values) {
      array.add(new JsonPrimitive(value));
    }
    object.add("values", array);
    
    // Also add the parsed time durations
    JsonArray durationArray = new JsonArray();
    for (TimeDuration duration : durations) {
      durationArray.add(duration.toJson());
    }
    object.add("time-durations", durationArray);
    
    return object;
  }
} 
