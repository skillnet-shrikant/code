<section id="form-validation-docs" class="docs-section">
	<h2>Form Validation</h2>
	<p>All fields should have client-side field validations with error messages.</p>

	<p>We're using the validate plugin found in <code>kp.validate.js</code>. Initialize the plugin by adding a <code>data-validate</code> attribute to the <code>form</code> element as shown below.</p>

	<p>For each validated field, add a <code>data-validation</code> attribute to the element with a space-separated list of validation rules (default rules can be found in the plugin). Validation rules handle a range of field input errors such as email, phone, zipcode or date patterns, input lengths, alpha-numeric requirements, etc.</p>

	<p>Optionally, you can add a <code>data-fieldname</code> attribute with a user-friendly name to display custom messages. See the email field below for an example of the fieldname property to include a field-specific message on blur.</p>

	<p>Error messages display beneath the form field in a <code>field-error-text</code> container.</p>

	<p>Note on the Example below: the First and Last Name fields have generic validation messages. The Email field has a custom message set with <code>data-fieldname</code>.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<form data-validate>
			<div class="field-group">
				<div class="field">
					<label for="firstName">First Name</label>
					<input id="firstName" name="firstName" type="text" placeholder="First Name" autocorrect="off" autocapitalize="on" data-validation="required name"/>
				</div>
				<div class="field">
					<label for="lastName">Last Name</label>
					<input id="lastName" name="lastName" type="text" placeholder="Last Name"autocorrect="off" autocapitalize="on" data-validation="required name"/>
				</div>
			</div>
			<div class="field-group">
				<label for="email">Email</label>
				<input id="email" name="email" type="tel" placeholder="Email" autocorrect="off" autocapitalize="off" data-validation="required email" data-fieldname="Email"/>
			</div>
			<div class="field-group">
				<label for="exampleSelect2">Select an avatar</label>
				<select id="exampleSelect2"	data-validation="required">
					<option value="">Default select</option>
					<option value="cat">Cat</option>
					<option value="dog">Dog</option>
					<option value="jackalope">Jackalope</option>
				</select>
			</div>
			<div class="field-group">
				<div id="agreement-group">
					<div class="checkbox">
						<label for="agree-check">
							<input type="checkbox" value="agree" id="agree-check" name="agree-check" data-validation="required" data-parent="#agreement-group">
							I agree to the terms of this agreement
						</label>
					</div>
				</div>
			</div>
			<input type="submit" value="Submit" class="button primary"/>
		</form>
	</div>

	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">

				<!-- text fields -->
			<form data-validate>
				<div class="field-group">
					<div class="field">
						<label for="myField">My Field</label>
						<input id="myField" name="myField" type="text" data-validation="required optionalSecondaryRule"/>
					</div>

					<div class="field">
						<label for="email">Email Example Field</label>
						<!-- The data-fieldname attribute allows you to add field-specific text in the error message
								"[data-fieldname] is required" is default empty field text but editable in kp.validate.js -->
						<input id="email" name="email" type="text" data-validation="required email" data-fieldname="Email"/>
					</div>
				</div>

				<!-- select field -->
				<div class="field-group">
					<label for="exampleSelect2">Select an avatar</label>
					<select id="exampleSelect2"	data-validation="required">
						<option value="">Default select</option>
						<option value="cat">Cat</option>
						<option value="dog">Dog</option>
						<option value="jackalope">Jackalope</option>
					</select>
				</div>

				<!-- checkbox field -->
				<div class="field-group">
					<div id="agreement-group">
						<div class="checkbox">
							<label for="agree-check">
								<input type="checkbox" value="agree" id="agree-check" name="agree-check" data-validation="required" data-parent="#agreement-group">
								I agree to the terms of this agreement
							</label>
						</div>
					</div>
				</div>

				<!-- submit button will automatically validate empty fields when clicked -->
				<input type="submit" value="Submit" class="button primary"/>
			</form>
		</jsp:attribute>
	</format:prettyPrint>

	<h4 >Javascript</h4>
	<pre class="prettyprint">
See plugin: kp.validate.js
	</pre>

	<p>Generic validations for common input fields such as phone, email, postal code, etc, are already defined in the validate plugin.</p>
	<p>Add custom validations (as testPattern checks) to the <code>rules</code> object in <code>kp.validate.js</code>. Error message keys for all validations live in <code>jsConstants.js</code>. Don't forget to add any further custom validation rule messages here as well.</p>

</section>
