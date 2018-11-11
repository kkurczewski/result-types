# Result types

Result types allows lazy propagation of checked-exception in Java.

## Use case

It is intended in cases where:
* low-level exception need to be bubbled up to high-level endpoint of application
* type is expected to have one of three states: Present | Absent | Error
* original exception may not be dropped (unlike Java Optional API)
* user should be aware of exception (unlike RuntimeException)
* solution must be simple (unlike bloated Functional Libs)

# Pseudo-code

```java

Optional<User> getUser(String name) throws CheckedException;

OptionalResult<CheckedException, User> result = OptionalResult.of(() -> db.getUser());

result
    .map(User::getMovies)
    .map(Movie::getTitle)
    .flatMap(...)
    .peek(...)
    // ... more processing

try {
    Optional<Recommendations> recommendations = result.unwrapOpt();
} catch(CheckedException e) {
    // ... handle error on top tier
}
```