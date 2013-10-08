module.exports = function(grunt) {

  var Helpers = require('./tasks/eupath-helpers'),
      config = Helpers.config;

  require('load-grunt-tasks')(grunt);

  config = grunt.util._.extend(config, Helpers.loadConfig('./tasks/options/'));

  grunt.loadTasks('tasks');

  grunt.registerTask('build:before', [
    'clean',
    'symlink'
  ]);

  grunt.registerTask('build:after', [
    'copy:tmp'
  ]);

  grunt.registerTask('build:js:dev', [
    'browserify:compile'
  ]);

  grunt.registerTask('build:js:prod', [
    'browserify:compile',
    'uglify'
  ]);

  grunt.registerTask('build:css:dev', [
    'copy:css',
    'less:compile'
  ]);

  grunt.registerTask('build:css:prod', [
    'copy:css',
    'less:compile',
    'cssmin'
  ]);

  grunt.registerTask('build:dev', [
    'build:before',
    'build:js:dev',
    'build:css:dev',
    'copy:images',
    'build:after',
    'watch'
  ]);

  grunt.registerTask('build:prod', [
    'build:before',
    'build:js:prod',
    'build:css:prod',
    'copy:images',
    'build:after'
  ]);

  grunt.registerTask('default', [
    'build:dev'
  ]);

  grunt.initConfig(config);
};
