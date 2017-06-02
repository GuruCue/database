# Guru Cue Search &amp; Recommendations Database Interface Definitions

This is the library providing the database (and increasingly also other)
interface definitions for the Guru Cue Search &amp; Recommendations. It
defines interfaces and utilities for handling database entities, blender
filters, JSON and XML data, and other miscellany.

# Building the Library
The minimum required JDK version to build the library with is 1.8.

Perform the build using [gradle](https://gradle.org/). If you don't have it
installed, you can use the gradle wrapper script `gradlew` (Linux and similar)
or `gradlew.bat` (Windows).

The build process will result in a `jar` file in the `build/libs` directory.
Copy it into the `libs` directory of dependent projects, such as
`data-provider-postgresql`.
