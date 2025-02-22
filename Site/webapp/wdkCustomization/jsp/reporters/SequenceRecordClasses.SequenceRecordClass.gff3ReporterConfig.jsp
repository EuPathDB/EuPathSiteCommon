<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>
<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ taglib prefix="nested" uri="http://struts.apache.org/tags-nested" %>

<%-- get wdkAnswer from requestScope --%>
<c:set value="${requestScope.wdkStep}" var="wdkStep"/>
<c:set var="wdkAnswer" value="${wdkStep.answerValue}" />
<c:set var="format" value="${requestScope.wdkReportFormat}"/>


<%-- display page header --%>
<imp:pageFrame title="Create and download a Report in GFF3 Format">

<%-- display the parameters of the question, and the format selection form --%>
<imp:reporter/>

<%-- display description for page --%>
<h3>Generate a report of your query result in GFF3 format. </h3>


<%-- handle empty result set situation --%>
<c:choose>
  <c:when test='${wdkAnswer.resultSize == 0}'>
    No results for your query
  </c:when>
  <c:otherwise>

<%-- content of current page --%>
<form name="downloadConfigForm" method="get" action="<c:url value='/getDownloadResult.do' />">
        <c:if test="${param.signature != null}">
           <input type="hidden" name="signature" value="${param.signature}" />
        </c:if>
        <input type="hidden" name="step" value="${wdkStep.stepId}"/>
        <input type="hidden" name="wdkReportFormat" value="${format}"/>
    <table width="100%">
        <tr>
            <td valign="top" nowrap><b>Download Type</b>: 
                <input type="radio" name="downloadType" value="text">GFF File
                <input type="radio" name="downloadType" value="plain" checked>Show in Browser
            </td>
        </tr>
        <tr>
            <td  style="text-align:center">
                <html:submit property="downloadConfigSubmit" value="Get Report"/>
            </td>
        </tr>
    </table>
</form>

  </c:otherwise>
</c:choose>

</imp:pageFrame>
