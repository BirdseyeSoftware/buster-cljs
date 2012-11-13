/**
* @type {object}
*/
var buster;

/**
* @param {string}
* @param {object}
*
* @return {undefined}
*/
buster.testCase = function(desc, test_spec)  {};

/**
* @param {boolean}
* @param {string}
*
* @return {undefined}
*/
buster.assert = function(bool_val, fail_desc) {};

/**
* @type {Object}
*
*/
buster.spec = {};


/**
* @param {string}
* @param {function}
*
* @return {undefined}
*/
buster.spec.it = function(desc, bodyfn) {};

/**
* @param {string}
* @param {function}
*
* @return {undefined}
*/
buster.spec.describe = function(dev, bodyfn) {};
