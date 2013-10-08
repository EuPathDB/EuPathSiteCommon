module.exports = {
  wdk: {
    files: [
      {
        expand: true,
        cwd: '<%= env.WEBAPP %>/wdk/js/',
        src: ['**'],
        dest: 'js/wdk/'
      },
      {
        expand: true,
        cwd: '<%= env.WEBAPP %>/wdk/css/',
        src: ['**'],
        dest: 'css/wdk/'
      }
    ]
  },
  css: {
    expand: true,
    cwd: 'css/',
    src: '**',
    dest: 'tmp/css/'
  },
  images: {
    expand: true,
    cwd: 'images/',
    src: '**',
    dest: 'tmp/images/'
  },
  tmp: {
    expand: true,
    cwd: 'tmp/',
    src: '**',
    dest: '<%= env.HTML %>/assets/'
  }
};
