var config = exports;

config['Browser Tests'] = {
    environment: 'browser',
    sources: [],
    tests: [ "resources/js/hello_world_browser_test.js"
           , "resources/js/hello_world_browser_optimized_test.js"
           ]
};

 config['Node Tests'] = {
     environment: 'node',
     sources: [],
     tests: ["resources/js/hello_world_node_test.js"]
 };
