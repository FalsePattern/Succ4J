# Succ4J
See https://github.com/JimmyCushnie/SUCC for the official documentation. See the tests for examples.

# How to add as a dependency
## Maven

Add the following repository into your project's pom.xml:
```xml
<repositories>
    <repository>
        <id>FalsePattern-maven-repo</id>
        <url>https://falsepattern.ddns.net/maven2/</url>
    </repository>
</repositories>
```
and then include Succ4J as a dependency:
```xml
<dependencies>
    <dependency>
        <groupId>com.github.falsepattern</groupId>
        <artifactId>succ4j</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```
Replace VERSION with the version you want to use

## Downloading as a standalone jar
Go to the [succ4j repository](https://falsepattern.ddns.net/maven2/com/github/falsepattern/succ4j/), choose your version, download succ4j-VERSION.jar and add it as a dependency to your project.

# Changelog:
1.2.0: Added annotation @GenericID, which can be used in generic classes to specify which generic type is used by a field based on an index (see ComplexChild.java in the tests for example)

1.1.3: Can now save fields of superclasses. Previously superclass data was lost due to the getDeclaredFields() not returning superclass fields.

1.1.2: Fix error when trying to save/load types containing arrays of references (example: List<Object[]>)

1.1.1: Fix security exception when trying to access protected or package-private fields

1.1.0+sources: Added source jar generation to maven packaging, removed releases, use packaged versions

1.1.0: Breaking refactors: Moved package from succ to com.github.falsepattern.succ4j

1.0.1: File auto-reload now works properly 

1.0: Initial release
