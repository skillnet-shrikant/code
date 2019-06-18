<%--
  - File Name: gcInfoModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal shows a user where to find the access number on their gift card
  --%>

<layout:ajax>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	
	<c:set var="gcHeaderText">Gift Card Access Number</c:set>
	<c:set var="gcText">gift card</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Card Access Number</c:set>
		<c:set var="gcText">card</c:set>
	</c:if>
	
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">giftCardInfoModal</jsp:attribute>
	<jsp:body>

		<div class="gift-card-info-modal">

			<div class="modal-header">
				<h2>${gcHeaderText}</h2>
			</div>

			<div class="modal-body">
				<p>
					The Access Number is located on the back right of your ${gcText} where it reads "Access
					Number". You will have to scratch off to reveal.
				</p>
				<img src="${contextPath}/resources/images/gc-info.jpg" class="gift-card-info" alt="Back of Gift Card" />
			</div>

		</div>

	</jsp:body>
</layout:ajax>
