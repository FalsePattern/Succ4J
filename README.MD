# Succ4J
See https://github.com/JimmyCushnie/SUCC for the official documentation. See the tests for examples.

# How to add as a dependency
## Maven
First, you need to create an access token with the read:packages permission.
<details><summary>Click this for a tutorial on how to get a token</summary>
<p>
Click on your profile -> Settings -> Developer settings -> Personal access tokens
-> Generate new token.
</p>
<p>
Under Note enter the name for your token (can be anything),
then you need to tick the read:packages entry, so that you can download github packages.
</p>
<p>
After that, click on Generate token, then copy the token code that the site generates,
and save it somewhere, because it won't be shown again.
</p>
</details>

After you have your token, open or create the file under the path `~/.m2/settings.xml` or `C:\Users\YOUR_USERNAME\.m2\settings.xml`
and add the following text:
```xml
<settings>
	<servers>
		<server>
			<id>Succ4J-mvn-repo</id>
			<username>USERNAME</username>
			<password>TOKEN</password>
		</server>
	</servers>
</settings>
```
Replace USERNAME with your github username, and TOKEN with the token key you just created. Save and close the file.
This allows maven to access github-hosted repositories.

Add the following repository into your project's pom.xml:
```xml
<repositories>
    <repository>
        <id>Succ4J-mvn-repo</id>
        <url>https://maven.pkg.github.com/FalsePattern/Succ4J/</url>
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
Head to the packages tab, select the latest version, and there you can download it as a separate .jar

# Changelog:
1.1.1: Fix security exception when trying to access protected or package-private fields

1.1.0+sources: Added source jar generation to maven packaging, removed releases, use packaged versions

1.1.0: Breaking refactors: Moved package from succ to com.github.falsepattern.succ4j

1.0.1: File auto-reload now works properly 

1.0: Initial release
