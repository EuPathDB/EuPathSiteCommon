var grunt = require('grunt'),
    pathUtils = require('path'),
    Helpers = {

      config: {
        pkg: grunt.file.readJSON('./package.json'),
        env: process.env
      },

      /*
       * Load configuration modules from ./tasks/options/{task}.js
       *
       * IDEA - Instead of the above, do:
       *   Load configuration modules from ./tasks/options/{task}/{subtask}.js
       *   This would allow subtasks to easily be added. For instance,
       *   to add the grunt task less:fungi, we could have 
       *       ./tasks/options/less/fungi.js
       *
       *   This would look something like:
       *     module.exports = {
       *       src: 'my/site.less',
       *       dest: 'dist/my_site.css'
       *     }
       *
       *   We would then create options.less, and then
       *   create options.less.fungi with the above definition
       *
       */
      loadConfig: function(path) {
        var glob = require('glob'),
            object = {},
            key;

        glob.sync('*', {cwd: path}).forEach(function(option) {
          key = option.replace(/\.js$/, '');
          object[key] = require('../' + path + option);
        });

        return object;
      },

      // pull out inline source map and write to file
      postBundleCB: function(bundlePath) {
        return function(err, src, next) {
          var path        =  require('path'),
              fs          =  require('fs'),
              mold        =  require('mold-source-map'),
              resumer     =  require('resumer'),
              mapFilePath =  bundlePath +  '.map',
              jsRoot      =  'js',
              modifiedSrc =  '';


          function mapFileUrlComment(sourcemap, cb) {

            // make source files appear under the following paths:
            // /js
            //    foo.js
            //    main.js
            // /js/wunder
            //    bar.js 

            sourcemap.sourceRoot(''); 
            sourcemap.mapSources(mold.mapPathRelativeTo(jsRoot));

            // write map file and return a sourceMappingUrl that points to it
            fs.writeFile(mapFilePath, sourcemap.toJSON(2), 'utf-8', function (err) {
              if (err) return console.error(err);
              cb('//# sourceMappingURL=' + path.basename(mapFilePath));
            });
          }

          resumer().queue(src)
            .end()
            .pipe(mold.transform(mapFileUrlComment))
            .on('data', function(chunk) {
                modifiedSrc += chunk;
            })
            .on('end', function() {
              next(err, modifiedSrc);
            });
        }
      }
    };

module.exports = Helpers;
