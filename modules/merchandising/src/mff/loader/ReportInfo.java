package mff.loader;

public class ReportInfo{
	private String	itemName;
	private String	itemType;
	private String	itemID;
	private String	message;
	
	public ReportInfo(String itemType, String itemID, String itemName, String message){
		this.itemID = itemID;
		this.itemName = itemName;
		this.itemType = itemType;
		this.message = message;	
	}

	public String getItemName(){
		return itemName;
	}

	public void setItemName( String itemName ){
		this.itemName = itemName;
	}

	public String getItemType(){
		return itemType;
	}

	public void setItemType( String itemType ){
		this.itemType = itemType;
	}

	public String getItemID(){
		return itemID;
	}

	public void setItemID( String itemID ){
		this.itemID = itemID;
	}

	public String getMessage(){
		return message;
	}

	public void setMessage( String message ){
		this.message = message;
	}

}
