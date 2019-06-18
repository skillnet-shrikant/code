<section id="forms-docs" class="docs-section">
	<h2>Forms</h2>
	<p>Provides normalized form field appearances and <a href="presentation.jsp#form-validation-docs">field validation message formats</a>. Form labels are displayed above form elements so that zoom on small screens does not put labels off canvas. Form elements should have labels for accessibility. If you have a form that is designed without labels, provide the labels with the <code>sr-only</code> class so that they are <a href="index.jsp#accessibility-docs">accessible to screen readers</a>.</p>

	<p>Form elements fill 100% of the width of their container. Form elements can display side by side, by default the display is in 2 columns. Override this for instances where you need more than two fields next to each other.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<form>
			<div class="field-group">
				<div class="field">
					<label for="firstName">First Name</label>
					<input type="text" id="firstName" autocorrect="off" autocapitalize="on"/>
				</div>
				<div class="field">
					<label for="lastName">Last Name</label>
					<input type="text" id="lastName" autocorrect="off" autocapitalize="on"/>
				</div>
			</div>
			<div class="field-group">
				<label for="exampleEmailAddress">Email Address</label>
				<input id="exampleEmailAddress" type="email" placeholder="email" autocorrect="off" autocapitalize="off"/>
			</div>
			<div class="field-group">
				<label for="exampleSelect">Select an avatar</label>
				<select id="exampleSelect">
					<option value="">Default select</option>
				</select>
			</div>
			<input type="submit" value="Submit" class="button primary"/>
		</form>
	</div>

	<p>The <code>inline-form</code> class can be used for formatting of a form with a single input. This will place the field and the button side-by-side. There is a subclass <code>rounded</code> that will round the outside edges of the pair.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<form>
			<div class="field-group inline-form">
				<label for="exampleInputPassword1">Search</label>
				<input type="text" autocorrect="off" autocapitalize="on"/>
				<input type="hidden" name="testHiddenField" value="hi"/>
				<input type="submit" value="Submit" class="button primary"/>
			</div>
		</form>
		<form>
			<div class="field-group inline-form rounded">
				<label for="exampleInputPassword1">Search</label>
				<input type="text" autocorrect="off" autocapitalize="on"/>
				<input type="hidden" name="testHiddenField" value="hi"/>
				<input type="submit" value="Submit" class="button primary"/>
			</div>
		</form>
	</div>


	<p>The <code>field-note</code> class can be used to provide helpful information (longer than a placeholder would hold)
		for filling out a form. </p>

	<div class="docs-example">
		<h4>Example</h4>
		<form>
			<div class="field-group">
				<label for="exampleInputPassword1">Password</label>
				<input id="exampleInputPassword1" type="password" placeholder="Password" aria-describedby="password-instructions"/>
				<div class="field-note" id="password-instructions">
					<p>Passwords should be over 8 characters long, include upper and lower case letters and contain at least 1 number or special character.</p>
				</div>
			</div>
		</form>
	</div>

	<p>Checkboxes and radios display as a list. There is a <code>disabled</code> class that can be used to give the label
		a disabled treatment if needed.</p>
	<div class="docs-example">
		<h4>Example</h4>
		<form data-validate>

			<div class="field-group">
				<span class="form-label">Checkbox heading</span>
				<div class="checkbox">
					<label for="exampleCheckbox1">
						<input type="checkbox" id="exampleCheckbox1">
						Check this checkbox.
					</label>
				</div>
			</div>
			<div class="field-group">
				<span class="form-label">Radio group heading</span>
				<div class="radio">
					<label for="optionsRadios1">
						<input type="radio" checked value="option1" id="optionsRadios1" name="optionsRadios">
						This is your first option
					</label>
				</div>
				<div class="radio">
					<label for="optionsRadios2">
						<input type="radio" value="option2" id="optionsRadios2" name="optionsRadios">
						this is your second option
					</label>
				</div>
				<div class="radio disabled">
					<label for="optionsRadios3">
						<input type="radio" disabled="" value="option3" id="optionsRadios3" name="optionsRadios">
						The final option is disabled
					</label>
				</div>
			</div>

		</form>
	</div>


</section>
