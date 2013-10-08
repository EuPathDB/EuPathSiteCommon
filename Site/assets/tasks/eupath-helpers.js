var grunt = require('grunt'),
    pathUtils = require('path'),
    Helpers = {};

Helpers.config = {
  pkg: grunt.file.readJSON('./package.json'),
  env: process.env
};

/*
 * Load configuration modules from ./tasks/options/{task}.js
 *
 * IDEA - Instead of the above, do:
 *   Load configuration modules from ./tasks/options/{task}/{subtask}.js
 *   This would allow subtasks to easily be added. For instance,
 *   to add the grunt task less:fungi, we could have 
 *       ./tasks/options/less/fungi.js
 *
 *   This would look something like:
 *     module.exports = {
 *       src: 'my/site.less',
 *       dest: 'dist/my_site.css'
 *     }
 *
 *   We would then create options.less, and then
 *   create options.less.fungi with the above definition
 *
 */
Helpers.loadConfig = function(path) {
  var glob = require('glob'),
      object = {},
      key;

  glob.sync('*', {cwd: path}).forEach(function(option) {
    key = option.replace(/\.js$/, '');
    object[key] = require('../' + path + option);
  });

  return object;
};

module.exports = Helpers;
