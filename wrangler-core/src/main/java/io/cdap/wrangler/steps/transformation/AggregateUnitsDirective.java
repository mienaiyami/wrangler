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

package io.cdap.wrangler.steps.transformation;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ErrorRowException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Optional;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.TransientStore;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * A directive for aggregating byte sizes and time durations with unit
 * conversions.
 * 
 * This directive allows users to aggregate (total or average) byte sizes and
 * time durations
 * across multiple rows, with optional unit conversion for the output.
 */
@Plugin(type = Directive.TYPE)
@Name("aggregate-units")
@Description("Aggregates byte sizes and time durations with optional unit conversion")
public class AggregateUnitsDirective implements Directive {

  private static final String TRANSIENT_STORE_SIZE_KEY = "aggregate-units.size.total";
  private static final String TRANSIENT_STORE_TIME_KEY = "aggregate-units.time.total";
  private static final String TRANSIENT_STORE_COUNT_KEY = "aggregate-units.count";

  private String sizeColumnName;
  private String timeColumnName;
  private String targetSizeColumnName;
  private String targetTimeColumnName;
  private String sizeUnit; // Output unit for size: B, KB, MB, GB, TB, PB
  private String timeUnit; // Output unit for time: ms, s, m, h, d
  private String aggregationType; // total or average

