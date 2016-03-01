'use strict';

angular.module('idManagementApp')
  .controller('SignUpCtrl', ['$scope', 'authService', function ($scope, authService) {

    $scope.signUp = {

      email: '',

      password: '',

      repeatPassword: '',

      errorMessage: '',

      signInUrl: function() {
        window.location.href = '/#/signin';
      },

      submit: function() {
        $scope.signUp.errorMessage = '';
        var email = $scope.signUp.email;
        var password = $scope.signUp.password;
        authService.signUp({email: email, password: password}).success(function(data) {
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
