'use strict';

/**
 * @ngdoc function
 * @name idManagementApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the idManagementApp
 */
angular.module('idManagementApp')
  .controller('DashboardCtrl', ['$cookies', '$scope', 'authService', function ($cookies, $scope, authService) {
    var creds = $cookies.get('globals');
    if (creds === null || creds === undefined || creds === '') {
      window.location.href = '/#/signin';
    }
    authService.setHeaders();

    $scope.dashboard = {

      getUserInfo: function () {
        authService.getUserInfo().then(function(result) {
          $scope.dashboard.userInfo = result.data;
        });
      },

      userInfo: {},

      init: function () {
        $scope.dashboard.getUserInfo();
      },

      testSaml: function () {
        window.location.href = '/#/sso?app=wp';
      }
    };

    $scope.dashboard.init();


  }]);
