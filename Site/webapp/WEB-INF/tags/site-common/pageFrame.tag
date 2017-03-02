<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:imp="urn:jsptagdir:/WEB-INF/tags/imp"
    xmlns:common="urn:jsptagdir:/WEB-INF/tags/site-common">

  <jsp:directive.attribute name="title" required="false"
              description="Value to appear in page's title"/>

  <jsp:directive.attribute name="refer" required="false" 
              description="Page calling this tag"/>

  <jsp:directive.attribute name="banner" required="false"
              description="Value to appear at top of page if there is no title provided"/>

  <!-- jsp:output tag for doctype no longer supports simple HTML5 declaration -->
  <jsp:text>&lt;!DOCTYPE html&gt;</jsp:text>
  <html lang="en">
    <!--~~~~~~~ HEAD of HTML doc ~~~~~~~-->
    <imp:head title="${title}" refer="${refer}" banner="${banner}"/>
    <body class="${refer}">
      <imp:header refer="${refer}" title= "${title}" />
      <a name="skip" id="skip"><jsp:text/></a>
      <div class="eupathdb-PageContent eupathdb-PageContent__${refer}">
        <jsp:doBody/>
      </div>
      <imp:footer refer="${refer}"/>
      <common:IEWarning version="8"/>
      <imp:dialogs/>
    </body>
  </html>
</jsp:root>
