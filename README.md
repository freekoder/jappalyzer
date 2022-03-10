# Jappalyzer

A Java implementation of the [Wappalyzer](https://github.com/AliasIO/wappalyzer).

Uses data from https://github.com/AliasIO/wappalyzer


## Build

### Build library
```shell
$ gradle build
```

### Build console application (with dependencies)
```shell
$ gradle fatJar
```
## Usage

#### Get technologies from web page
```java
Jappalyzer jappalyzer = Jappalyzer.latest();
List<TechnologyMatch> matches = jappalyzer.fromUrl("https://yandex.ru/");
matches.forEach(System.out::println);
```

#### Get technologies from html file
```java
Jappalyzer jappalyzer = Jappalyzer.latest();
List<TechnologyMatch> matches = jappalyzer.fromFile("website.html");
matches.forEach(System.out::println);
```

#### Output
```sh
TechnologyMatch{technology=BEM, reason=html, duration=7ms}
TechnologyMatch{technology=Cart Functionality, reason=dom, duration=1ms}
TechnologyMatch{technology=jQuery, reason=script, duration=1ms}
TechnologyMatch{technology=React, reason=html, duration=14ms}
```
