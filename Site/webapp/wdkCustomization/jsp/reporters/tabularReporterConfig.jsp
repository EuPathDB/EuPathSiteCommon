<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>
<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ taglib prefix="nested" uri="http://struts.apache.org/tags-nested" %>

<jsp:useBean id="websiteRelease" class="org.eupathdb.common.controller.WebsiteReleaseConstants"/>

<%-- get wdkAnswer from requestScope --%>
<c:set value="${requestScope.wdkStep}" var="wdkStep"/>
<c:set var="wdkAnswer" value="${wdkStep.answerValue}" />
<c:set var="format" value="${requestScope.wdkReportFormat}"/>

<%-- display page header --%>
<imp:pageFrame title="Create and download a Report in Tabular Format">

<%-- galaxy.psu.edu users; to send data to Galaxy  --%>
<script type="text/javascript">
function appendchecked(form, url) {
    var configForm = document.downloadConfigForm;
    var newtxt = 'primary_key';
    var chkbx = configForm.selectedFields;
    for (var i = 0; i < chkbx.length; i++) {
       if( chkbx[i].value != "primary_key" && (chkbx[i].type=="hidden" ||
               (chkbx[i].type == 'checkbox' && chkbx[i].checked === true))) {
           newtxt += ',' + chkbx[i].value;
       }
    }
    // append includeHeader value
    var includeHeader = "includeHeader=" +
      jQuery(configForm.includeHeader).filter(":checked").val();

    form.URL.value = url + newtxt + "&" + includeHeader;
}
</script>
<%-- end galaxy.psu.edu users  --%>

<%-- display the parameters of the question, and the format selection form --%>
<imp:reporter/>

<%-- handle empty result set situation --%>
<c:choose>
  <c:when test='${wdkAnswer.resultSize == 0}'>
    No results for your query
  </c:when>
  <c:otherwise>

<h4 id="tab-reporter-header">Below select type and format for download
<c:if test="${!empty sessionScope.GALAXY_URL}">
, including the option to <span class="galaxy">send results to Galaxy</span>.
</c:if>
</h4>

<form name="downloadConfigForm" method="get" action="<c:url value='/getDownloadResult.do' />" >
        <c:if test="${param.signature != null}">
           <input type="hidden" name="signature" value="${param.signature}" />
        </c:if>
<table id="download-columns">
  <tr>
      <td></td>
      <td>
        <input type="hidden" name="step" value="${step_id}">
        <input type="hidden" name="wdkReportFormat" value="${format}">

        <table>
 					<tr><td id="column-title">You may include additional columns in the report</td></tr> 

          <c:if test="${wdkAnswer.useCheckboxTree}">
            <tr>
              <td>
							  <div id="download-featureID">
                		 <input type="checkbox" name="selectedFields" value="${wdkAnswer.recordClass.primaryKeyAttribute.name}" checked="checked"/>
							  		 <span>${wdkAnswer.recordClass.primaryKeyAttribute.displayName}</span>
							  		 <div id="overlay"></div>
								</div>
							  <div id="tree-column">
                  <imp:checkboxTree id="selectedFieldsCBT" tree="${wdkAnswer.reportMakerAttributeTree}" 
																		checkboxName="selectedFields" showSelectAll="false" showResetCurrent="true" useHelp="true"/>
								</div>
              </td>
            </tr>
          </c:if>

