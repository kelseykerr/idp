'use strict';

/**
 * @ngdoc overview
 * @name idManagementApp
 * @description
 * # idManagementApp
 *
 * Main module of the application.
 */
angular
  .module('idManagementApp', [
    'ngAnimate',
    'ngCookies',
    'ngMessages',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/dashboard.html',
        controller: 'DashboardCtrl',
        controllerAs: 'main'
      })
      .when('/signin', {
        templateUrl: 'views/signin.html',
        controller: 'SignInCtrl',
        controllerAs: 'signin'
      })
      .when('/signup', {
        templateUrl: 'views/signup.html',
        controller: 'SignUpCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
