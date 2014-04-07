<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/include.jsp" %>

<div id="buildsListSummary">
</div>
<div id="buildsList" style="display: none;">
    <table id="buildsTable" class="buildsListTable">
        <thead>
        <tr>
            <th></th>
            <th>Project Name</th>
            <th>Build Type</th>
            <th>Agent</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
</div>
