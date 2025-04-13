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
 * The ByteSize class represents data size values with unit suffixes like "KB", "MB", etc.
 * It provides methods to retrieve the value in various units.
 */
@PublicEvolving
public class ByteSize implements Token {
  private static final Pattern BYTE_SIZE_PATTERN = Pattern.compile("(\\d+)\\s*(B|KB?|MB?|GB?|TB?|PB?)", 
                                                                  Pattern.CASE_INSENSITIVE);
  private final String originalValue;
  private final long bytes;

  /**
   * Constructor for ByteSize.
   *
   * @param value String representation of byte size with unit (e.g., "100KB", "5MB")
   * @throws IllegalArgumentException if the input string is not a valid byte size
   */
  public ByteSize(String value) {
    this.originalValue = value;
    this.bytes = parseByteSize(value);
  }

  /**
   * Parses a byte size string with unit suffix into bytes.
   *
   * @param value String representation of byte size with unit (e.g., "100KB", "5MB")
   * @return Number of bytes
   * @throws IllegalArgumentException if the input string is not a valid byte size
   */
  private long parseByteSize(String value) {
    Matcher matcher = BYTE_SIZE_PATTERN.matcher(value.trim());
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    long size = Long.parseLong(matcher.group(1));
    String unit = matcher.group(2).toUpperCase();

    switch (unit) {
      case "B":
        return size;
      case "K":
      case "KB":
        return size * 1024;
      case "M":
      case "MB":
        return size * 1024 * 1024;
      case "G":
      case "GB":
        return size * 1024 * 1024 * 1024;
      case "T":
      case "TB":
        return size * 1024 * 1024 * 1024 * 1024;
      case "P":
      case "PB":
        return size * 1024 * 1024 * 1024 * 1024 * 1024;
      default:
        throw new IllegalArgumentException("Unsupported unit: " + unit);
    }
  }

  /**
   * @return The size in bytes
   */
  public long getBytes() {
    return bytes;
  }

  /**
   * @return The size in kilobytes
   */
  public double getKilobytes() {
    return bytes / 1024.0;
  }

  /**
   * @return The size in megabytes
   */
  public double getMegabytes() {
    return bytes / (1024.0 * 1024.0);
  }

  /**
   * @return The size in gigabytes
   */
  public double getGigabytes() {
    return bytes / (1024.0 * 1024.0 * 1024.0);
  }

  /**
   * @return The size in terabytes
   */
  public double getTerabytes() {
    return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
  }

  /**
   * @return The size in petabytes
   */
  public double getPetabytes() {
    return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
  }

  @Override
  public Object value() {
    return originalValue;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.add("type", new JsonPrimitive("byte-size"));
    object.add("value", new JsonPrimitive(originalValue));
    object.add("bytes", new JsonPrimitive(bytes));
    return object;
  }

  @Override
  public String toString() {
    return originalValue;
  }
} 
