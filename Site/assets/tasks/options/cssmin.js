var grunt = require('grunt');

module.exports = {
  minify: {
    expand: true,
    cwd: 'tmp/css/',
    src: ['*.css'],
    dest: 'tmp/css/',
    ext: '.min.css'
  }
};

// module.exports = {
//   combine: {
//     files: (function() {
//       return grunt.file.expand({
//         cwd: 'css'
//       }, '*DB.css').map(function(src) {
//         return {
//           src: ['css/wdk/wdkCommon.css', 'css/' + src],
//           dest: 'css/all.' + src
//         };
//       });
//     })()
//   },
//   minify: {
//   }
// };
