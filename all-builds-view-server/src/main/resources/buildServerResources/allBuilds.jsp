<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/include.jsp" %>

<bs:page>
   <jsp:attribute name="head_include">
        <script type="application/javascript" src="${teamcityPluginResourcesPath}/lib/atmosphere.js"></script>
        <script type="application/javascript" src="${teamcityPluginResourcesPath}/lib/moment.min.js"></script>
        <script type="application/javascript" src="${teamcityPluginResourcesPath}/js/allBuildsView.js"></script>
        <script type="application/javascript" src="${teamcityPluginResourcesPath}/js/allBuilds.js"></script>

        <link rel="stylesheet" href="${teamcityPluginResourcesPath}/css/allBuilds.css">
   </jsp:attribute>
   <jsp:attribute name="body_include">
          <div id="buildsListSummary">
          </div>
        <div id="buildsList" style="display: none;">
            <table id="buildsTable" class="buildsListTable">
                <thead>
                <tr>
                    <th></th>
                    <th>Build Configuration</th>
                    <th>Agent</th>
                    <th>Start Date</th>
                    <th>Duration</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
   </jsp:attribute>
</bs:page>
