module.exports = {
  compile: {
    files: [{
      expand: true,
      cwd:  'less',
      src:  '*.less',
      dest: 'tmp/css',
      ext:  '.css'
    }],
    options: {
      ieCompat: true
    }
  }
};
