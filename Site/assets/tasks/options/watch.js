module.exports = {
  js: {
    files: ['js/**'],
    tasks: ['build:js:dev', 'copy:tmp']
  },
  css: {
    files: ['less/**', 'css/**'],
    tasks: ['build:css:dev', 'copy:tmp']
  },
  image: {
    files: ['images/**'],
    tasks: ['copy:images', 'copy:tmp']
  }
}
