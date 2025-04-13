# Unit Tokens in Wrangler Grammar

Wrangler now supports specialized token types for working with byte sizes and time durations. These tokens allow for more straightforward handling of units and unit conversions in data preparation recipes.

## BYTE_SIZE Tokens

The `BYTE_SIZE` token represents data size values with unit suffixes. This makes it easier to work with file sizes, memory allocations, and other byte-oriented measurements.

### Syntax

```
<number><unit>
```

Where `<unit>` can be one of:

- `B` - Bytes
- `KB` or `K` - Kilobytes
- `MB` or `M` - Megabytes
- `GB` or `G` - Gigabytes
- `TB` or `T` - Terabytes
- `PB` or `P` - Petabytes

### Examples

```
100B      // 100 bytes
1.5KB     // 1.5 kilobytes
256MB     // 256 megabytes
2GB       // 2 gigabytes
1TB       // 1 terabyte
0.5PB     // 0.5 petabytes
```

## TIME_DURATION Tokens

The `TIME_DURATION` token represents time interval values with unit suffixes. This makes it easier to work with durations, intervals, and other time-based measurements.

### Syntax

```
<number><unit>
```

Where `<unit>` can be one of:

- `ms` - Milliseconds
- `s` - Seconds
- `m` - Minutes
- `h` - Hours
- `d` - Days

### Examples

```
100ms     // 100 milliseconds
1.5s      // 1.5 seconds
30m       // 30 minutes
2h        // 2 hours
1d        // 1 day
```

## Grammar Implementation

These tokens are implemented in the Wrangler grammar (Directives.g4) with the following lexer rules:

```antlr
BYTE_SIZE
 : Int BYTE_UNIT
 ;

TIME_DURATION
 : Int TIME_UNIT
 ;

fragment BYTE_UNIT
 : 'B'
 | 'KB' | 'K'
 | 'MB' | 'M'
 | 'GB' | 'G'
 | 'TB' | 'T'
 | 'PB' | 'P'
 ;

fragment TIME_UNIT
 : 'ms'
 | 's'
 | 'm'
 | 'h'
 | 'd'
 ;
```

## Usage in Recipes

These tokens can be used directly in recipe directives that support byte size or time duration arguments:

```
// Example directive using byte size
set-threshold :column 100MB

// Example directive using time duration
set-timeout :process 30s
```

## Conversion Support

When these tokens are parsed, they are converted to canonical units (bytes for BYTE_SIZE and milliseconds for TIME_DURATION), which makes them suitable for arithmetic operations and comparisons.

For example, "1.5KB" is internally represented as 1536 bytes, and "2m" is internally represented as 120000 milliseconds. This allows for precise calculations while maintaining human-readable representations in the recipe code.
