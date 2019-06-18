<%--
- File Name: vimeo.jsp
- Author(s): KnowledgePath Solutions
- Copyright Notice:
- Description: This modal embeds a Vimeo video on the page
--%>
<dsp:page>
	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">vimeoModal</jsp:attribute>
		<jsp:body>
			<div class="vimeo-embed">
				<iframe id="vimeo-modal-iframe"
					src="https://player.vimeo.com/video/${param.id}?api=1&autoplay=1&playsinline=0&color=fd6316&byline=0&portrait=0&title=0"
					webkitallowfullscreen
					mozallowfullscreen
					allowfullscreen>
				</iframe>
			</div>
		</jsp:body>
	</layout:ajax>
</dsp:page>
