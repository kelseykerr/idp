'use strict';

/**
 * @ngdoc function
 * @name idManagementApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the idManagementApp
 */
angular.module('idManagementApp')
  .controller('MainCtrl', ['$cookies', '$scope', 'authService', function ($cookies, $scope, authService) {
    var creds = $cookies.get('globals');
    if (creds === null || creds === undefined || creds === '') {
      window.location.href = '/#/signin';
    }
    authService.setHeaders();

    $scope.dashboard = {

      getUserInfo: function () {
        authService.getUserInfo().then(function(result) {
          console.log(result);
        });
      },

      init: function () {
        $scope.dashboard.getUserInfo();
      }
    };

    $scope.dashboard.init();


  }]);