<!--  HOW/WHEN DOES THIS GET USED?
				  <c:set var="numPerLine" value="2"/>		
          <c:if test="${not wdkAnswer.useCheckboxTree}">
			      <c:set var="attributeFields" value="${wdkAnswer.allReportMakerAttributes}"/>
			      <c:set var="numPerColumn" value="${fn:length(attributeFields) / numPerLine}"/>
			      <c:set var="i" value="0"/>
	          <tr>
	            <td colspan="${numPerLine}">
	              <input type="checkbox" name="selectedFields" value="default" onclick="wdk.uncheckFields(1);" checked>
	              Default (same as in <a href="showApplication.do">result</a>), or...
	            </td>
	          </tr>
	          <tr><td colspan="${numPerLine}">&nbsp;</td></tr>
	          <tr>
	            <td nowrap>
	              <c:forEach items="${attributeFields}" var="rmAttr">
	                <%-- this is a hack, why some reportMakerAttributes have no name? --%>
	                <c:choose>
	                  <c:when test="${rmAttr.name != null && rmAttr.name != ''}">
	                    <c:choose>
	                      <c:when test="${rmAttr.name eq 'primary_key'}">
	                        <input type="checkbox" checked="checked" disabled="true" >
	                        <input type="hidden" name="selectedFields" value="${rmAttr.name}" >
	                      </c:when>
	                      <c:otherwise>
	                        <input type="checkbox" name="selectedFields" value="${rmAttr.name}" onclick="wdk.uncheckFields(0);">
	                      </c:otherwise>
	                    </c:choose>
	                        ${rmAttr.displayName}
	                    <c:set var="i" value="${i+1}"/>
	                    <c:choose>
	                      <c:when test="${i >= numPerColumn}">
	                        <c:set var="i" value="0"/>
	                        </td><td nowrap>
	                      </c:when>
	                      <c:otherwise>
	                        <br />
	                      </c:otherwise>
	                    </c:choose>
	                  </c:when>
	                  <c:otherwise>
	                    <!-- <td><html:multibox property="selectedFields">junk</html:multibox>junk</td>${br} -->
	                  </c:otherwise>
	                </c:choose>
	              </c:forEach>
	            </td>
	          </tr>
            <tr>
              <td align="center" colspan="${numPerLine}"><input type="button" value="select all" onclick="wdk.checkFields(1)">
                <input type="button" value="clear all" selected="yes" onclick="wdk.checkFields(0)">
              </td>
            </tr>
	        </c:if>
-->
        </table>
      </td>
    </tr>

  <tr><td valign="top"><b>Column names: </b></td>
      <td><input type="radio" name="includeHeader" value="yes" checked>include
          <input type="radio" name="includeHeader" value="no" >exclude
        </td></tr>
  <tr><td title="Will open new tabs"><b>Download type and format: </b></td>
      <td>
          <input type="radio" name="downloadType" value="text">Text File
          <input type="radio" name="downloadType" value="excel">Excel File**
          <input type="radio" name="downloadType" value="plain" checked>Show in Browser
					<html:submit property="downloadConfigSubmit" value="Get Report"/>
        </td></tr>
</table>
</form>

<c:if test="${!empty sessionScope.GALAXY_URL}">
  <c:url var='downloadPath' 
           value='/getDownloadResult.do;jsessionid=${pageContext.session.id}?step=${step_id}&downloadType=plain&wdkReportFormat=tabular&selectedFields='/>
  <c:set var='downloadUrl'>
      ${pageContext.request.scheme}://${pageContext.request.serverName}${downloadPath}
  </c:set>

  <table id="download-galaxy">
  <tr><td title="Will open new tabs"><b>Alternatively</span> you may:</b>

</td><td>

<c:choose>
<c:when test="${requestScope.WEBSITE_RELEASE_STAGE eq websiteRelease.www}">
    <form target="public-galaxy" action="${sessionScope.GALAXY_URL}" name="galaxy_exchange" id="galaxy_exchange" method="POST">
      <input type="hidden" name="URL" value="${fn:escapeXml(downloadUrl)}">
      <input type="submit" name="Send" value="Send to Public Galaxy" onclick="appendchecked(this.form, '${fn:escapeXml(downloadUrl)}')">
    </form>
</c:when>
<c:otherwise>
    <form target="eupathdb-galaxy" action="${sessionScope.EUPATHDB_GALAXY_URL}" name="eu_galaxy_exchange" id="eu_galaxy_exchange" method="POST">
      <input type="hidden" name="URL" value="${fn:escapeXml(downloadUrl)}">
      <input type="submit" name="Send" value="Send to EuPathDB Galaxy" onclick="appendchecked(this.form, '${fn:escapeXml(downloadUrl)}')">
    </form>
</c:otherwise>
</c:choose>

  </td></tr>
</table>
</c:if>

<br><hr>
<i>**Note: if you choose "Excel File" as Download Type, you can only download a maximum 10M (in bytes) of the results and the rest will be discarded. Opening a huge Excel file may crash your system. If you need to get the complete results, please choose "Text File" or "Show in Browser".</i>

  </c:otherwise>
</c:choose>

</imp:pageFrame>
