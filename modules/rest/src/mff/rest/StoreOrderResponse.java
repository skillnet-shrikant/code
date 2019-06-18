package mff.rest;

import java.util.List;

/**
 * 
 * @author vsingh
 *
 */
public class StoreOrderResponse {
	
	private int totalNumbeOfPages;
	private int currentPageNumber;
	private int requestPageNumber;
	private int numberofRecordsPerPage;
	private int totalRecords;
	private List<StoreOrder> storeOrder;
	
	public int getTotalNumbeOfPages() {
		return totalNumbeOfPages;
	}
	public void setTotalNumbeOfPages(int totalNumbeOfPages) {
		this.totalNumbeOfPages = totalNumbeOfPages;
	}
	
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}
	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}
	
	public int getRequestPageNumber() {
		return requestPageNumber;
	}
	public void setRequestPageNumber(int requestPageNumber) {
		this.requestPageNumber = requestPageNumber;
	}
	
	public int getNumberofRecordsPerPage() {
		return numberofRecordsPerPage;
	}
	public void setNumberofRecordsPerPage(int numberofRecordsPerPage) {
		this.numberofRecordsPerPage = numberofRecordsPerPage;
	}
	
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public List<StoreOrder> getStoreOrder() {
		return storeOrder;
	}
	public void setStoreOrder(List<StoreOrder> storeOrder) {
		this.storeOrder = storeOrder;
	}
	
}
