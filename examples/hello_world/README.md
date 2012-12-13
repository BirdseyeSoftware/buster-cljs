# hello_world

Simple hello world application to test buster-cljs

## Installation & Execution

After you install node.js/npm, execute in the root of the project (buster-cljs/examples/hello_world/):

```bash
$> npm install
$> lein cljsbuild once
$> ./node_modules/.bin/buster-server &
# Go to URL http://localhost:1111 to slave your browser
$> npm test
```

You may also check the test results on a static page using `buster-static`

```bash
$> npm install
$> lein cljsbuild once
$> ./node_modules/.bin/buster-static &
# Go to URL http://localhost:8282 to see test suite results
```

## Important take aways

You need:

* A `package.json` file to fetch buster dependencies from npm

* A `buster.js` file in the root of the project to run test
  suites successfuly

* To match the Javascript products from `lein-cljsbuild` into the
  `buster.js` file

## License

Copyright © 2012 Birdseye Software

Distributed under the MIT License.
