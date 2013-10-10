module.exports = {
  options: {
    mangle: {
      except: ['jQuery', 'Handlebars', 'wdk']
    },
    sourceMap: 'tmp/js/application.min.js.map',
    sourceMappingURL: 'application.min.js.map',
    sourceMapIn: 'tmp/js/application.js.map'
  },
  compile: {
    files: {
      'tmp/js/application.min.js': ['tmp/js/application.js']
    }
  }
};
