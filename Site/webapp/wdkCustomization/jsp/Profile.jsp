<%@ taglib prefix="imp" tagdir="/WEB-INF/tags/imp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="w" uri="http://www.servletsuite.com/servlets/wraptag" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ taglib prefix="logic" uri="http://struts.apache.org/tags-logic" %>
<%@ taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="wdkModel" value="${applicationScope.wdkModel}"/>

<imp:pageFrame title="${wdkModel.displayName} :: Update User Profile" >

  <script language="JavaScript" type="text/javascript">
    <!--
       function validateFields(e)
       {
       if (typeof e != 'undefined' && !enter_key_trap(e)) {
       return;
       }

       if (document.profileForm.email.value != document.profileForm.confirmEmail.value) {
       alert("the email does not match.");
       document.profileForm.email.focus();
       return email;
       } else if (document.profileForm.firstName.value == "") {
       alert('Please provide your first name.');
       document.profileForm.firstName.focus();
       return false;
       } else if (document.profileForm.lastName.value == "") {
       alert('Please provide your last name.');
       document.profileForm.lastName.focus();
       return false;
       } else if (document.profileForm.organization.value == "") {
       alert('Please provide the name of the organization you belong to.');
       document.profileForm.organization.focus();
       return false;
       } else {
       document.profileForm.submit();
       return true;
       }
       }
       //-->
  </script>

  <!-- show error messages, if any -->
  <imp:errors/>

  <!-- get user object from session scope -->
  <c:set var="wdkUser" value="${sessionScope.wdkUser}"/>
  <c:set var="global" value="${wdkUser.globalPreferences}"/>

  <!-- display the success information, if the user registered successfully -->
  <c:if test="${requestScope.profileSucceed != null}">
    <p><font color="blue">Your profile has been updated successfully.</font></p><br/>
  </c:if>

  <html:form method="POST" action='/processProfile.do' >

    <c:if test="${requestScope.refererUrl != null}">
      <input type="hidden" name="refererUrl" value="${requestScope.refererUrl}" />
    </c:if>

    <table width="650" border="0">
      <tr>
        <th colspan="2"> User Profile </th>
      </tr>

      <c:choose>
        <c:when test="${wdkUser == null || wdkUser.guest == true}">

          <tr>
            <td colspan="2">Please login to view or update your profile.</td>
          </tr>

        </c:when>

        <c:otherwise>

          <!-- check if there is an error message to display -->
          <c:if test="${requestScope.profileError != null}">
            <tr>
              <td colspan="2">
                <font color="red">${requestScope.profileError}</font>
              </td>
            </tr>
          </c:if>

          <tr>
            <td colspan="2" align="right">
              <a href="<c:url value='/showPassword.do'/>"><img border="0" src="<c:url value='/images/change_pwd.gif'/>"></a>
            </td>
          </tr>

          <tr>
            <td align="right" width="50%" nowrap><font color="red">*</font> Email: </td>
            <td align="left"><input type="text" name="email" value="${wdkUser.email}" size="20"></td>
          </tr>
          <tr>
            <td align="right" width="50%" nowrap><font color="red">*</font> Re-type email: </td>
            <td align="left"><input type="text" name="confirmEmail" value="${wdkUser.email}" size="20"></td>
          </tr>
          <tr>
            <td colspan="2" align="left"><hr><b>User Information:</b></td>
          </tr>
          <tr>
            <td align="right" width="50%" nowrap><font color="red">*</font> First Name: </td>
            <td align="left"><input type="text" name="firstName" value="${wdkUser.firstName}" size="20"></td>
          </tr>
          <tr>
            <td align="right" width="50%" nowrap>Middle Name: </td>
            <td align="left"><input type="text" name="middleName" value="${wdkUser.middleName}" size="20"></td>
          </tr>
          <tr>
            <td align="right" width="50%" nowrap><font color="red">*</font> Last Name:</td>
            <td align="left"><input type="text" name="lastName" value="${wdkUser.lastName}" size="20"></td>
          </tr>
          <tr>
            <td align="right" width="50%" nowrap><font color="red">*</font> Institution:</td>
            <td align="left"><input type="text" name="organization" value="${wdkUser.organization}" size="50"></td>
          </tr>
          <tr>
            <td colspan="2" align="left"><hr><b>Preferences:</b></td>
          </tr>
          <tr>
            <td align="right">Items in the query result page:</td>
            <td>
              <select name="preference_global_items_per_page">
                <option value="5" ${(global['preference_global_items_per_page'] == 5)? 'SELECTED' : ''}>5</option>
                <option value="10" ${(global['preference_global_items_per_page'] == 10)? 'SELECTED' : ''}>10</option>
                <option value="20" ${(global['preference_global_items_per_page'] == 20)? 'SELECTED' : ''}>20</option>
                <option value="50" ${(global['preference_global_items_per_page'] == 50)? 'SELECTED' : ''}>50</option>
                <option value="100" ${(global['preference_global_items_per_page']== 100)? 'SELECTED' : ''}>100</option>
              </select>
            </td>
          </tr>

          <tr>
            <td colspan="2" align="right">
              <a href="#" onclick="return validateFields();">
                <img  border="0" src="<c:url value='/images/update_profile.gif'/>"/>
              </a>
            </td>
          </tr>

        </c:otherwise>

      </c:choose>

    </table>
  </html:form>

</imp:pageFrame>
