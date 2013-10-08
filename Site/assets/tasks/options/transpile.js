module.exports = {
  amd: {
    type: 'amd',
    files: [{
      expand: true,
      cwd: 'js/wdk/src/',
      src: ['**/*.js'],
      dest: 'tmp/wdk/',
      ext: '.amd.js'
    }]
  }
}
