<%--
 This page defines the navigation tree
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/navigationTree.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true" >
<dsp:importbean bean="/atg/commerce/custsvc/catalog/CustomCatalogProductSearch"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/ProductSearch"/>
<dsp:tomap bean="/atg/userprofiling/Profile" var="profile"/>

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    &nbsp;&nbsp;<b><fmt:message key="catalogBrowse.navigationTree.browseProducts"/></b>
    <div class="atg_comma_browseTree">
    <c:url var="treeURL" context="/WebUI"  value="/tree/treeFrame.jsp">
    <c:param name="_windowid"              value="${param['_windowid']}"/>
      <c:param name="styleSheet"           value="${CSRConfigurator.contextRoot}/css/csc.css"/>
      <c:param name="treeComponent"        value="/atg/commerce/custsvc/catalog/CatalogTreeState"/>
      <c:param name="onSelect"             value="atg.commerce.csr.catalog.nodeSelected"/>
      <c:param name="onSelectProperties"   value="URI,path"/>
      <c:param name="nodeIconRoot"         value=""/>
      <c:param name="showToolTips"         value="true"/>
      <c:param name="onCheckProperties"    value="childTypes,path"/>
      <c:param name="selectorControl"      value="none"/>
      <c:param name="onLoad"               value="atg.commerce.csr.catalog.restoreTreeState"/>
      <c:param name="shouldScrollIntoView" value="false"/>
    </c:url>
    <dsp:iframe id="treeContainer"
                  name="treeContainer"
                  src="${treeURL}"
                  scrolling="auto"
                  frameborder="0"
                  width="200px;"
                  height="360px;"
                  framespacing="0"
                  hspace="0"
                  vspace="0"
                  marginheight="0"
                  marginwidth="0" />
    </div>
    <script type="text/javascript">
      _container_.onLoadDeferred.addCallback(function () {
        var rootItem = atg.commerce.csr.catalog.getTreeFrame().getElementsByTagName("a")[0];
        if (rootItem) {
          if (!window.treeInfo) {
            window.treeInfo = {};
          }
          atg.commerce.csr.catalog.getTreeInfo().rootItemExpanded = rootItem.className != "openIcon";
          atg.commerce.csr.catalog.getTreeInfo().rootCategoryId = atg.commerce.csr.catalog.getTreeNodeCategoryId(rootItem.id);
        }
      });
    </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/navigationTree.jsp#1 $$Change: 946917 $--%>
