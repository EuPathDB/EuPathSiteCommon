/* global $, wdk */

/**
 * Show an error message if specified parameter values are identical. In the
 * case of mulipick params, treat as error only if a single value is selected
 * for each. If we want to treat multiple identical values as an error, we
 * should make it a flag.
 */

(function() {
  var initEvent = wdk.parameterHandlers.QUESTION_INITIALIZE_EVENT;

  $(document).on(initEvent, '[data-uniq-value-params]', function(event) {
    var $el = $(event.target);
    initUniqValueParams($el.closest('form'), $el.data('uniq-value-params'));
  });

  function initUniqValueParams($form, paramNames) {
    $form.on('change', validateWith(paramNames));
    $form.on('submit', validateWith(paramNames, true));
  }

  function validateWith(paramNames, preventOnValidationError) {
    return function validate(event) {
      var $form = $(event.currentTarget);
      var errors = makeErrorRecords(makeParamRecords(paramNames, $form));
      var $messageDiv = getMessageDiv(event.currentTarget);

      // clear previous valdity messages
      paramNames.forEach(function(name) {
        $form.find(makeParamSelector(name)).toArray().forEach(function(el) {
          $(el).closest('.param')
            .qtip('destroy')
            .css({
              display: '',
              boxShadow: ''
            });
        });
      });

      errors.forEach(function(error) {
        error.param.$el
          .closest('.param')
          .qtip({
            content: {
              text: '<div style="color: darkred;">' + error.message + '</div>'
            },
            position: {
              my: 'left center',
              at: 'right center'
            },
            style: {
              classes: 'qtip-bootstrap'
            },
            show: {
              solo: true
            }
          })
          .css({
            boxShadow: '0 0 4px 1px red',
            display: 'inline-block'
          })
      });

      if (errors.length > 0) {
        if (preventOnValidationError) event.preventDefault();

        $messageDiv.show(400, function() {
          if (preventOnValidationError) {
            this.scrollIntoView();
            errors[0].param.$el.closest('.param').qtip('show');
          }
        })
      }
      else {
        $messageDiv.hide(400)
      }
    }
  }

  function makeParamSelector(name, isChecked) {
    return '[name="value(' + name + ')"],[name="array(' + name + ')"]' +
      (isChecked ? ':checked' : '');
  }

  function makeParamRecords(paramNames, $form) {
    return paramNames.map(function(name) {
      var $el = $form.find(makeParamSelector(name, true));
      var displayName = $el.closest('.param').attr('prompt');
      return {
        name: name,
        displayName: displayName,
        $el: $el
      };
    });
  }

  function makeErrorRecords(params) {
    return params.map(function(param) {
      var duplicates = params.filter(function(otherParam) {
        return param.name !== otherParam.name && hasDuplicateValue(param, otherParam);
      });
      return {
        param: param,
        message: formatCustomValidityMessage(duplicates)
      };
    }).filter(function(error) {
      return error.message;
    })
  }

  function hasDuplicateValue(param1, param2) {
    return param1.$el.length === 1 &&
           param2.$el.length === 1 &&
           getParamValue(param1) === getParamValue(param2);
  }

  function getParamValue(param) {
    return _(param.$el)
      .map(_.property('value'))
      .join();
  }

  function formatCustomValidityMessage(duplicateParams) {
    var otherParams = duplicateParams
      .map(_.property('displayName'))
      .join(', ')
    return otherParams ? 'This value must be different from ' + otherParams : '';
  }

  var getMessageDiv = _.memoize(function(form) {
    return $(
      '<div style="text-align: center; display: none;">' +
      '  <p style="color: darkred; display: inline-block; padding: 8px; border: 1px solid #a4a4a4; border-radius: 4px; font-size: 1.2em;">' +
      '    <i style="color: orange;" class="fa fa-warning"/> ' +
      '    Please fix the errors below.' +
      '  </p>' +
      '</div>')
      .insertBefore(form);
  })

})();
