package mff.allocation;


public class PlanoGramInfoResponse {

	private String planogramInfo;
	private boolean showPlanogramInfo;
	private String recordSeparator;
	
	public String getPlanogramInfo() {
		return planogramInfo;
	}
	public void setPlanogramInfo(String planogramInfo) {
		this.planogramInfo = planogramInfo;
	}
	public boolean isShowPlanogramInfo() {
		return showPlanogramInfo;
	}
	public void setShowPlanogramInfo(boolean pShowPlanogramInfo) {
		showPlanogramInfo = pShowPlanogramInfo;
	}
	public String getRecordSeparator() {
		return recordSeparator;
	}
	public void setRecordSeparator(String pRecordSeparator) {
		recordSeparator = pRecordSeparator;
	}
	
}
