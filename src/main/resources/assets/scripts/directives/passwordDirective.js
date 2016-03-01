'use strict';


/**
 * Created by kerrk on 2/29/16.
 */
angular.module('idManagementApp')
  .directive('passwordVerifier', function() {
    return {
      require: 'ngModel',
      restrict: 'A',
      link: function($scope, $element, $attrs, ngModel) {
        $scope.$watch($attrs.ngModel, function(value) {
          if (value !== undefined && value !== '' && value !== null) {
            ngModel.$setValidity('hasSpecial', /(?=.*\W)/.test(value) ? true : false);
            ngModel.$setValidity('hasLength',/.{8,255}/.test(value) ? true : false );

            var pwRegex = new RegExp('(?=.*\\W).{8,255}');
            if (pwRegex.test(value)) {
              ngModel.$setValidity('invalid', true);
            } else {
              ngModel.$setValidity('invalid', false);
            }
          } else {
            ngModel.$setValidity('hasSpecial', false);
            ngModel.$setValidity('hasLength', false );
          }
        });

      }
    };
  })

.directive('compareTo', function() {
  return {
    require: 'ngModel',
    restrict: 'A',
    scope: {
      otherModelValue: '=compareTo'
    },
    link: function(scope, element, attributes, ngModel) {

      ngModel.$validators.compareTo = function(modelValue) {
        return modelValue === scope.otherModelValue;
      };

      scope.$watch('otherModelValue', function() {
        ngModel.$validate();
      });
    }
  };

});
