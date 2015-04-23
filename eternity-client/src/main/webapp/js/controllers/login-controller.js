angular.module('Eternity').controller('LoginCtrl', function ($scope, $rootScope, $location, SessionService) {

    $scope.login = {
        username: 'khansen',
        password: 'pw',
        remember: false
    };

    $scope.loginMeIn = function () {
        $scope.user = SessionService.save($scope.login,

            function (success) {

                // essential since the cookie is not yet available for checking
                $rootScope.loggedIn = true;

                if ($rootScope.nextRoute && $rootScope.nextRoute.indexOf('#') > 0
                    && $rootScope.nextRoute.indexOf('logout') < 0) {

                    $location.path($rootScope.nextRoute.substr($rootScope.nextRoute.indexOf('#') + 1));
                    $rootScope.nextRoute = undefined;

                } else {

                    // default page
                    $location.path('/');

                }

                return success;

            }, function (error) {

                $scope.loginError = true;

                return error;

            });

    };

});