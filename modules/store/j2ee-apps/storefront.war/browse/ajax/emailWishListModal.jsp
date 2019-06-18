<%--
  - File Name: emailWishListModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows a user to email their Wish List to a friend
  --%>

<layout:ajax>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">emailWishListModal</jsp:attribute>

	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:getvalueof var="giftListId" param="giftListId"/>

	<jsp:body>

		<div class="email-wish-list-modal">

			<div class="modal-header">
				<h2>Share with a friend</h2>
			</div>

			<dsp:form id="email-wish-list-form" action="${originatingRequest.requestURI}" method="post" name="emailWishListForm" data-validate>

				<div class="modal-body">
					<%-- modal message --%>
					<p>
						Enter the information below and we'll send your Wish List to your friend.
					</p>

					<%-- email form --%>
					<div class="email-form">
						<div class="field-group">
							<label for="friend-email">Friend's Email Address</label>
							<dsp:input bean="GiftlistFormHandler.value.friendEmail" id="friend-email" name="friend-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Friend's Email" placeholder="Friend's Email Address"/>
						</div>
						<div class="field-group">
							<label for="your-name">Your Name</label>
							<dsp:input bean="GiftlistFormHandler.value.yourName" id="your-name" name="your-name" type="text" autocapitalize="off" data-validation="required" data-fieldname="Your Name" placeholder="Your Name"/>
						</div>
						<div class="field-group">
							<label for="your-email">Your Email Address</label>
							<dsp:input bean="GiftlistFormHandler.value.yourEmail" id="your-email" name="your-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Your Email Address" placeholder="Your Email Address"/>
						</div>
						<div class="field-group">
							<label for="message">Message</label>
							<dsp:textarea bean="GiftlistFormHandler.value.message" id="message" name="message" autocapitalize="off" placeholder="Type a message here" />
						</div>
					</div>

				</div>

				<div class="modal-footer">
					<div class="email-wish-list-submit">
						<dsp:input type="submit" id="email-wish-list-submit" bean="GiftlistFormHandler.shareWishlist" name="email-wish-list-submit" class="button primary expand" value="Send" />
						<dsp:input bean="GiftlistFormHandler.giftlistId" type="hidden" value="${giftListId}"/>
						<dsp:input bean="GiftlistFormHandler.shareWishlistSuccessURL" type="hidden" value="${contextPath}/account/json/wishListEmailSuccess.jsp"/>
						<dsp:input bean="GiftlistFormHandler.shareWishlistErrorURL" type="hidden" value="${contextPath}/account/json/wishListEmailError.jsp"/>
					</div>
				</div>

			</dsp:form>

		</div>

	</jsp:body>
</layout:ajax>
