angular.module('Eternity').controller('AssignToProjectCtrl',
    ['$scope', '$element', 'ServerPushService', 'ProjectService', 'user', 'close', 'title',
        function ($scope, $element, ServerPushService, ProjectService, user, close, title) {

            var ctrl = this;
            ctrl.user = user;

            $scope.isLoading = true;

            $scope.alerts = [];
            $scope.title = title;

            $scope.selectedProject = '';
            $scope.selectedUserProject = '';
            $scope.assignableProjects = [];
            $scope.assignedProjects = [];

            ServerPushService.on('project', $scope, function () {
                ctrl.loadProjects();
            });

            ctrl.loadProjects = function () {

                $scope.isLoading = true;

                ProjectService.getAssignProjectsForUser({identifier:ctrl.user.identifier},function (result) {

                    $scope.assignableProjects = result.assignableProjects;
                    $scope.assignedProjects = result.assignedProjects;

                    if (result.assignableProjects && result.assignableProjects.length > 0) {
                        $scope.selectedProject = result.assignableProjects[0].identifier;
                    }

                    if (result.assignedProjects && result.assignedProjects.length > 0) {
                        $scope.selectedUserProject = result.assignedProjects[0].identifier;
                    }

                    $scope.isLoading = false;

                }, function (error) {
                    createErrorAlert(error);
                });

            };

            $scope.addAlert = function (type, msg) {
                $scope.clearAlerts();
                $scope.alerts.push({type: type, msg: msg});
            };

            $scope.clearAlerts = function () {
                $scope.alerts.length = 0;
            };

            $scope.closeAlert = function (index) {
                $scope.alerts.splice(index, 1);
            };

            var createErrorAlert = function (error) {

                if (error.status === 500) {
                    $scope.addAlert('danger', error.statusText);
                } else {
                    $scope.addAlert('danger', error.statusText + "\n" + error.data.error);
                }

            };

            $scope.cancel = function () {

                //  Manually hide the modal.
                $element.modal('hide');

                //  Now call close, returning control to the caller.
                close(undefined, 500);

            };

            $scope.assignToProject = function () {
                console.log("assgin: " + $scope.selectedProject);
            };

            $scope.unassignToProject = function () {
                console.log("unassgin: " + $scope.selectedUserProject);
            };

            ctrl.loadProjects();

        }]);
