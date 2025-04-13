# ByteSize

The `ByteSize` class represents data size values with unit suffixes (like "KB", "MB", etc.) and provides methods to access the size in various units.

## Package

```
io.cdap.wrangler.api.parser
```

## Class Declaration

```java
@PublicEvolving
public class ByteSize implements Token
```

## Description

The `ByteSize` class is a token implementation that represents byte sizes with unit suffixes. It parses input strings like "100KB" or "5MB" and provides methods to access the size in various units (bytes, kilobytes, megabytes, etc.).

When parsing a byte size string, the class extracts the numeric value and unit, then converts it to a canonical representation in bytes. This allows for consistent arithmetic operations and comparisons while maintaining the original format for display purposes.

## Constructor

### ByteSize(String value)

```java
public ByteSize(String value)
```

Constructs a new ByteSize object from a string representation.

**Parameters:**

- `value` - String representation of a byte size with unit (e.g., "100KB", "5MB")

**Throws:**

- `IllegalArgumentException` - If the input string is not a valid byte size

## Methods

### getBytes()

```java
public long getBytes()
```

Returns the byte size in bytes.

**Returns:**

- The size in bytes

### getKilobytes()

```java
public double getKilobytes()
```

Returns the byte size in kilobytes.

**Returns:**

- The size in kilobytes

### getMegabytes()

```java
public double getMegabytes()
```

Returns the byte size in megabytes.

**Returns:**

- The size in megabytes

### getGigabytes()

```java
public double getGigabytes()
```

Returns the byte size in gigabytes.

**Returns:**

- The size in gigabytes

### getTerabytes()

```java
public double getTerabytes()
```

Returns the byte size in terabytes.

**Returns:**

- The size in terabytes

### getPetabytes()

```java
public double getPetabytes()
```

Returns the byte size in petabytes.

**Returns:**

- The size in petabytes

### value()

```java
@Override
public Object value()
```

Returns the original string value.

**Returns:**

- The original string representation of the byte size

### type()

```java
@Override
public TokenType type()
```

Returns the token type.

**Returns:**

- `TokenType.BYTE_SIZE`

### toJson()

```java
@Override
public JsonElement toJson()
```

Returns a JSON representation of this object.

**Returns:**

- A JsonElement containing the type, original value, and byte count

### toString()

```java
@Override
public String toString()
```

Returns the string representation of this object.

**Returns:**

- The original string representation of the byte size

## Supported Units

The following byte units are supported:

| Unit | Description | Factor |
|------|-------------|--------|
| B    | Bytes       | 1      |
| KB, K| Kilobytes   | 1024   |
| MB, M| Megabytes   | 1024² |
| GB, G| Gigabytes   | 1024³ |
| TB, T| Terabytes   | 1024⁴ |
| PB, P| Petabytes   | 1024⁵ |

## Examples

```java
// Create a ByteSize object
ByteSize size = new ByteSize("100MB");

// Get the size in different units
long bytes = size.getBytes();        // 104857600
double kb = size.getKilobytes();     // 102400.0
double mb = size.getMegabytes();     // 100.0
double gb = size.getGigabytes();     // 0.09765625
```

## Notes

- The class performs case-insensitive unit matching
- The class uses binary (1024-based) unit conversions rather than decimal (1000-based)
- Unit shortcuts (K, M, G, T, P) are accepted in addition to full unit names (KB, MB, GB, TB, PB)
- Spaces between the number and unit are allowed (e.g., "100 MB" is valid)

## Related Classes

- [TimeDuration](TimeDuration.md) - Represents time duration values with unit suffixes
- [ByteSizeList](ByteSizeList.md) - Represents a list of byte size values