  /**
   * Define the usage of the directive.
   * 
   * @return {@link UsageDefinition} for this directive.
   */
  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-units");
    builder.define("size-column", TokenType.COLUMN_NAME);
    builder.define("time-column", TokenType.COLUMN_NAME);
    builder.define("target-size-column", TokenType.COLUMN_NAME);
    builder.define("target-time-column", TokenType.COLUMN_NAME);
    builder.define("size-unit", TokenType.TEXT, Optional.TRUE);
    builder.define("time-unit", TokenType.TEXT, Optional.TRUE);
    builder.define("aggregation", TokenType.TEXT, Optional.TRUE);
    return builder.build();
  }

  /**
   * Initialize the directive with the specified arguments.
   * 
   * @param arguments Arguments parsed for this directive.
   * @throws DirectiveParseException if the arguments are invalid.
   */
  @Override
  public void initialize(Arguments arguments) throws DirectiveParseException {
    this.sizeColumnName = ((ColumnName) arguments.value("size-column")).value();
    this.timeColumnName = ((ColumnName) arguments.value("time-column")).value();
    this.targetSizeColumnName = ((ColumnName) arguments.value("target-size-column")).value();
    this.targetTimeColumnName = ((ColumnName) arguments.value("target-time-column")).value();

    // Default to MB and seconds if not specified
    if (arguments.contains("size-unit")) {
      this.sizeUnit = ((Text) arguments.value("size-unit")).value();
    } else {
      this.sizeUnit = "MB";
    }

    // Validate size unit
    if (!isValidSizeUnit(sizeUnit)) {
      throw new DirectiveParseException(
          String.format("Invalid size unit '%s'. Valid units are: B, KB, MB, GB, TB, PB", sizeUnit));
    }

    if (arguments.contains("time-unit")) {
      this.timeUnit = ((Text) arguments.value("time-unit")).value();
    } else {
      this.timeUnit = "s";
    }

    // Validate time unit
    if (!isValidTimeUnit(timeUnit)) {
      throw new DirectiveParseException(
          String.format("Invalid time unit '%s'. Valid units are: ms, s, m, h, d", timeUnit));
    }

    if (arguments.contains("aggregation")) {
      this.aggregationType = ((Text) arguments.value("aggregation")).value();
    } else {
      this.aggregationType = "total";
    }

    // Validate aggregation type
    if (!aggregationType.equals("total") && !aggregationType.equals("average")) {
      throw new DirectiveParseException(
          String.format("Invalid aggregation type '%s'. Valid types are: total, average", aggregationType));
    }
  }

  /**
   * Checks if the provided size unit is valid.
   * 
   * @param sizeUnit The size unit to validate
   * @return true if valid, false otherwise
   */
  private boolean isValidSizeUnit(String sizeUnit) {
    return sizeUnit.equals("B") || sizeUnit.equals("KB") || sizeUnit.equals("MB") ||
        sizeUnit.equals("GB") || sizeUnit.equals("TB") || sizeUnit.equals("PB");
  }

  /**
   * Checks if the provided time unit is valid.
   * 
   * @param timeUnit The time unit to validate
   * @return true if valid, false otherwise
   */
  private boolean isValidTimeUnit(String timeUnit) {
    return timeUnit.equals("ms") || timeUnit.equals("s") || timeUnit.equals("m") ||
        timeUnit.equals("h") || timeUnit.equals("d");
  }

  /**
   * Execute the directive on the provided list of rows.
   * 
   * @param rows    List of rows to be processed.
   * @param context ExecutorContext for the directive.
   * @return List of rows after processing by the directive.
   * @throws DirectiveExecutionException if there's an error during execution.
   * @throws ErrorRowException           if there's an issue with a specific row.
   */
  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context)
      throws DirectiveExecutionException, ErrorRowException {

    // Get the transient store for maintaining state between batches
    TransientStore store = context.getTransientStore();

    // Initialize totals if not already set
    if (!store.getVariables().contains(TRANSIENT_STORE_SIZE_KEY)) {
      store.set(null, TRANSIENT_STORE_SIZE_KEY, 0L);
    }
    if (!store.getVariables().contains(TRANSIENT_STORE_TIME_KEY)) {
      store.set(null, TRANSIENT_STORE_TIME_KEY, 0L);
    }
    if (!store.getVariables().contains(TRANSIENT_STORE_COUNT_KEY)) {
      store.set(null, TRANSIENT_STORE_COUNT_KEY, 0);
    }

    // Get current totals
    long totalSize = store.get(TRANSIENT_STORE_SIZE_KEY);
    long totalTime = store.get(TRANSIENT_STORE_TIME_KEY);
    int count = store.get(TRANSIENT_STORE_COUNT_KEY);

    List<Row> results = new ArrayList<>();

    // Process each row, accumulating totals
    for (Row row : rows) {
      if (row.find(sizeColumnName) != -1 && row.find(timeColumnName) != -1) {
        try {
          Object sizeObj = row.getValue(sizeColumnName);
          Object timeObj = row.getValue(timeColumnName);

          long sizeBytes = 0;
          long timeMs = 0;

          // Handle ByteSize objects or string representations
          if (sizeObj instanceof ByteSize) {
            sizeBytes = ((ByteSize) sizeObj).getBytes();
          } else if (sizeObj instanceof String) {
            sizeBytes = new ByteSize((String) sizeObj).getBytes();
          } else if (sizeObj instanceof Number) {
            sizeBytes = ((Number) sizeObj).longValue();
          }

          // Handle TimeDuration objects or string representations
          if (timeObj instanceof TimeDuration) {
            timeMs = ((TimeDuration) timeObj).getMilliseconds();
          } else if (timeObj instanceof String) {
            timeMs = new TimeDuration((String) timeObj).getMilliseconds();
          } else if (timeObj instanceof Number) {
            timeMs = ((Number) timeObj).longValue();
          }

          // Accumulate totals
          totalSize += sizeBytes;
          totalTime += timeMs;
          count++;

          // Add the row to the result set (original row will be passed through)
          results.add(row);

        } catch (Exception e) {
          // Skip rows with parsing issues
          results.add(row);
        }
      } else {
        // If columns don't exist, still include the row in output
        results.add(row);
      }
    }

    // Update the store with new totals
    store.set(null, TRANSIENT_STORE_SIZE_KEY, totalSize);
    store.set(null, TRANSIENT_STORE_TIME_KEY, totalTime);
    store.set(null, TRANSIENT_STORE_COUNT_KEY, count);

    // If no rows were processed, return the input rows
    if (rows.isEmpty()) {
      return rows;
    }

    // On the last batch, compute final values and add a summary row
    if (context.getEnvironment() == ExecutorContext.Environment.TRANSFORM) {
      // Create a new row with the aggregated values
      Row summaryRow = new Row();

      double finalSize;
      double finalTime;

      // Apply aggregation based on type
      if (aggregationType.equals("average") && count > 0) {
        finalSize = (double) totalSize / count;
        finalTime = (double) totalTime / count;
      } else {
        finalSize = totalSize;
        finalTime = totalTime;
      }

      // Convert to requested units
      double convertedSize = convertSize(finalSize, sizeUnit);
      double convertedTime = convertTime(finalTime, timeUnit);

      // Add to summary row
      summaryRow.add(targetSizeColumnName, convertedSize);
      summaryRow.add(targetTimeColumnName, convertedTime);

      // Replace results with just the summary row for final output
      results = new ArrayList<>();
      results.add(summaryRow);

      // Clear the store for next pipeline run
      store.set(null, TRANSIENT_STORE_SIZE_KEY, 0L);
      store.set(null, TRANSIENT_STORE_TIME_KEY, 0L);
      store.set(null, TRANSIENT_STORE_COUNT_KEY, 0);
    }

    return results;
  }

  /**
   * Convert a byte size to the specified unit.
   * 
   * @param bytes Size in bytes
   * @param unit  Target unit (B, KB, MB, GB, TB, PB)
   * @return Converted size in the requested unit
   */
  private double convertSize(double bytes, String unit) {
    switch (unit) {
      case "B":
        return bytes;
      case "KB":
        return bytes / 1024.0;
      case "MB":
        return bytes / (1024.0 * 1024.0);
      case "GB":
        return bytes / (1024.0 * 1024.0 * 1024.0);
      case "TB":
        return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
      case "PB":
        return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
      default:
        return bytes / (1024.0 * 1024.0);
    }
  }

  /**
   * Convert a time duration to the specified unit.
   * 
   * @param milliseconds Time in milliseconds
   * @param unit         Target unit (ms, s, m, h, d)
   * @return Converted time in the requested unit
   */
  private double convertTime(double milliseconds, String unit) {
    switch (unit) {
      case "ms":
        return milliseconds;
      case "s":
        return milliseconds / 1000.0;
      case "m":
        return milliseconds / (60.0 * 1000.0);
      case "h":
        return milliseconds / (60.0 * 60.0 * 1000.0);
      case "d":
        return milliseconds / (24.0 * 60.0 * 60.0 * 1000.0);
      default:
        return milliseconds / 1000.0;
    }
  }

  /**
   * Destroys any resources held by this directive.
   * This method is called during cleanup.
   */
  @Override
  public void destroy() {
    // Nothing to clean up for this directive
  }
}
