/**
 * Created by kerrk on 4/9/16.
 */

angular.module('idManagementApp')
.controller('samlRedirectController', ['$scope', 'authService', '$sce', '$location', '$rootScope',
  function ($scope, authService, $sce, $location, $rootScope) {
    var appName = $location.search().app;
    $location.search('app', null);
    if (appName != undefined) {
      authService.samlSSO(appName).then(function(response) {
        var data = {
          redirectUrl: $sce.trustAsResourceUrl(response.data.acsUrl),
          samlResponse: response.data.samlResponse
        }
        console.log(data.redirectUrl + '***redirect');
        console.log(data.samlResponse + '***response');
        $rootScope.$broadcast('gateway.redirect', data);
      });
    }
  }])

  .directive('autoSubmitForm', ['$timeout', function($timeout) {
    return {
      replace: true,
      scope: {},
      template: '<form action="{{formData.redirectUrl}}" method="POST">'+
      '<input type="hidden" name="SAMLResponse" value="{{formData.samlResponse}}" />'+
      '</div>'+
      '</form>',
      link: function($scope, element, $attrs) {
        $scope.$on($attrs['event'], function(event, data) {
          $scope.formData = data;
          $timeout(function() {
            element.submit();
          })
        })
      }
    }
  }])
