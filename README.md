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
Set<TechnologyMatch> matches = jappalyzer.fromUrl("https://yandex.ru/");
matches.forEach(System.out::println);
```

#### Get technologies from html file
```java
Jappalyzer jappalyzer = Jappalyzer.latest();
Set<TechnologyMatch> matches = jappalyzer.fromFile("website.html");
matches.forEach(System.out::println);
```

#### Output
```sh
TechnologyMatch{technology=Typekit, reason=html, duration=0ms, categories=[Category{name='Font scripts'}]}
TechnologyMatch{technology=jQuery Migrate, reason=script, duration=0ms, categories=[Category{name='JavaScript libraries'}]}
TechnologyMatch{technology=Nginx, reason=header, duration=1ms, categories=[Category{name='Web servers'}, Category{name='Reverse proxies'}]}
TechnologyMatch{technology=jQuery CDN, reason=script, duration=0ms, categories=[Category{name='CDN'}]}
TechnologyMatch{technology=cdnjs, reason=script, duration=0ms, categories=[Category{name='CDN'}]}
TechnologyMatch{technology=PHP, reason=implied, duration=0ms, categories=[Category{name='Programming languages'}]}
TechnologyMatch{technology=jQuery, reason=script, duration=0ms, categories=[Category{name='JavaScript libraries'}]}
TechnologyMatch{technology=MySQL, reason=implied, duration=0ms, categories=[Category{name='Databases'}]}
TechnologyMatch{technology=WordPress, reason=meta, duration=0ms, categories=[Category{name='CMS'}, Category{name='Blogs'}]}
TechnologyMatch{technology=RequireJS, reason=script, duration=0ms, categories=[Category{name='JavaScript frameworks'}]}
TechnologyMatch{technology=Google Analytics, reason=script, duration=0ms, categories=[Category{name='Analytics'}]}
```
