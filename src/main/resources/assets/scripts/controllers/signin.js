'use strict';

/**
 * @ngdoc function
 * @name idManagementApp.controller:SignInCtrl
 * @description
 * # SignInCtrl
 * Controller of the idManagementApp
 */
angular.module('idManagementApp')
  .controller('SignInCtrl', ['$scope', 'authService', function ($scope, authService) {

    $scope.signIn = {

      email: '',

      password: '',

      errorMessage: '',

      signUpUrl: function() {
        window.location.href = '/#/signup';
      },

      submit: function() {
        $scope.signIn.errorMessage = '';
        var email = $scope.signIn.email;
        var password = $scope.signIn.password;
        authService.signIn({email: email, password: password}).success(function(data) {
          if (data !== null && data !== undefined && data !== '') {
            authService.setUserInfo(data, email, password);
            window.location.href = '/#/main';
          } else {
            console.log('invalid credentials!');
            $scope.signIn.errorMessage = 'invalid credentials';
          }
        });
      }
    };

  }]);
