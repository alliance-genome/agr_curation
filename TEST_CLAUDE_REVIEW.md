# Test PR for Claude Code review simplification

This file tests the simplified Claude Code review workflow.

## Changes
- Added test file with multiple lines
- Verifying PR size calculation works correctly
- Ensuring auto-review triggers with DB tunnel
- Testing the split auto-review/interactive pattern

## Notes
This file should be deleted after the test is complete.
The workflow should detect >3 lines changed and trigger a full review.

## Fake code section
```java
public class TestEntity {
    private Long id;
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
