<dsp:page>
	
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/mff/email/ContactUsFormHandler"/>
	<dsp:importbean bean="/com/mff/email/MFFEmailManager"/>

	<div class="section-title">
		<h1>CONTACT US</h1>
	</div>

	<div class="section-row">
		<div class="required-note">* Required</div>
		<div>
		<dsp:form method="post" action="contactUsForm.jsp" id="contact-us-form" formid="contact-us-form" data-validate>
			<div class="field-group">
				<label for="first">* First Name</label>
				<dsp:input bean="ContactUsFormHandler.editValue.firstName" id="first" name="first" type="text" priority="14" autocapitalize="off" data-validation="required name" data-fieldname="First Name" placeholder="First Name" value=""/>
			</div>
	
			<div class="field-group">
				<label for="last">* Last Name</label>
				<dsp:input bean="ContactUsFormHandler.editValue.lastName" id="last" name="last" type="text" priority="13" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" placeholder="Last Name" value=""/>
			</div>
	
			<div class="field-group">
				<label for="address">Address</label>
				<dsp:input bean="ContactUsFormHandler.editValue.address" id="address" name="address" priority="12" type="text" autocapitalize="off" data-validation="address" data-fieldname="Address" placeholder="Address" value=""/>
			</div>
	
			<div class="field-group">
				<label for="address2">Address 2 (optional)</label>
				<dsp:input bean="ContactUsFormHandler.editValue.address2" id="address2" name="address2" priority="11" type="text" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="Address 2 (optional)" value=""/>
			</div>
	
			<div class="field-group">
				<label for="city">City</label>
				<dsp:input bean="ContactUsFormHandler.editValue.city" id="city" name="city" priority="10" type="text" autocapitalize="off" data-validation="alphaspace" data-fieldname="City" placeholder="City" value=""/>
			</div>
	
			<div class="field-group">
				<div class="field">
					<label for="state">State</label>
					<dsp:select bean="ContactUsFormHandler.editValue.state" id="state" priority="9" data-fieldname="State" nodefault="true">
						<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
					</dsp:select>
				</div>
				<div class="field">
					<label for="zip">Zip Code</label>
					<dsp:input bean="ContactUsFormHandler.editValue.postalCode" id="zip" name="zip" type="text" priority="8" autocapitalize="off" data-validation="uspostal" data-fieldname="Zip Code" placeholder="Zip Code" value=""/>
				</div>
			</div>
	
			<div class="field-group">
				<label for="phone">* Phone</label>
				<dsp:input bean="ContactUsFormHandler.editValue.phoneNumber" id="phone" name="phone" type="text" priority="7" autocapitalize="off" data-validation="required usphone" data-fieldname="Phone" placeholder="Area Code and Number" value=""/>
			</div>
	
			<div class="field-group">
				<label for="email">* Email</label>
				<dsp:input bean="ContactUsFormHandler.editValue.contact-us-email" id="contact-us-email" name="contact-us-email" type="text" priority="6" autocapitalize="off" data-validation="required email" data-fieldname="contact-us-email" placeholder="Email" value="" maxlength="255"/>
			</div>
	
			<div class="field-group">
				<label for="store">* Store Location Visited</label>
				<dsp:select bean="ContactUsFormHandler.editValue.storeName" id="store" name="store" priority="5"data-validation="required" data-fieldname="store" nodefault="true">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" bean="MFFEmailManager.contactUsStoreDropdownName" />
						<dsp:param name="sortProperties" value="_key"/>
						<dsp:param name="elementName" value="storeValue"/>
						<dsp:oparam name="outputStart">
							<option value="">Select your store</option>
							<option value="fleetfarmdotcom">FleetFarm.com</option>
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="keyValue" param="key" />
							<dsp:getvalueof var="storeName" param="storeValue" />
							<option value="${keyValue}">${storeName}</option>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</div>
	
			<div class="field-group">
				<label for="subjectSelect">* Area of Interest</label>
				<dsp:select bean="ContactUsFormHandler.editValue.areaOfInterest" id="subjectSelect" name="areaOfInterest" iclass="subject-select" priority="4" data-validation="required" data-fieldname="subjectSelect" nodefault="true">
					<option value="">Select your message topic</option>
					<option value="fleetFarm">FleetFarm.com</option>
					<option value="tires">Tires &amp; Auto Center</option>
					<option value="orderingShippingTracking">Ordering, Shipping &amp; Tracking</option>
					<option value="returns">Returns</option>
					<option value="products">Products</option>
					<option value="stores">Retail Stores</option>
					<option value="suppliers">Suppliers</option>
					<option value="rewardsCC">Fleet Rewards Credit Card</option>
					<option value="corporate">Corporate Information</option>
					<option value="weeklyAd">Weekly Ad</option>
				</dsp:select>
			</div>
	
			<%-- in order to show/hide fields with subjectSelect above, make sure div ids in hidden fields below are the same as option values in the select --%>
	
			<div id="fleetFarm" class="option-div">
				<div class="field-group">
					<label for="fleetFarm">* Topic</label>
					<select id="fleetFarm" class="sub-select" name="fleetFarm" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Trouble Logging In</option>
						<option value="two">Manage Account</option>
						<option value="three">Email Subscriptions</option>
						<option value="four">Website Issues</option>
					</select>
				</div>
			</div>
	
			<div id="orderingShippingTracking" class="option-div">
				<div class="field-group">
					<label for="orderingShippingTracking">* Topic</label>
					<select id="orderingShippingTracking" class="sub-select" name="orderingShippingTracking" data-validation="required" data-fieldname="Topic">
						<option value="">Select a topic</option>
						<option value="one">Order Information</option>
						<option value="two">Gift Cards</option>
						<option value="three">Order Tracking</option>
						<option value="four">General Shipping Questions</option>
					</select>
				</div>
			</div>
	
			<div id="returns" class="option-div">
				<div class="field-group">
					<label for="returns">* Topic</label>
					<select id="returns" class="sub-select" name="returns" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Damaged</option>
						<option value="two">Incorrect Item</option>
						<option value="three">Incorrect Quantity</option>
						<option value="four">Missing Parts</option>
						<option value="five">Refund Status</option>
					</select>
				</div>
			</div>
	
			<div id="products" class="option-div">
				<div class="field-group">
					<label for="products">* Topic</label>
					<select id="products" class="sub-select" name="products" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Inquiry</option>
						<option value="two">Availability</option>
						<option value="three">Recommendation</option>
					</select>
				</div>
			</div>
	
			<div id="stores" class="option-div">
				<div class="field-group">
					<label for="stores">* Topic</label>
					<select id="stores" class="sub-select" name="stores" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Experience Feedback</option>
						<option value="two">Recommendations</option>
					</select>
				</div>
			</div>
	
			<div id="tires" class="option-div">
				<div class="field-group">
					<label for="tires">* Topic</label>
					<select id="tires" class="sub-select" name="tires" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Experience Feedback</option>
						<option value="two">Inquiry</option>
					</select>
				</div>
			</div>
	
			<div id="suppliers" class="option-div">
				<div class="field-group">
					<label for="suppliers">* Topic</label>
					<select id="suppliers" class="sub-select" name="suppliers" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Inquiry</option>
					</select>
				</div>
			</div>
	
			<div id="rewardsCC" class="option-div">
				<div class="field-group">
					<label for="rewardsCC">* Topic</label>
					<select id="rewardsCC" class="sub-select" name="rewardsCC" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Inquiry</option>
					</select>
				</div>
			</div>
	
			<div id="corporate" class="option-div">
				<div class="field-group">
					<label for="corporate">* Topic</label>
					<select id="corporate" class="sub-select"  name="corporate" priority="" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Inquiry</option>
						<option value="two">Feedback</option>
						<option value="three">Donations</option>
					</select>
				</div>
			</div>
			<div id="weeklyAd" class="option-div">
				<div class="field-group">
					<label for="weeklyAd">* Topic</label>
					<select id="weeklyAd" class="sub-select"  name="weeklyAd" priority="" data-validation="required" data-fieldname="Topic" nodefault="true">
						<option value="">Select a topic</option>
						<option value="one">Weekly Ads &amp; Print Mail</option>
					</select>
				</div>
			</div>
			<%-- end change fields --%>
			<dsp:input type="hidden" bean="ContactUsFormHandler.editValue.topic" id="topic" name="topic" priority="3"/>
	
			<div class="field-group">
				<label for="subject">* Subject</label>
				<dsp:input id="subject" bean="ContactUsFormHandler.editValue.subject" iclass="contact-us-subject" name="subject" type="text" priority="2" autocapitalize="off" data-validation="required subject" data-fieldname="subject" placeholder="Subject" value="" readonly="true"/>
			</div>
	
			<div class="field-group">
				<label for="comments">* Comments</label>
				<dsp:textarea id="comments" bean="ContactUsFormHandler.editValue.comments" type="text" rows="4" cols="50" autocapitalize="off" data-validation="required comments" priority="1" data-fieldname="comments" placeholder="Your comments" value=""/></textarea>
			</div>
			<dsp:input type="hidden" bean="ContactUsFormHandler.contactUsEmailSuccessURL" name="contactUsEmailSuccessURL" id="contactUsEmailSuccessURL" priority="-8" value="${contextPath}/content/json/contactUsSuccess.jsp"/>
			<dsp:input type="hidden" bean="ContactUsFormHandler.contactUsEmailErrorURL" name="contactUsEmailErrorURL" id="contactUsEmailErrorURL" priority="-9" value="${contextPath}/content/json/contactUsError.jsp"/>
	
			<dsp:input id="login-submit" bean="ContactUsFormHandler.contactUsEmail" name="login-submit" type="submit" priority="-10" class="button primary" value="Submit" />
	
		</dsp:form>
		</div>
	</div>	
</dsp:page>
