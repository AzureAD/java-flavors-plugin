// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package com.microsoft.javaflavours

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import static org.junit.Assert.assertEquals

class JavaFlavoursPluginTest extends Specification {

	@Rule
	final TemporaryFolder testProjectDir = new TemporaryFolder()

	def setup() {
		writeFile('gradle.properties', getResourceUrl("testkit-gradle.properties").text)
	}

	URL getResourceUrl(String path) {
		URL url = getClass().classLoader.getResource(path)
		if (url == null) throw new RuntimeException("No such resource $path")
		return url
	}

	void writeFile(String path, String text) {
		File file = new File(testProjectDir.root, path)
		file.parentFile.mkdirs()
		file.text = text
	}

	void assertZipEntries(String zipPath, List<String> expectedEntries) {
		File zipFile = new File(testProjectDir.root, zipPath)
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))
		Set<String> actualEntries = [] as Set
		ZipEntry entry
		while ((entry = zipIn.getNextEntry()) != null) {
			if (!entry.isDirectory()) {
				actualEntries << entry.name
			}
		}
		assertEquals(expectedEntries as Set, actualEntries)
	}

	def "Test tasks"() {
		given:
		writeFile("build.gradle", """
				plugins {
					id 'com.microsoft.javaflavours'					
				}
				version = '1.0-SNAPSHOT'
				repositories {
					mavenCentral()
				}
				javaFlavours {
					flavour 'one'
					flavour 'two'
				}
				task performAssertions {
					doLast {
						Set<String> taskNames = tasks.collect { it.name } as Set
						def expected = [
							'compileOneJava', 'compileOneTestJava', 'oneClasses', 'oneJar', 'oneTest', 'oneTestClasses', 'processOneResources', 'processOneTestResources', 
							'compileTwoJava', 'compileTwoTestJava', 'twoClasses', 'twoJar', 'twoTest', 'twoTestClasses', 'processTwoResources', 'processTwoTestResources'
						]
						expected.each { taskName ->
							assert taskNames.contains(taskName)
						}
					}
				}
			""")
		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments('performAssertions', '--stacktrace')
				.withPluginClasspath()
				.build()
		then:
		result.task(":performAssertions").outcome == TaskOutcome.SUCCESS
	}

	def "Test configurations"() {
		given:
		writeFile("build.gradle", """
				plugins {
					id 'com.microsoft.javaflavours'
				}
				version = '1.0-SNAPSHOT'
				repositories {
					mavenCentral()
				}
				javaFlavours {
					flavour 'one'
					flavour 'two'
				}
				dependencies {
					compile 'org.springframework:spring-context:4.3.2.RELEASE'
					testCompile 'junit:junit:4.12'
					oneCompile 'org.hibernate:hibernate-core:5.2.8.Final'
					oneTestCompile 'org.mockito:mockito-core:2.7.12'
				}
				task performAssertions {
					doLast {
						Set<String> oneRuntime = configurations.oneRuntime.files.collect({ it.name }) as Set
						Set<String> oneTestRuntime = configurations.oneTestRuntime.files.collect({ it.name }) as Set
						Set<String> twoRuntime = configurations.twoRuntime.files.collect({ it.name }) as Set
						Set<String> twoTestRuntime = configurations.twoTestRuntime.files.collect({ it.name }) as Set

						assert oneRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert oneRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert oneTestRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert oneTestRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert oneTestRuntime.contains('junit-4.12.jar')
						assert oneTestRuntime.contains('mockito-core-2.7.12.jar')
			
						assert twoRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert !twoRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert twoTestRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert !twoTestRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert twoTestRuntime.contains('junit-4.12.jar')
						assert !twoTestRuntime.contains('mockito-core-2.7.12.jar')
					}
				}
			""")
		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments('performAssertions', '--stacktrace')
				.withPluginClasspath()
				.build()

		then:
		result.task(":performAssertions").outcome == TaskOutcome.SUCCESS
	}

	def "Test configurations with Implementation and Api"() {
		given:
		writeFile("build.gradle", """
				plugins {
					id 'com.microsoft.javaflavours'
				}
				version = '1.0-SNAPSHOT'
				repositories {
					mavenCentral()
				}
				javaFlavours {
					flavour 'one'
					flavour 'two'
				}
				dependencies {								    
					implementation 'org.springframework:spring-context:4.3.2.RELEASE'
					testImplementation 'junit:junit:4.12'
					oneImplementation 'org.hibernate:hibernate-core:5.2.8.Final'
					oneTestImplementation 'org.mockito:mockito-core:2.7.12'
				}
				task performAssertions {
					doLast {
						Set<String> oneRuntime = configurations.oneRuntimeClasspath.files.collect({ it.name }) as Set
						Set<String> oneTestRuntime = configurations.oneTestRuntimeClasspath.files.collect({ it.name }) as Set
						Set<String> twoRuntime = configurations.twoRuntimeClasspath.files.collect({ it.name }) as Set
						Set<String> twoTestRuntime = configurations.twoTestRuntimeClasspath.files.collect({ it.name }) as Set

						assert oneRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert oneRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert oneTestRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert oneTestRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert oneTestRuntime.contains('junit-4.12.jar')
						assert oneTestRuntime.contains('mockito-core-2.7.12.jar')
			
						assert twoRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert !twoRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert twoTestRuntime.contains('spring-context-4.3.2.RELEASE.jar')
						assert !twoTestRuntime.contains('hibernate-core-5.2.8.Final.jar')
						assert twoTestRuntime.contains('junit-4.12.jar')
						assert !twoTestRuntime.contains('mockito-core-2.7.12.jar')
					}
				}
			""")
		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments('performAssertions', '--stacktrace')
				.withPluginClasspath()
				.build()

		then:
		result.task(":performAssertions").outcome == TaskOutcome.SUCCESS
	}

	def "Test two flavours compile, test and jar"() {
		given:
		writeFile("settings.gradle", "rootProject.name = 'test-project'")
		writeFile("build.gradle", """
			plugins {
				id 'com.microsoft.javaflavours'
			}
			version = '1.0-SNAPSHOT'
			repositories {
				mavenCentral()
			}
			javaFlavours {
				flavour 'red'
				flavour 'blue'
			}
			dependencies {
				testImplementation 'junit:junit:4.12'
			}
			tasks.withType(Test) {
			    testLogging.showStandardStreams = true
			}
		""")
		['main', 'red', 'blue'].each { String flavour ->
			writeFile("src/$flavour/resources/${flavour}.txt", flavour)
			writeFile("src/$flavour/java/foo/${flavour.capitalize()}.java", """
				package foo;
				public interface ${flavour.capitalize()} {}
			""")
			String testDir = 'main' == flavour ? 'test' : "${flavour}Test"
			writeFile("src/$testDir/resources/${flavour}Test.txt", flavour)
			writeFile("src/$testDir/java/foo/${flavour.capitalize()}Test.java", """
				package foo;
				import java.util.*;
				import org.junit.*;
				public class ${flavour.capitalize()}Test {

					@Test
					public void ${flavour}Test() {
						String[] files = { 
							"foo/Main.class", "main.txt", "foo/MainTest.class", "mainTest.txt",
							"foo/Red.class", "red.txt", "foo/RedTest.class", "redTest.txt", 
							"foo/Blue.class", "blue.txt", "foo/BlueTest.class", "blueTest.txt" 
						};
						List<String> found = new ArrayList<>();
						List<String> notFound = new ArrayList<>();
						for (String file : files) {
							if (getClass().getClassLoader().getResource(file) != null) {
								found.add(file);
							} else {
								notFound.add(file);
							}
						}
						System.out.println(String.format("class=%s, found=%s, notFound=%s", getClass().getName(), found, notFound));
					}
				}
			""")
		}
		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments('build', '--stacktrace')
				.withPluginClasspath()
				.build()

		then:
		result.task(":build").outcome == TaskOutcome.SUCCESS

		result.output.contains('class=foo.MainTest, found=[foo/Main.class, main.txt, foo/MainTest.class, mainTest.txt], notFound=[foo/Red.class, red.txt, foo/RedTest.class, redTest.txt, foo/Blue.class, blue.txt, foo/BlueTest.class, blueTest.txt]')
		result.output.contains('class=foo.RedTest, found=[foo/Main.class, main.txt, foo/MainTest.class, mainTest.txt, foo/Red.class, red.txt, foo/RedTest.class, redTest.txt], notFound=[foo/Blue.class, blue.txt, foo/BlueTest.class, blueTest.txt]')
		result.output.contains('class=foo.BlueTest, found=[foo/Main.class, main.txt, foo/MainTest.class, mainTest.txt, foo/Blue.class, blue.txt, foo/BlueTest.class, blueTest.txt], notFound=[foo/Red.class, red.txt, foo/RedTest.class, redTest.txt]')

		assertZipEntries("build/libs/test-project-1.0-SNAPSHOT.jar", ['META-INF/MANIFEST.MF', 'foo/Main.class', 'main.txt'])
		assertZipEntries("build/libs/test-project-1.0-SNAPSHOT-red.jar", ['META-INF/MANIFEST.MF', 'foo/Main.class', 'main.txt', 'foo/Red.class', 'red.txt'])
		assertZipEntries("build/libs/test-project-1.0-SNAPSHOT-blue.jar", ['META-INF/MANIFEST.MF', 'foo/Main.class', 'main.txt', 'foo/Blue.class', 'blue.txt'])
	}
}
