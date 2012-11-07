var config = exports;

config['Browser Tests'] = {
    environment: 'browser',
    sources: [],
    tests: ["resources/js/buster_cljs_browser_test.js"]
};

 config['Server Tests'] = {
     environment: 'node',
     sources: [],
     tests: ["resources/js/buster_cljs_node_test.js"]
 };
