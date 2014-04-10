(function () {
    var view = allBuildsView();
    var buildsPerPage = 50;
    var builds = [];

    $j(document).ready(function () {

        /**
         * Not optimal for now
         */
        function getBuildById(id) {
            for (var i = 0; i < builds.length; i++) {
                if (builds[i].id === id){
                    return builds[i];
                }
            }
        }

        function loadCurrentBuilds() {
            function processLoadedBuilds(response) {
                if (response.count === 0) {
                    view.displayNoBuildsFound();
                } else {
                    for (var i = 0; i < response.count; i++) {
                        var build = response.build[i];
                        builds.push(build);
                        view.insertToTheEndOfTable(build);
                    }
                    view.displayShownBuildsCount(builds.length);
                }
            }

            $j.getJSON(window['base_uri'] + "/httpAuth/app/rest/builds/?" +
                "fields=count,build(id,number,startDate,finishDate,state,status,statusText,buildType,agent,webUrl)" +
                "&locator=running:any,personal:any,canceled:any,count:" + buildsPerPage, processLoadedBuilds)
        }

        function processNewBuild(build) {
            builds.unshift(build);
            if (builds.length >= buildsPerPage) {
                builds.pop();
                view.removeLastRow();
            }
            view.displayShownBuildsCount(builds.length);
            view.insertToTheTopOfTable(build);
        }

        function processBuildUpdate(message) {
            var build = getBuildById(message.build.id);
            if (build) {
                if (build.status !== message.build.status || build.state !== message.build.state) {
                    build.status = message.build.status;
                    build.state = message.build.state;
                    view.changeBuildStatus(build);
                }
                if (build.statusText !== message.build.statusText) {
                    build.statusText = message.build.statusText;
                    view.changeBuildStatusText(build);
                }
                if (message.type === 'FINISHED'){
                    build.finishDate = message.build.finishDate;
                    view.displayBuildDuration(build);
                }
            }
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
            } else {
                processBuildUpdate(message);
            }
        };

        atmosphere.subscribe(atmosphereRequest);
    });
})();