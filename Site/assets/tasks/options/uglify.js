module.exports = {
  options: {
    mangle: {
      except: ['jQuery', 'Handlebars', 'wdk']
    },
  },
  compile: {
    files: {
      'tmp/application.min.js': ['tmp/application.js']
    }
  }
};
