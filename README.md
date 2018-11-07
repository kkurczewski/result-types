# Result type

Lightweight either-like type for Java

## Use case

It is intended in cases where:
* low-level exception need to be bubbled up to high-level endpoint of application
* type is expected to have one of three states: Present | Absent | Error
* original exception may not be dropped (unlike Java Optional API)
* user should be forced to handle exception (unlike RuntimeException)
* solution must be simple (unlike bloated Functional Libs)

# Example

```java
// low-level call
Result<SQLException, List<String>> result = Result.of(() -> db.getUser("bob"));

// ... some processing
result.map(...)
    .flatMap(...)
    .peek(...)

try {
    Payment payment = result.unwrap();
} catch (SQLException e) {
    // handle error on top tier
}
    
```