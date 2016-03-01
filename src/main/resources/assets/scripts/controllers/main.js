'use strict';

/**
 * @ngdoc function
 * @name idManagementApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the idManagementApp
 */
angular.module('idManagementApp')
  .controller('MainCtrl', ['$cookies', function ($cookies) {
    var creds = $cookies.get('globals');
    if (creds === null || creds === undefined || creds === '') {
      window.location.href = '/#/signin';
    }
  }]);
