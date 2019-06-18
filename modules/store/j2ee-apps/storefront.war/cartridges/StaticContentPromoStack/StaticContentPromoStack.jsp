<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<div class="section-row">
		<c:if test="${not empty contentItem.sectionTitle}">
			<div class="subtitle">
				${contentItem.sectionTitle}
			</div>
		</c:if>
		<div class="two-column-content">
			<div class="two-col-left">
				<dsp:renderContentItem contentItem="${contentItem.mainLeftPromo}" />
			</div>
			<div class="two-col-right">
				<div class="two-col-right-top">
					<div class="nested-two-col-left">
						<dsp:renderContentItem contentItem="${contentItem.rightPromos[0]}" />
					</div>
					<div class="nested-two-col-right">
						<dsp:renderContentItem contentItem="${contentItem.rightPromos[1]}" />
					</div>
				</div>
				<div class="two-col-right-btm">
					<dsp:renderContentItem contentItem="${contentItem.rightBottomPromo}" />
				</div>
			</div>
		</div>
	</div>
</dsp:page>