/*global module:false*/
module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    // Metadata.
    pkg: grunt.file.readJSON('package.json'),
    // Task configuration.
    less: {
      options: {
        paths: ['Site/htdocs/assets/css', '../WDK/View/webapp/wdk/css']
      },
      dist: {
        files: [{
          expand: true,
          cwd: '../ApiCommonWebsite/Site/htdocs/assets/css',
          src: ['*.bootstrap.less'],
          //dest: process.env.HTML + '/assets/css',
          dest: 'build',
          ext: '.css'
        }]
      }
    },

    copy: {
      dist: {
        files: [{
          expand: true,
          cwd: '../ApiCommonWebsite/Site/htdocs/assets/css',
          src: ['*DB.css'],
          //dest: process.env.HTML + '/assets/css',
          dest: 'build',
          ext: '.orig.css'
        }]
      }
    },

    cssmin: {
      dist: {
        files: [{
          expand: true,
          cwd: 'build',
          src: ['*.css'],
          //dest: process.env.HTML + '/assets/css',
          dest: 'build',
          //ext: '.min.css'
        }]
      }
    },

    clean: ['build']
  });

  // These plugins provide necessary tasks.
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-clean');

  // Default task.
  grunt.registerTask('default', ['less']);

  grunt.registerTask('validate', ['clean', 'copy', 'less', 'cssmin']);

};
