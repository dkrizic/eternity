(function () {

    'use strict';

    angular.module('Eternity', ['ngRoute', 'ngResource', 'ngCookies']);

    angular.module('Eternity').config(function ($httpProvider) {
        $httpProvider.interceptors.push(function ($cookies, $rootScope, $location, $q) {
            return {
                'request': function (request) {
                    // if we're not logged-in to the AngularJS app, redirect to login page
                    $rootScope.loggedIn = $cookies["XSRF-TOKEN"] || $cookies["REMEMBER-ME"] || $rootScope.loggedIn;
                    if (!$rootScope.loggedIn && $location.path() != '/login') {
                        $location.path('/login');
                    }
                    return request;
                },
                'responseError': function (rejection) {
                    // if we're not logged-in to the web service, redirect to login page
                    if (rejection.status === 401) {

                        // I am unauthorized
                        // So my session sux
                        delete $cookies["XSRF-TOKEN"];

                        // do I have a remember me? use it
                        if($cookies["REMEMBER-ME"]) {
                            $location.path('/tokenLogin');
                        } else if($location.path() != '/login') {
                            $location.path('/login');
                        }

                    }
                    return $q.reject(rejection);
                }
            };
        });
    });

    angular.module('Eternity').controller("EternityController", function ($rootScope) {

        $rootScope.$on("$locationChangeStart", function (event, nextUrl, currentUrl) {

            if (currentUrl) {
                $rootScope.nextRoute = currentUrl;
            }

        });

    });

    angular.module('Eternity').config(['$resourceProvider', function ($resourceProvider) {
        // Don't strip trailing slashes from calculated URLs
        $resourceProvider.defaults.stripTrailingSlashes = false;
    }]);

})();