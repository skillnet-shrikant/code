<!doctype html>

<html lang="en">
	<head>
		<%@ include file="/sitewide/fragments/decoratorMeta.jspf" %>
	</head>
	<body>

		<div class="example-modal">

			<%-- title bar --%>
			<div class="modal-header">
				<div class="title-bar">
					<h2 class="title">I'm an HTML Modal!</h2>
				</div>
			</div>

			<%-- the body of the modal --%>
			<div class="modal-body">
				<p>This HTML modal content is displayed via <code>exampleModal.jsp</code>.</p>
			</div>

			<%-- modal buttons --%>
			<div class="modal-footer">
				<a href="" data-dismiss="modal" class="button secondary">Close</a>
			</div>
		</div>
	</body>
</html>