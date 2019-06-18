package com.googleadwords.beans;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CDATAAdapter extends XmlAdapter<String,String> {

	@Override
	public String unmarshal(String pV) throws Exception {
		return "<![CDATA[" + pV + "]]>";
	}

	@Override
	public String marshal(String pV) throws Exception {
		return pV;
	}

}
