var Helpers = require('../eupath-helpers');

module.exports = {
  compile: {
    src: 'js/application.js',
    dest: 'tmp/js/application.js'
  },
  options: {
    debug: true,
    transforms: ['coffeeify', 'hbsfy', 'deamdify'],
    postBundleCB: Helpers.postBundleCB('tmp/js/application.js')
  }
};
