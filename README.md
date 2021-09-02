# gradle-java-flavours [![Build Status]()]() [![Coverage Status]()]()


A Gradle plugin to add Android style flavours to a Java project

Improved version of [Java Gradle Flavours provided by UkLance](https://github.com/uklance/gradle-java-flavours)

## Usage:

```groovy
plugins {
  id "com.microsoft.javaflavours" version "1.0.0"
}
javaFlavours {
    flavour 'free'
    flavour 'paid'
    
    testJavaPathResolver = { String flavour -> "src/${flavour}-test/java" }
    testResourcesPathResolver = { String flavour -> "src/${flavour}-test/resources" }
}
dependencies {
    implementation         'aaa:aaa:1.0'
    freeImplementation     'bbb:bbb:2.0'
    freeTestImplementation 'ccc:ccc:3.0'
    paidRuntime            'ddd:ddd:4.0'
}
```

You find detailed installation instructions at https://plugins.gradle.org/plugin/com.microsoft.javaflavours.

## Directories:

- `src/main/java` - Common java sources
- `src/main/resources` - Common resources
- `src/test/java` - Common tests
- `src/test/resources` - Common test resources
- `src/<flavour>/java` - Flavour specific java sources (can be configured)
- `src/<flavour>/resources` - Flavour specific resources (can be configured)
- `src/<flavour>Test/java` - Flavour specific tests (can be configured)
- `src/<flavour>Test/resources` - Flavour specific test resources (can be configured)

## Tasks
- `implmentation<flavour>Java`
- `testImplmentation<flavour>Java`
- `compile<flavour>Java`
- `compile<flavour>TestJava`
- `<flavour>Classes`
- `<flavour>Jar`
- `<flavour>Test`
- `<flavour>TestClasses`
- `process<flavour>Resources`
- `process<flavour>TestResources`

## Configurations:

- `<flavour>Implementation`
- `<flavour>TestImplementation`
- `<flavour>Compile`
- `<flavour>CompileOnly`
- `<flavour>CompileClasspath`
- `<flavour>Runtime`
- `<flavour>TestCompile`
- `<flavour>TestCompileOnly`
- `<flavour>TestCompileClasspath`
- `<flavour>TestRuntime`

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft 
trademarks or logos is subject to and must follow 
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.
