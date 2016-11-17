# [Avian](https://github.com/ReadyTalk/avian) example usage in gradle build

Kotlin+java.nio hello world http server example

## Requirements

You will need the following environment to build the project:

* JDK >= 1.7 (`jar`,`javac` in PATH)
* Build tools (`make`,`gcc`,`g++` in PATH)
* Bash (`bash` in PATH)

## How to build

```bash
$ ./gradlew clean standalone
```

This will download Avian to `build/avian` directory and then will launch the build of your project into a single
self-contained application file less than 4Mb in size.

## Drawbacks

There is a `src/util` directory containing parts of a standard jdk source files + primitive thread pool implementation.
The reason for this is that Avian does not implement many parts of a Java standard library.

## Advanced settings

Avian is a very lightweight JDK implementation. As such, it misses lots of great JDK standard library things.
Fortunately there is a way of building your project with the alternative JDK implementations (e.g. OpenJDK).

### Build with OpenJDK

You can build the project in more advanced way with the full JDK class library support. To do so, you will need to
have the source code and a distribution of OpenJDK. As soon as you get ones, just indicate the path to each of them
in the respective properties within `gradle.properties` file:

```properties
OPEN_JDK_PATH=/absolute/path/to/openjdk/distribution
OPEN_JDK_SRC_PATH=/absolute/path/to/openjdk/source/code
```