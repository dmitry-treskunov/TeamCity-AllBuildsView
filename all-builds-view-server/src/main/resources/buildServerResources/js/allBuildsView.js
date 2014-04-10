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

    function createDurationText(build) {
        if (build.finishDate) {
            var diff = parseDateFromServer(build.finishDate).diff(parseDateFromServer(build.startDate));
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

    function parseDateFromServer(dateStr) {
        return moment(dateStr, "YYYYMMDDThhmmss Z");
    }

    function formatDate(dateStr) {
        return parseDateFromServer(dateStr).format("DD MMM YY hh:mm");
    }

    function createBuildRow(build) {
        var row = $j('<tr></tr>');
        $j('<td></td>').text('#' + build.number).appendTo(row);

        var projectLink = $j('<a></a>').text(build.buildType.projectName).attr( {href: window['base_uri'] + '/project.html?projectId=' + build.buildType.projectId });
        var buildTypeLink = $j('<a></a>').text(build.buildType.name).attr( {href: window['base_uri'] + '/viewType.html?buildTypeId=' + build.buildType.id });
        $j('<td></td>').append(projectLink).append(' :: ').append(buildTypeLink).appendTo(row);

        var agentLink = $j('<a></a>').text(build.agent.name).attr( {href: window['base_uri'] + '/agentDetails.html?agentTypeId=' + build.agent.typeId + '&id=' + build.agent.typeId });
        $j('<td></td>').append(agentLink).appendTo(row);

        $j('<td></td>').text(formatDate(build.startDate)).appendTo(row);

        $j('<td></td>').text(createDurationText(build)).attr({id: 'buildDuration_' + build.id}).appendTo(row);

        var buildStatusText = $j('<span></span>').text(build.statusText).attr({id: 'buildStatusText_' + build.id });
        var buildStatusIcon = $j('<span></span>').append(createBuildsStatusIcon(build)).attr({id: 'buildStatusIcon_' + build.id});
        var buildLink = $j('<a></a>').
            attr({ href: window['base_uri'] + '/viewLog.html?buildId=' + build.id + '&buildTypeId=' + build.buildType.id }).
            append(buildStatusIcon).
            append(buildStatusText);
        $j('<td></td>').append(buildLink).appendTo(row);

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
            $j("#buildStatusIcon_" + build.id).html(createBuildsStatusIcon(build));
        },

        changeBuildStatusText: function (build) {
            $j("#buildStatusText_" + build.id).text(build.statusText);
        },

        displayBuildDuration: function (build) {
            $j("#buildDuration_" + build.id).text(createDurationText(build));
        },

        removeLastRow: function () {
            $j("#buildsTable>tbody>tr:last").remove();
        }
    }
};