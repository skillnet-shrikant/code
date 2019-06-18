<%--
 This page defines the progress bar panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/progressbar/progressBar.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dspel:page xml="true">

<dspel:droplet name="/atg/progressbar/UpdateProgressBarDroplet">
  <dspel:param name="state" param="state"/>
  <dspel:param name="process" param="process"/>
  <dspel:param name="progressBar" value="/atg/progressbar/ProgressBar"/>
</dspel:droplet>

  <dspel:importbean var="progressBar" bean="/atg/progressbar/ProgressBar"/>
  <%-- now render the state --%>
  <div class="panelContent" id="GuideActions">
    <ul class="atg_commerce_csr_progressPanel">

    <c:forEach var="root" items="${progressBar.rootItems}">
      <li class="${root.current ? 'atg_commerce_csr_progressTitleCurrent' : 'atg_commerce_csr_progressTitle'}">${root.name}
        <c:if test="${root.current}">
          <ul class="atg_commerce_csr_progressPanel atg_commerce_csr_progressSubPanel">
            <c:forEach var="child" items="${root.children}">
              <c:if test="${child.visited}">
                <c:if test="${child.complete}">
                  <c:set var="childStyle" value="atg_commerce_csr_progressComplete"/>
                </c:if>
                <c:if test="${!child.complete}">
                  <c:set var="childStyle" value="atg_commerce_csr_progressInComplete"/>
                </c:if>
              </c:if>
              <c:if test="${!child.visited}">
                <c:set var="childStyle" value="atg_commerce_csr_progressInactive"/>
              </c:if>
              <c:if test="${child.current}">
                <c:set var="childStyle" value="${childStyle} atg_commerce_csr_progressCurrent"/>
                <li class="atg_navigationHighlight ${childStyle}" id="${child.name}" onclick="${child.visited ? child.onClick : ''}">${child.name}</li>
              </c:if>
              <c:if test="${!child.visited}">
                <li class="atg_navigationHighlight ${childStyle}" id="${child.name}" onclick="${child.visited ? child.onClick : ''}">${child.name}</li>
              </c:if>
              <c:if test="${!child.current && child.visited}">
                <li class="atg_navigationHighlight ${childStyle}" id="${child.name}" onclick="${child.visited ? child.onClick : ''}"><a href="#">${child.name}</a></li>
              </c:if>
            </c:forEach>
          </ul>
        </c:if>
      </li>
    </c:forEach>
    </ul>
  </div>

</dspel:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="Caught exception in progressBar.jsp: ${exception}" /><br />
  <%
    Throwable error = (Throwable)pageContext.getAttribute("exception");
    String stack = atg.core.exception.StackTraceUtils.getStackTrace(error, 10, 10);
    stack = atg.core.util.StringUtils.replace(stack, '\n', "<br/>");
    stack = atg.core.util.StringUtils.replace(stack, '\t', "&nbsp;&nbsp;&nbsp;");
    out.println(stack);
  %>
</c:if>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/progressbar/progressBar.jsp#1 $$Change: 946917 $--%>
