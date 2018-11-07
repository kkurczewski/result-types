# Result type

Lightweight either-like type for Java

## Use case

It is intended in cases where:
* low-level exception need to be bubbled up to high-level endpoint of application
* type is expected to have one of three states: Present | Absent | Error
* original exception may not be dropped (unlike Java Optional API)
* user should be forced to deal with exception (unlike RuntimeException)
* solution may be simple (unlike bloated Functional Libs)