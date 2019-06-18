<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
  <dsp:param name="formhandler" bean="/atg/userprofiling/ForgotPasswordHandler" />
  <json:object>
    <%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
  </json:object>
</dsp:page>
