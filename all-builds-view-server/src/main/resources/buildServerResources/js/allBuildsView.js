var allBuildsView = function () {

    function createBuildsStatusIcon(build) {
        var icon;
        var iconText;

        if (build.state === 'running') {
            if (build.status === 'SUCCESS') {
                icon = 'running_green_transparent.gif';
                iconText = 'Build is running';
            } else {
                icon = 'running_red_transparent.gif';
                iconText = 'Build is failing';
            }
        } else {
            if (build.status === 'SUCCESS') {
                icon = 'buildSuccessful.png';
                iconText = 'Build was successful';
            } else {
                icon = 'buildFailed.png';
                iconText = 'Build failed';
            }
        }

        return $j('<img/>').attr({
            src: window['base_uri'] + '/img/buildStates/' + icon,
            alt: build.statusText
        });
    }

    function formatDate(date) {
        return moment(date, "YYYYMMDDThhmmss Z").format("DD MMM YY hh:mm");
    }

    function createDurationText(build) {
        if (build.finishDate) {
            var diff = moment(build.finishDate, "YYYYMMDDThhmmss Z").diff(moment(build.startDate, "YYYYMMDDThhmmss Z"));
            var duration = moment.duration(diff);
            var message = "";
            if (duration.hours() > 0) {
                message += duration.hours() + "h "
            }
            if (duration.minutes() > 0) {
                message += duration.minutes() + "m "
            }
            if (duration.seconds() > 0) {
                message += duration.seconds() + "s"
            }
            return message;
        } else {
            return "";
        }
    }

    function createBuildRow(build) {
        var row = $j('<tr></tr>').attr({ id: 'build_' + build.id});
        $j('<td></td>').text('#' + build.number).appendTo(row);

        var projectLink = $j('<a></a>').text(build.buildType.projectName).attr( {href: window['base_uri'] + '/project.html?projectId=' + build.buildType.projectId });
        var buildTypeLink = $j('<a></a>').text(build.buildType.name).attr( {href: window['base_uri'] + '/viewType.html?buildTypeId=' + build.buildType.id });
        $j('<td></td>').append(projectLink).append(' :: ').append(buildTypeLink).appendTo(row);

        var agentLink = $j('<a></a>').text(build.agent.name).attr( {href: window['base_uri'] + '/agentDetails.html?agentTypeId=' + build.agent.typeId + '&id=' + build.agent.typeId });
        $j('<td></td>').prepend(agentLink).appendTo(row);

        $j('<td></td>').text(formatDate(build.startDate)).appendTo(row);

        $j('<td></td>').text(createDurationText(build)).attr({id: 'buildFinishDate_' + build.id}).appendTo(row);

        var buildLink = $j('<a></a>').
            text(build.statusText).
            attr( {
                href: window['base_uri'] + '/viewLog.html?buildId=' + build.id + '&buildTypeId=' + build.buildType.id ,
                id: 'buildStatus_' + build.id
            }).
            prepend(createBuildsStatusIcon(build));
        $j('<td></td>').prepend(buildLink).appendTo(row);

        return row;
    }

    function showBuildsTableIfHidden() {
        $j("#buildsList").show();
    }

    return {
        displayShownBuildsCount: function (count) {
            $j("#buildsListSummary").text("Last " + count + " builds are shown.");
        },

        displayNoBuildsFound: function () {
            $j("#buildsListSummary").text("There are no builds yet.");
        },

        insertToTheEndOfTable: function (build) {
            showBuildsTableIfHidden();
            $j("#buildsTable>tbody").append(createBuildRow(build))
        },

        insertToTheTopOfTable: function (build) {
            showBuildsTableIfHidden();
            $j("#buildsTable>tbody").prepend(createBuildRow(build))
        },

        changeBuildStatus: function (build) {
            $j("#buildStatus_" + build.id).text(build.statusText).prepend(createBuildsStatusIcon(build));
        },

        showBuildFinished: function (build) {
            this.changeBuildStatus(build);
            $j("#buildFinishDate_" + build.id).text(createDurationText(build));
        },

        removeLastRow: function () {
            $j("#buildsTable>tbody>tr:last").remove();
        }
    }
};