# buster-cljs

buster-cljs provides an easy and convenient way to test your
Clojure/Clojurescript source code by using a same testsuite code base.

It offers a similar interface as the one found in the clojure.test library, but
offers the powerful buster.js test architecture.

## Project Goals

There are some ports of clojure.test already out there, so why build
yet another one? Birdseye Software strongly believes that the most
important thing is not the test API but rather, what facilities the
test platform provides, buster allows you to:

* Use different browsers<sup>1</sup> as slaves to run your tests; just set up
  the buster.js node server, connet to it through the browser you want
  to slave (might be a machine or a mobile phone), and click a
  button. No browser plugins required to be installed.

* Run your testsuite in both in the browser and node.

* Easy integration with phantomjs for headless testing.

## Project Maturity

buster-cljs is a very young library; started in November 2012, used
from the start to test all the Clojure/Clojurescript libraries that
Birdseye Software develops.

## Install

buster-cljs is released to clojars. If you are using maven, add the following repository
definition to your pom.xml

```xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

### The most recent release

With leiningen:

```
[com.birdseye/buster-cljs "0.1.0"]
```

With Maven:

```xml
<dependency>
  <groupId>com.birdseye</groupId>
  <artifactId>buster-cljs</artifactId>
  <version>1.4.0</version>
</dependency>
```xml

## Getting Started

Please refer to our [Getting Started guide][getting_started].

## Documentation & Examples

Please refer to our [documentation site][documentation_site], our
[test suite][test_suite] also has some examples

## Supported Clojure Versions

This library has been tested on Clojure 1.4 and Clojurescript version
that comes bundled with lein-cljsbuild 0.2.9

## Continuous Integration Status

TODO

## License

Copyright © 2012 Birdseye Software, Roman Gonzalez, Tavis Rudd.

Distributed under the Eclipse Public License, the same as Clojure.

## Footnotes

[1]: browsers might be running in different machines of the same
network
[getting_started]:
[documentation_site]:
[test_suite]:https://github.com/BirdseyeSoftware/buster-cljs/blob/master/test/buster_cljs/test/macros_test.cljs