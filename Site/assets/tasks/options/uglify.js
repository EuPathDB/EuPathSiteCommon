module.exports = {
  options: {
    mangle: {
      except: ['jQuery', 'Handlebars', 'wdk']
    },
  },
  compile: {
    'tmp/application.min.js': ['tmp/application.min.js']
  }
};
