<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
  <dsp:param name="formhandler" bean="/com/mff/email/ContactUsFormHandler" />
  <json:object>
    <%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
  </json:object>
</dsp:page>