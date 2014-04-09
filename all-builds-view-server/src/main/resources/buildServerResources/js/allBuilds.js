(function () {
    var view = allBuildsView();
    var buildsPerPage = 50;
    var shownBuildsCount = 0;

    $j(document).ready(function () {

        function loadCurrentBuilds() {
            function processLoadedBuilds(response) {
                if (response.count == 0) {
                    view.displayNoBuildsFound();
                } else {
                    shownBuildsCount = response.count;
                    view.displayShownBuildsCount(shownBuildsCount);
                    for (var i = 0; i < response.count; i++) {
                        view.insertToTheEndOfTable(response.build[i]);
                    }
                }
            }

            $j.getJSON(window['base_uri'] + "/httpAuth/app/rest/builds/?" +
                "fields=count,build(id,number,startDate,finishDate,state,status,statusText,buildType,agent,webUrl)" +
                "&locator=running:any,personal:any,canceled:any,count:" + buildsPerPage, processLoadedBuilds)
        }

        function processNewBuild(build) {
            if (shownBuildsCount >= buildsPerPage) {
                view.removeLastRow();
            } else {
                shownBuildsCount++;
            }
            view.displayShownBuildsCount(shownBuildsCount);
            view.insertToTheTopOfTable(build);
        }

        var atmosphereRequest = {
            url: window['base_uri'] + '/subscribeToBuildsUpdate.html',
            contentType: "application/json",
            logLevel: 'debug',
            shared: true,
            trackMessageLength : true,
            transport: "websocket",
            fallbackTransport: 'long-polling'
        };

        atmosphereRequest.onOpen = function (response) {
            console.log('Atmosphere connected using ' + response.transport);
            loadCurrentBuilds();
        };

        atmosphereRequest.onMessage = function (response) {
            console.log(response.responseBody);
            var message = JSON.parse(response.responseBody);
            if (message.type === 'STARTED') {
                processNewBuild(message.build);
            } else if (message.type === 'UPDATED') {
                view.changeBuildStatus(message.build);
            } else if (message.type === 'FINISHED') {
                view.showBuildFinished(message.build);
            }
        };

        atmosphere.subscribe(atmosphereRequest);
    });
})();