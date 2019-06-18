<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/capture/tfPicker.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">  
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:importbean var="editorActionFormHandler" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler"/>
<dspel:importbean var="tfService" bean="/atg/svc/repository/service/TransactionalFragmentService"/>
<caf:outputXhtml targetId="${editorActionFormHandler.fieldId}DialogCell">
  <table width="100%" border="0" class="dialogTable">
    <tr>
      <td colspan="3" class="borderDashedBottom">
        <span><fmt:message key='transactionFragment.select'/></span>
      </td>
    </tr>
    <tr>
      <td id="<c:out value="${editorActionFormHandler.fieldId}"/>_pickerCell" class="borderDashedBottom borderDashedRight">
        <table width="100%" border="0" cellPadding="0" cellSpacing="0">
          <c:forEach var="tf" items="${tfService.allTransactionalFragments}" varStatus="loop">
            <c:choose>
              <c:when test="${loop.index%2 == 0}">
                <tr onclick="selectTF('<c:out value="${editorActionFormHandler.fieldId}"/>','<c:out value="${tf.absoluteName}"/>')" 
                    id="<c:out value="${editorActionFormHandler.fieldId}"/>_<c:out value='${tf.absoluteName}'/>"
                    class="bgLightBlue">
              </c:when>
              <c:otherwise>
                <tr onclick="selectTF('<c:out value="${editorActionFormHandler.fieldId}"/>','<c:out value="${tf.absoluteName}"/>')" 
                    id="<c:out value="${editorActionFormHandler.fieldId}"/>_<c:out value='${tf.absoluteName}'/>">
              </c:otherwise>
            </c:choose>
              <td id="<c:out value="${editorActionFormHandler.fieldId}"/>_TFCheck_<c:out value='${tf.absoluteName}'/>" valign="middle">
                <img id="<c:out value="${editorActionFormHandler.fieldId}"/>_TFCheckImg_<c:out value='${tf.absoluteName}'/>" src="<c:out value='${imageLocation}' />/iconcatalog/21x21/table_icons/icon_checkMark.gif" 
                     width="25" height="22" style="visibility:hidden;">
              </td>
              <td id="<c:out value="${editorActionFormHandler.fieldId}"/>_TF_<c:out value='${tf.absoluteName}'/>" width="100%" valign="middle">
                &nbsp;
                <a href="#" id="<c:out value="${editorActionFormHandler.fieldId}"/>_TFDisplayName_<c:out value='${tf.absoluteName}'/>">
                  <c:out value="${tf.displayName}"/>
                </a>
                <div style="display:none;" id="<c:out value="${editorActionFormHandler.fieldId}"/>_TFDescription_<c:out value='${tf.absoluteName}'/>">
                  <span><fmt:message key='transactionFragment.description'/>" "<c:out value="${tf.displayName}"/>":</span><br/><br/>
                  <c:out value="${tf.description}"/>
                </div>
                <div style="display:none;" id="<c:out value="${editorActionFormHandler.fieldId}"/>_TFParameters_<c:out value='${tf.absoluteName}'/>">
                  <table border=0>
                  <c:forEach var="paramObj" items="${tf.parameters}">
                    <c:if test="${not paramObj.hidden}">
                      <tr>
                        <td align="right">
                          <c:out value="${paramObj.label}"/>:&nbsp;
                        </td>
                        <td align="left">
                          <c:choose>
                            <c:when test="${paramObj.type == 'ListType'}">
                              <select id="<c:out value='${paramObj.name}'/>"
                                      name="<c:out value="${editorActionFormHandler.fieldId}"/>_TFParamValue_<c:out value='${tf.absoluteName}'/>_<c:out value='${paramObj.name}'/>"
                                      param="<c:out value='${paramObj.name}'/>">
                                <c:forEach var="listObj" items="${paramObj.listValues}">
                                  <option value="<c:out value='${listObj.value}'/>"><c:out value="${listObj.displayName}"/></option>
                                </c:forEach>
                              </select>
                            </c:when>
                            <c:otherwise>
                              <input value="<c:out value='${paramObj.defaultValue}'/>" 
                                    id="<c:out value='${paramObj.name}'/>" type="text"
                                    name="<c:out value="${editorActionFormHandler.fieldId}"/>_TFParamValue_<c:out value='${tf.absoluteName}'/>_<c:out value='${paramObj.name}'/>">
                              </input>
                            </c:otherwise>
                          </c:choose>
                        </td>
                      </tr>
                    </c:if>
                  </c:forEach>
                  </table>
                </div>
              </td>
            </tr>
          </c:forEach>
        </table>
      </td>
      <td width="50%" vAlign="top" class="contentText borderDashedBottom"
          id="<c:out value="${editorActionFormHandler.fieldId}"/>_descriptionCell"></td>
    </tr>
    <tr>
      <td id="<c:out value="${editorActionFormHandler.fieldId}"/>_parameterCell" colspan="2" class="borderDashedBottom"></td>
    </tr>
    <tr>
      <td id="updateCell" colspan="2" align="right">        
        <a href="#" onclick="UpdateTF('<c:out value="${editorActionFormHandler.fieldId}"/>')" class="buttonSmallinsert" title="<fmt:message key='property.edit.ok'/>"><span><fmt:message key="property.edit.ok"/></span></a>
        <a href="#" onclick="CancelTF('<c:out value="${editorActionFormHandler.fieldId}"/>')" class="buttonSmallinsert" title="<fmt:message key='property.edit.ok'/>"><span><fmt:message key="property.edit.cancel"/></span></a>
      </td>
    </tr>
  </table>
</caf:outputXhtml> 
<caf:outputJavaScript>
  InitializeTF('<c:out value="${editorActionFormHandler.fieldId}"/>');
</caf:outputJavaScript>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/capture/tfPicker.jsp#1 $$Change: 946917 $--%>
