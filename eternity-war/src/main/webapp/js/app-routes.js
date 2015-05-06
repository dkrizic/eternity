angular.module('Eternity').config(function ($routeProvider) {

    $routeProvider
        .when('/users', {
            templateUrl: 'templates/list-users.html',
            controller: 'UserCtrl',
            resolve: {
                data: function (UserService) {
                    return UserService.find().$promise;
                }
            }
        })
        .when('/projects', {
            templateUrl: 'templates/list-projects.html',
            controller: 'ProjectCtrl',
            resolve: {
                data: function (ProjectService) {
                    return ProjectService.find().$promise;
                }
            }
        })
        .when('/bookings', {
            templateUrl: 'templates/list-bookings.html',
            controller: 'BookingCtrl',
            controllerAs: 'bCtrl',
            resolve: {
                bookings: function (UserService) {
                    return UserService.getBookingsForCurrentUser().$promise;
                },
                projects: function (UserService) {
                    return UserService.getProjectsForCurrentUser().$promise;
                }
            }
        })
        .when('/login', {
            templateUrl: 'templates/authentication/login.html',
            controller: 'LoginCtrl'
        })
        .when('/tokenLogin', {
            templateUrl: 'templates/authentication/token.html',
            controller: 'TokenLoginCtrl'
        })
        .when('/logout', {
            templateUrl: 'templates/authentication/logout.html',
            controller: 'LogoutCtrl'
        })
        .when('/forbidden', {
            templateUrl: 'templates/authentication/forbidden.html'
        })
        .otherwise({
            redirectTo: '/bookings'
        });

});