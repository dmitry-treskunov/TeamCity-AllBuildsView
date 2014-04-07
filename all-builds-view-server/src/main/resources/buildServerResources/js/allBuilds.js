(function () {
    var buildsPerPage = 20;
    var shownBuildsCount = 0;

    $j(document).ready(function () {

        function createBuildRow(build) {
            return '' +
                '<tr id="build_' + build.id + '">' +
                '<td>' + build.number + '</td> ' +
                '<td>' + build.buildTypeId + '</td> ' +
                '<td>' + build.status + '</td> ' +
                '<td id="buildState_' + build.id + '">' + build.state + '</td> ' +
                '</tr>'

        }

        function insertToTheEndOfTable(build) {
            $j("#buildsList>tbody").append(createBuildRow(build))
        }

        function insertToTheTopOfTable(build) {
            $j("#buildsList>tbody>tr:first").before(createBuildRow(build))
        }

        function changeBuildState(build) {
            $j("#buildState_" + build.id).text(build.state);
        }

        function removeLastRow() {
            $j("#buildsList>tbody>tr:last").remove();
        }

        function loadBuilds() {
            $j.getJSON("/httpAuth/app/rest/builds/?locator=count:" + buildsPerPage, function (data) {
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
            if (message.type === 'started') {
                insertNewBuild(message.build);
            } else if (message.type === 'finished') {
                changeBuildState(message.build);

            }
        };

        atmosphere.subscribe(atmosphereRequest);
    });
})();