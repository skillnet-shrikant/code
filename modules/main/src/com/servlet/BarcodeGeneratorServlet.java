package com.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class BarcodeGeneratorServlet extends DynamoServlet {

	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
		String lOrderNumber = pReq.getParameter("orderNumber");
		pRes.setContentType("image/x-png");
		AbstractBarcodeBean codeBean = new Code128Bean();
        codeBean.setHeight(5);
        codeBean.setBarHeight(8);
        codeBean.setFontSize(1.6);
        codeBean.setModuleWidth(UnitConv.in2mm(8.0f / 900));
        BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(pRes.getOutputStream(), "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        codeBean.generateBarcode(canvasProvider, lOrderNumber);
        canvasProvider.finish();
		
	}

	
}
