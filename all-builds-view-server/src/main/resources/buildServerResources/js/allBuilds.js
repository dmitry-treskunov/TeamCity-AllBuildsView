(function () {
    var buildsPerPage = 20;
    var shownBuildsCount = 0;

    $j(document).ready(function () {

        function createBuildRow(build) {
            return '' +
                '<tr id="build_' + build.id + '">' +
                    '<td>#' + build.number + '</td> ' +
                    '<td><a href=/project.html?projectId=' + build.buildType.projectId + '>' + build.buildType.projectName + '</a></td> ' +
                    '<td><a href=/viewType.html?buildTypeId=' + build.buildType.id + '>' + build.buildType.name + '</a></td> ' +
                    '<td><a href=/agentDetails.html?agentTypeId=' + build.agent.typeId + '&id=' + build.agent.typeId + '>' + build.agent.name + '</a></td> ' +
                    '<td id="buildStatus_' + build.id + '">' + build.statusText + '</td> ' +
                '</tr>'
        }

        function insertToTheEndOfTable(build) {
            $j("#buildsList>tbody").append(createBuildRow(build))
        }

        function insertToTheTopOfTable(build) {
            $j("#buildsList>tbody>tr:first").before(createBuildRow(build))
        }

        function changeBuildStatus(build) {
            $j("#buildStatus_" + build.id).text(build.statusText);
        }

        function removeLastRow() {
            $j("#buildsList>tbody>tr:last").remove();
        }

        function loadBuilds() {
            $j.getJSON("/httpAuth/app/rest/builds/?" +
                "fields=count,build(number,statusText,buildType,agent,webUrl)" +
                "&locator=running:any,personal:any,canceled:any,count:" + buildsPerPage, function (data) {
                shownBuildsCount = data.count;
                for (var i = 0; i < data.count; i++) {
                    insertToTheEndOfTable(data.build[i]);
                }
            })
        }

        function insertNewBuild(build) {
            if (shownBuildsCount >= buildsPerPage) {
                removeLastRow();
            } else {
                shownBuildsCount++;
            }
            insertToTheTopOfTable(build);
        }

        var atmosphereRequest = {
            url: '/subscribeToBuildsUpdate.html',
            contentType: "application/json",
            logLevel: 'debug',
            transport: "websocket",
            fallbackTransport: 'long-polling'
        };

        atmosphereRequest.onOpen = function (response) {
            console.log('Atmosphere connected using ' + response.transport);
            loadBuilds();
        };

        atmosphereRequest.onMessage = function (response) {
            var message = JSON.parse(response.responseBody);
            if (message.type === 'buildStarted') {
                insertNewBuild(message.build);
            } else if (message.type === 'statusUpdated') {
                changeBuildStatus(message.build);

            }
        };

        atmosphere.subscribe(atmosphereRequest);
    });
})();