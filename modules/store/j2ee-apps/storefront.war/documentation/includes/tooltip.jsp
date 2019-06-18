<section id="tooltip-docs" class="docs-section">


	<h2>Tooltip</h2>
	<p>A utility to show a tooltip when the user clicks or hovers over a trigger icon/element. This feature delivers immediate and localized info to the user without needing a modal or other heavy UI interaction.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<p>Hover on icon to launch the tooltip. <span data-tooltip class="has-tip tip-right round icon icon-info" title="Hello. My name is Tooltip Montoya. You killed my father. Prepare to die."><span class="sr-only">Hello. My name is Tooltip Montoya. You killed my father. Prepare to die.</span></span></p>
	</div>

	<p>The tooltip text is added by way of the <code>title</code> attribute and inside a <code>sr-only</code> class for <a href="index.jsp/accessibility-docs">accessibility</a>.</p>

	<h4 >HTML</h4>
	<format:prettyPrint>
    <jsp:attribute name="htmlString">
			<span data-tooltip class="has-tip tip-bottom round icon icon-info" title="Hello. My name is Tooltip Montoya. You killed my father. Prepare to die."><span class="sr-only">Hello. My name is Tooltip Montoya. You killed my father. Prepare to die.</span></span>
    </jsp:attribute>
  </format:prettyPrint>

	<p>You can specify how the tooltip looks and behaves by adding classes to add to the icon/element.</p>
	<ul>
		<li>The first position (ex: <code>has-tip</code>) gives the icon a tooltip.</li>
		<li>The second postion (ex: <code>tip-bottom</code>) defines which side of the icon displays the tooltip</li>
			<ul>
				Options:
				<li><code>tip-top</code></li>
				<li><code>tip-bottom</code></li>
				<li><code>tip-left</code></li>
				<li><code>tip-right</code></li>
			</ul>
		<li>The third postion (ex: <code>round</code>) defines whether the tooltip will have rounded corners or square: </li>
			<ul>
				Options:
				<li><code>radius</code></li>
				<li><code>round</code></li>
			</ul>
		<li>The classes <code>icon icon-info</code> define which icon is used to activate the tooltip.</li>
	</ul>


	<h4 >Javascript</h4>
<pre class="prettyprint">
See plugin: kp.tooltip.js
</pre>

	<p>Further styles and colors can be adjusted/added in the _tooltip.scss file.</p>

	<h4>SASS</h4>
<pre class="prettyprint">
.has-tip {
	cursor: pointer;
	&:hover, &:focus {}
	&.tip-left, &.tip-right {
		float: none !important;
	}
}

.tooltip {
	// accessibility
	background: $medium-gray;
	color: $white;
	display: none;
	font-size: 0.875rem;
	font-weight: normal;
	line-height: 1.3;
	max-width: 300px;
	padding: 0.75rem;
	position: absolute;
	width: 100%;
	z-index: 1006;
	left: 50%;

	& > .nub {
		border-color: transparent transparent $medium-gray transparent;
		border: solid 5px;
		display: block;
		height: 0;
		pointer-events: none;
		position: absolute;
		top: -10px;
		width: 0;
		left: 5px;
	}
	&.radius {
		border-radius: 3px;
	}
	&.round {
		border-radius: 6px;
	}
	&.round > .nub {
		left: 2rem;
	}
	&.opened {
		border-bottom: dotted 1px $deep-blue !important;
		color: $bright-blue !important;
	}
}

.tap-to-close {
	color: $deep-gray;
	display: block;
	font-size: 0.625rem;
	font-weight: normal;
}

@include media(medium-up){
	.tooltip {
		& > .nub {
			border-color: transparent transparent $medium-gray transparent;
			top: -10px;
		}
		&.tip-top > .nub {
			border-color: $medium-gray transparent transparent transparent;
			bottom: -10px;
			top: auto;
		}
		&.tip-left, &.tip-right {
			float: none !important;
		}
		&.tip-left > .nub {
			border-color: transparent transparent transparent $medium-gray;
			left: auto;
			margin-top: -5px;
			right: -10px;
			top: 50%;
		}
		&.tip-right > .nub {
			border-color: transparent $medium-gray transparent transparent;
			left: -10px;
			margin-top: -5px;
			right: auto;
			top: 50%;
		}
	}
}
</pre>

</section>
