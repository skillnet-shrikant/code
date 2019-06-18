<%--
  - File Name: productDetailSpecialMsgModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal show the special message for product
  --%>
<dsp:page>

	<%-- Params --%>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:droplet name="ProductLookup">
		<dsp:param name="id" param="productId"/>
		<dsp:param name="filterByCatalog" value="false"/>
		<dsp:param name="filterBySite" value="false"/>
		<dsp:param name="elementName" value="productItem"/>
		<dsp:oparam name="output">
			<layout:ajax>
				<jsp:attribute name="pageType">productDetailSpecialMsgModal</jsp:attribute>
				<jsp:body>
					<div class="product-special-msg-modal">
			
						<div class="modal-header">
							<h2><dsp:valueof param="productItem.splMsgTitle" valueishtml="true"/></h2>
						</div>
			
						<div class="modal-body">
							<p><dsp:valueof param="productItem.splMsg" valueishtml="true"/></p>
						</div>
			
						<div class="modal-footer">
							<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
						</div>
			
					</div>
			
				</jsp:body>
			</layout:ajax>
			
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
