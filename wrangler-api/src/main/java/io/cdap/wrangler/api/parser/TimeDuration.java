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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TimeDuration class represents time duration values with unit suffixes like "ms", "s", etc.
 * It provides methods to retrieve the value in various time units.
 */
@PublicEvolving
public class TimeDuration implements Token {
  private static final Pattern TIME_DURATION_PATTERN = Pattern.compile("(\\d+)\\s*(ms|s|m|h|d)", 
                                                                     Pattern.CASE_INSENSITIVE);
  private final String originalValue;
  private final long milliseconds;

  /**
   * @param value String representation of time duration with unit (e.g., "100ms", "5s")
   * @throws IllegalArgumentException if the input string is not a valid time duration
   */
  public TimeDuration(String value) {
    this.originalValue = value;
    this.milliseconds = parseTimeDuration(value);
  }

  /**
   * Parses a time duration string with unit suffix into milliseconds.
   *
   * @param value String representation of time duration with unit (e.g., "100ms", "5s")
   * @return Number of milliseconds
   * @throws IllegalArgumentException if the input string is not a valid time duration
   */
  private long parseTimeDuration(String value) {
    Matcher matcher = TIME_DURATION_PATTERN.matcher(value.trim());
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    long duration = Long.parseLong(matcher.group(1));
    String unit = matcher.group(2).toLowerCase();

    switch (unit) {
      case "ms":
        return duration;
      case "s":
        return duration * 1000;
      case "m":
        return duration * 60 * 1000;
      case "h":
        return duration * 60 * 60 * 1000;
      case "d":
        return duration * 24 * 60 * 60 * 1000;
      default:
        throw new IllegalArgumentException("Unsupported unit: " + unit);
    }
  }

  /**
   * Returns the time duration in milliseconds.
   *
   * @return The duration in milliseconds
   */
  public long getMilliseconds() {
    return milliseconds;
  }

  /**
   * Returns the time duration in seconds.
   *
   * @return The duration in seconds
   */
  public double getSeconds() {
    return milliseconds / 1000.0;
  }

  /**
   * Returns the time duration in minutes.
   *
   * @return The duration in minutes
   */
  public double getMinutes() {
    return milliseconds / (60.0 * 1000.0);
  }

  /**
   * Returns the time duration in hours.
   *
   * @return The duration in hours
   */
  public double getHours() {
    return milliseconds / (60.0 * 60.0 * 1000.0);
  }

  /**
   * Returns the time duration in days.
   *
   * @return The duration in days
   */
  public double getDays() {
    return milliseconds / (24.0 * 60.0 * 60.0 * 1000.0);
  }

  @Override
  public Object value() {
    return originalValue;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.add("type", new JsonPrimitive("time-duration"));
    object.add("value", new JsonPrimitive(originalValue));
    object.add("milliseconds", new JsonPrimitive(milliseconds));
    return object;
  }

  @Override
  public String toString() {
    return originalValue;
  }
} 
