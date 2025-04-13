# TimeDuration

The `TimeDuration` class represents time duration values with unit suffixes (like "ms", "s", etc.) and provides methods to access the duration in various time units.

## Package

```
io.cdap.wrangler.api.parser
```

## Class Declaration

```java
@PublicEvolving
public class TimeDuration implements Token
```

## Description

The `TimeDuration` class is a token implementation that represents time durations with unit suffixes. It parses input strings like "100ms" or "5s" and provides methods to access the duration in various units (milliseconds, seconds, minutes, etc.).

When parsing a time duration string, the class extracts the numeric value and unit, then converts it to a canonical representation in milliseconds. This allows for consistent arithmetic operations and comparisons while maintaining the original format for display purposes.

## Constructor

### TimeDuration(String value)

```java
public TimeDuration(String value)
```

Constructs a new TimeDuration object from a string representation.

**Parameters:**

- `value` - String representation of a time duration with unit (e.g., "100ms", "5s")

**Throws:**

- `IllegalArgumentException` - If the input string is not a valid time duration

## Methods

### getMilliseconds()

```java
public long getMilliseconds()
```

Returns the time duration in milliseconds.

**Returns:**

- The duration in milliseconds

### getSeconds()

```java
public double getSeconds()
```

Returns the time duration in seconds.

**Returns:**

- The duration in seconds

### getMinutes()

```java
public double getMinutes()
```

Returns the time duration in minutes.

**Returns:**

- The duration in minutes

### getHours()

```java
public double getHours()
```

Returns the time duration in hours.

**Returns:**

- The duration in hours

### getDays()

```java
public double getDays()
```

Returns the time duration in days.

**Returns:**

- The duration in days

### value()

```java
@Override
public Object value()
```

Returns the original string value.

**Returns:**

- The original string representation of the time duration

### type()

```java
@Override
public TokenType type()
```

Returns the token type.

**Returns:**

- `TokenType.TIME_DURATION`

### toJson()

```java
@Override
public JsonElement toJson()
```

Returns a JSON representation of this object.

**Returns:**

- A JsonElement containing the type, original value, and milliseconds count

### toString()

```java
@Override
public String toString()
```

Returns the string representation of this object.

**Returns:**

- The original string representation of the time duration

## Supported Units

The following time units are supported:

| Unit | Description  | Factor (ms) |
|------|--------------|-------------|
| ms   | Milliseconds | 1           |
| s    | Seconds      | 1,000       |
| m    | Minutes      | 60,000      |
| h    | Hours        | 3,600,000   |
| d    | Days         | 86,400,000  |

## Examples

```java
// Create a TimeDuration object
TimeDuration duration = new TimeDuration("2.5m");

// Get the duration in different units
long ms = duration.getMilliseconds();  // 150000
double sec = duration.getSeconds();    // 150.0
double min = duration.getMinutes();    // 2.5
double hours = duration.getHours();    // 0.04166...
```

## Notes

- The class performs case-insensitive unit matching
- Spaces between the number and unit are allowed (e.g., "100 ms" is valid)
- All conversions are based on standard time unit definitions (60 seconds per minute, 60 minutes per hour, 24 hours per day)

## Related Classes

- [ByteSize](ByteSize.md) - Represents data size values with unit suffixes
- [TimeDurationList](TimeDurationList.md) - Represents a list of time duration values
