'use strict';


angular.module('idManagementApp')
  .controller('NavbarCtrl', ['$cookies', '$scope', 'authService', '$location',
    function ($cookies, $scope, authService, $location) {
    var creds = $cookies.get('globals');
    if (creds === null || creds === undefined || creds === '') {
      window.location.href = '/#/signin';
    }

    $scope.navbar = {

      show: function() {
        console.log($location.path());
        return $location.path() !== '/signin' && $location.path() !== '/signup';
      },

      userInfo: {},

      init: function () {
        if (creds !== undefined && $scope.navbar.show()) {
          authService.setHeaders();
          authService.init().then(function(result) {
            $scope.navbar.userInfo = result;
          });
        }
      }
    };

    $scope.navbar.init();


  }]);

