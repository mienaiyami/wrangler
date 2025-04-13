# Aggregate Units

The `aggregate-units` directive aggregates (totals or averages) byte sizes and time durations across multiple rows, with support for unit conversion in the output.

## Syntax

```
aggregate-units <size-column> <time-column> <target-size-column> <target-time-column> [<size-unit>] [<time-unit>] [<aggregation-type>]
```

* `<size-column>` - Column containing byte size values (required)
* `<time-column>` - Column containing time duration values (required)
* `<target-size-column>` - Column where the aggregated size will be stored (required)
* `<target-time-column>` - Column where the aggregated time will be stored (required)
* `<size-unit>` - Unit for the output size value (optional, defaults to 'MB')
  * Valid values: 'B', 'KB', 'MB', 'GB', 'TB', 'PB'
* `<time-unit>` - Unit for the output time value (optional, defaults to 's')
  * Valid values: 'ms', 's', 'm', 'h', 'd'
* `<aggregation-type>` - Type of aggregation to perform (optional, defaults to 'total')
  * Valid values: 'total', 'average'

## Usage Notes

The `aggregate-units` directive processes all rows in the input data, accumulating byte sizes and time durations from the specified columns. It then outputs a single row containing the aggregated values in the specified units.

This directive is particularly useful for:

* Calculating total file sizes or storage requirements
* Measuring total or average processing times
* Analyzing resource usage across multiple operations
* Generating summary statistics for reports

### Input Handling

The directive can handle various input formats for byte sizes and time durations:

* Native `ByteSize` and `TimeDuration` objects
* String representations with unit suffixes (e.g., "100MB", "5s")
* Numeric values (assumed to be in bytes for size and milliseconds for time)

### State Management

The directive maintains state between batches using the `TransientStore` mechanism, allowing it to work correctly in both streaming and batch processing modes.

### Output Mode

In the final processing phase, the directive outputs a single row containing the aggregated values. The original rows are not included in the output.

## Examples

### Basic Example

Using this input data:

```
| file_name | file_size | processing_time |
|-----------|-----------|-----------------|
| file1.txt | 100KB     | 500ms           |
| file2.txt | 2MB       | 1s              |
| file3.txt | 5MB       | 2.5s            |
```

Applying this directive:

```
aggregate-units :file_size :processing_time :total_size_mb :total_time_sec 'MB' 's' 'total'
```

Would produce:

```
| total_size_mb | total_time_sec |
|---------------|----------------|
| 7.098         | 4.0            |
```

The total size is calculated as (100KB + 2MB + 5MB) converted to MB, and the total time is calculated as (500ms + 1s + 2.5s) converted to seconds.

### Average Calculation Example

Using this input data:

```
| server | mem_usage | response_time |
|--------|-----------|---------------|
| srv1   | 100MB     | 30s           |
| srv2   | 200MB     | 90s           |
| srv3   | 300MB     | 120s          |
```

Applying this directive:

```
aggregate-units :mem_usage :response_time :avg_mem_gb :avg_time_min 'GB' 'm' 'average'
```

Would produce:

```
| avg_mem_gb | avg_time_min |
|------------|--------------|
| 0.195      | 1.333        |
```

The average memory usage is calculated as avg(100MB, 200MB, 300MB) converted to GB, and the average response time is calculated as avg(30s, 90s, 120s) converted to minutes.

### Multi-Batch Processing Example

When processing data in multiple batches, the directive accumulates the values across all batches and produces the final aggregated result at the end of processing.

## Related Concepts

* [Unit Tokens in Wrangler Grammar](../grammar/unit-tokens.md)
* [ByteSize Class](../reference/ByteSize.md)
* [TimeDuration Class](../reference/TimeDuration.md)
