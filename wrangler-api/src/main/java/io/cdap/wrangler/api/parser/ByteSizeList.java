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
 * The ByteSizeList class represents a list of byte size values with unit suffixes.
 * This type is associated with comma-separated byte size values.
 * Example: "10KB, 5MB, 2GB"
 */
@PublicEvolving
public class ByteSizeList implements Token {
  private final List<String> values;
  private final List<ByteSize> byteSizes;

  /**
   * @param values List of byte size strings with unit suffixes
   */
  public ByteSizeList(List<String> values) {
    this.values = values;
    this.byteSizes = new ArrayList<>();
    for (String value : values) {
      byteSizes.add(new ByteSize(value));
    }
  }

  /**
   * Returns the original string values.
   *
   * @return List of byte size strings
   */
  @Override
  public List<String> value() {
    return values;
  }

  /**
   * Returns the list of parsed ByteSize objects.
   *
   * @return List of ByteSize objects
   */
  public List<ByteSize> getByteSizes() {
    return byteSizes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", "byte-size-list");
    JsonArray array = new JsonArray();
    for (String value : values) {
      array.add(new JsonPrimitive(value));
    }
    object.add("values", array);
    
    // Also add the parsed byte sizes
    JsonArray byteSizeArray = new JsonArray();
    for (ByteSize byteSize : byteSizes) {
      byteSizeArray.add(byteSize.toJson());
    }
    object.add("byte-sizes", byteSizeArray);
    
    return object;
  }
} 
