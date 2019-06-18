<%--
 This custom page defines the Popup to display the Preview/Print label page
--%>
<%@ include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
	<dsp:layeredBundle basename="atg.svc.agent.WebAppResources">
	<dsp:getvalueof var="line1" param="line1"/>
	<dsp:getvalueof var="line2" param="line2"/>
	<dsp:getvalueof var="line3" param="line3"/>
	
	<c:url var="labelFileURL" context="${CSRConfigurator.contextRoot}"
			value="/renderers/order/mffLabel.label">
	</c:url>
	
	<script type="text/javascript"
		src="<c:out value='${CSRConfigurator.contextRoot}'/>/script/DYMO.Label.Framework.1.2.4.js"></script>

	<script type="text/javascript">
		function loadImage(line1, line2, line3) {
			var labelXml = getXMLLabel();
			try 
			{
				var label = dymo.label.framework.openLabelXml(labelXml);
				label.setObjectText("addressText",line1+"\n"+line2+"\n"+line3);
				updatePreview(label);
			}
			catch(err) {
				errorMessage.innerHTML = "Preview - " + err;
			    errorMessage.style.color = 'red';
			    return false;
			}
		}

		function printShippingLabel(labelFileURL, line1, line2, line3) {
			var errorMessage = document.getElementById('errorMessage');
			try {
				var printers = dymo.label.framework.getPrinters();
				if (printers.length == 0) throw "No DYMO printers are installed. Expecting a Dymo Label Writer 450 Turbo available.";
				var printerName = "Not found";
				for (var i = 0; i < printers.length; ++i) {
					var printer = printers[i];
					if (printer.printerType == "LabelWriterPrinter") {
						printerName = printer.name;
						break;
					}
				}
				var labelXml = getXMLLabel();
				var label = dymo.label.framework.openLabelXml(labelXml);
				label.setObjectText("addressText",line1+"\n"+line2+"\n"+line3);
				var paramsXml = dymo.label.framework.createLabelWriterPrintParamsXml({	copies : 1	});
				dymo.label.framework.printLabel(printerName, paramsXml, label.toString());
			}
			catch(err) {
				errorMessage.innerHTML = "Print - " + err;
			    errorMessage.style.color = 'red';
			    return false;
			}
		}

		function updatePreview(label) {
			if (!label)
				return;
			var pngData = label.render();
			var labelImage = document.getElementById('labelImage');
			labelImage.src = "data:image/png;base64," + pngData;
		}
		
		function getXMLLabel() {
			var labelXml = '<DieCutLabel Version="8.0" Units="twips"><PaperOrientation>Landscape</PaperOrientation>\
				<Id></Id><PaperName>30323 Shipping</PaperName><DrawCommands>\
				<RoundRectangle X="0" Y="0" Width="3060" Height="5715" Rx="270" Ry="270" /></DrawCommands>\
				<ObjectInfo>\
					<TextObject>\
				             <Name>addressText</Name>\
				             <ForeColor Alpha="255" Red="0" Green="0" Blue="0" />\
				             <BackColor Alpha="0" Red="255" Green="255" Blue="255" />\
				             <LinkedObjectName></LinkedObjectName>\
				             <Rotation>Rotation0</Rotation>\
				             <IsMirrored>False</IsMirrored>\
				             <IsVariable>False</IsVariable>\
				             <HorizontalAlignment>Left</HorizontalAlignment>\
				             <VerticalAlignment>Top</VerticalAlignment>\
				             <TextFitMode>AlwaysFit</TextFitMode>\
				             <UseFullFontHeight>False</UseFullFontHeight>\
				             <Verticalized>False</Verticalized>\
				             <StyledText>\
				                 <Element>\
				                     <String>BARCODE</String>\
				                     <Attributes>\
				                         <Font Family="Arial" Size="8" Bold="False" Italic="False" Underline="False" Strikeout="False" />\
				                         <ForeColor Alpha="255" Red="0" Green="0" Blue="0" />\
				                     </Attributes>\
				                 </Element>\
				             </StyledText>\
					</TextObject>\
					<Bounds X="357" Y="810" Width="5000" Height="1440" />\
				</ObjectInfo>\
				</DieCutLabel>';
			return labelXml;
		}
	</script>
	<form action="" method="post" id="printLabelForm">
		<img id="labelImage" src=""/><BR> 
		<p id="errorMessage"></p><BR>
		<input type="button" id="printButton" value="Print Label"
				onClick="printShippingLabel('${labelFileURL}','${line1}','${line2}','${line3}');" />
	</form>
	<script type="text/javascript">
		loadImage('${line1}','${line2}','${line3}');
	</script>

	</dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
      ee.printStackTrace();
  %>
</c:if>