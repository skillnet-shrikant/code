<%--
 This page defines the Customer Tax Exemption Panel
--%>
<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
	<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

		<dspel:importbean var="profile"
			bean="/atg/userprofiling/ServiceCustomerProfile" />
		<dspel:importbean var="UIConfig"
			bean="/atg/svc/agent/ui/AgentUIConfiguration" />
		<dspel:getvalueof var="taxExemptions" vartype="java.lang.Object"
			bean="ServiceCustomerProfile.taxExemptions" />

		<link type="text/css" href="${UIConfig.contextRoot}/css/maincsc.css"
			rel="stylesheet" />

		<script type="text/javascript">
			if (!dijit.byId("exemptionPopup")) {
				new dojox.Dialog({
					id : "exemptionPopup",
					cacheContent : "false",
					executeScripts : "true",
					scriptHasHooks : "true",
					"class": "atg_commerce_csr_popup"});
			}
		</script>

		<script type="text/javascript">
			if (!dijit.byId("exemptionDeletePopup")) {
				new dojox.Dialog({
					id : "exemptionDeletePopup",
					cacheContent : "false",
					executeScripts : "true",
					scriptHasHooks : "true",
					style : "display:none;"
				});
			}
		</script>

		<%-- This section header and link --%>
		<fmt:message key="customer.addresses.addAddress.label"
			var="addAddressLabel" />
		<div id="atg_service_customerinfo_addresses_subPanel"
			class="atg_svc_subPanel">
			<div class="atg_svc_subPanelHeader">
				<ul class="atg_svc_panelToolBar">
					<li class="atg_svc_header">
						<h4 id="atg_commerce_csr_customerinfo_addresses">Tax
							Exemptions</h4>
					</li>

					<%-- Check max tax exempts allowed --%>
					<c:if test="${fn:length(taxExemptions)<6}">
						<li class="atg_svc_last"><svc-ui:frameworkPopupUrl
								var="exemptionEdit" value="/include/addresses/exemptionEditor.jsp"
								context="/agent" windowId="${windowId}" /> <a href="#"
							class="atg_svc_popupLink"
							onClick="showPopupWithResults({
                  popupPaneId: 'exemptionPopup',
                  title: 'Add Exemption',
                  url: '${exemptionEdit}',
                  onClose: function( args ) {
                    if ( args.result == 'save' ) {
                      atgSubmitAction({
                        panels : ['customerInformationPanel'],
                        panelStack : ['customerPanels','globalPanels'],
                        form : dojo.byId('transformForm')
                      });
                    }
                  }});
                  return false;">
								Add New Tax Exemption </a></li>
					</c:if>
				</ul>
			</div>

			<%-- This section panel content and links remove and edit --%>
			<c:choose>
				<c:when test="${ ! empty taxExemptions }">
					<c:forEach var="taxExemption" items="${taxExemptions}">
						<div class="atg_svc_customerInfo_addresses">
							<dspel:getvalueof var="nickName" value="${taxExemption.key}" />
							<dspel:setvalue param="taxExemptionVal"
								value="${taxExemption.value}" />
							<dspel:getvalueof var="classification"
								param="taxExemptionVal.classificationName" />
							<dspel:getvalueof var="taxId" param="taxExemptionVal.taxId" />
							<dspel:getvalueof var="currentTaxExmpId"
								param="taxExemptionVal.repositoryId" />
							<div class="card">
								<div class="card-title">${nickName}</div>
								<div class="card-content">
									<p>
										<strong>Classification:</strong> ${classification}
									</p>
									<p>
										<strong>Tax ID:</strong> ${taxId}
									</p>
								</div>
							</div>
							<div class="card-links">
								<ul class="atg_svc_shipAddressControls default">
									<li><svc-ui:frameworkPopupUrl var="exemptionDeleteURL"
											value="/include/addresses/exemptionDeleter.jsp"
											context="/agent" nickname="${nickName}"
											windowId="${windowId}" /> <a href="#"
										class="atg_tableIcon atg_propertyDelete"
										title="<fmt:message key='address.delete.mouseover'/>"
										onClick="showPopupWithResults({
								              popupPaneId: 'exemptionDeletePopup',
								              url: '${exemptionDeleteURL}',
								              title: 'Delete Exemption',
								              onClose: function( args ) {
								                if ( args.result == 'delete' ) {
								                  atgSubmitAction({
								                    panels : ['customerInformationPanel'],
								                    panelStack : ['customerPanels','globalPanels'],
								                    form : dojo.byId('transformForm')
								                  });
								                }
								              }
								            }); return false;">
											Delete</a></li>
								</ul>
							</div>
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="emptyLabel">There are no tax exemptions
						associated with this customer</div>
				</c:otherwise>
			</c:choose>
		</div>
	</dspel:layeredBundle>
</dspel:page>
