# Assignment tasks

## Modules
- NHL teams (module: teams)
- UI Testing Playground (module: playground)

## Technologies
- RestAssured
- TestNG
- Selenide

Note: Playwright was tried but seemed more complicated comparing with Selenide.
Moreover, Playwright has less documentation and examples for Java than for NodeJS or JavaScript.

## Test Execution
Chrome web browser is used by default.

To use Firefox run the tests with:
```console
-Dselenide.browser=firefox
```

### Teams module
```console
mvn clean install -pl teams
```

### Playground module
```console
mvn clean install -pl playground
```

Note: `testProgressBar` test is flaky and may fail.
