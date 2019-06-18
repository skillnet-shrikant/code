<%--
 This page encodes the search results as JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/updateCategoryInSearchForm.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ page errorPage="/error.jsp" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags"  %>

<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %>

<dsp:page>
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
<dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
   <dsp:droplet name="SharingSitesDroplet">
   <dsp:oparam name="output">
     <dsp:getvalueof var="sites" param="sites"/>
   </dsp:oparam>
   </dsp:droplet>

  <dsp:droplet name="CategoryLookup">
    <dsp:param bean="RequestLocale.locale" name="repositoryKey"/>
    <dsp:param name="id" param="categoryId"/>
    <dsp:param name="sites" value="${sites}"/>
    <dsp:param name="elementName" value="category"/>
    <dsp:oparam name="output">
      {
        categoryName: '<dsp:valueof param="category.displayName"/>'
      }
    </dsp:oparam>
  </dsp:droplet>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/updateCategoryInSearchForm.jsp#1 $$Change: 946917 $--%>
