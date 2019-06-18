package mff.task;

import atg.nucleus.GenericService;


/**
 * An abstract class that defines the contract for all
 * batch jobs.
 * @author KnowledgePath inc.
 */
public abstract class Task extends GenericService {

    /**
     * Executes the task. 
     */
    public abstract void doTask();
    
	/**
	 * @return True if the job is enabled
	 */
	public boolean isEnable() {
		return enable;
	}
	
	/**
	 * @param enable True if the job is enabled
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
    
    /**
     * @return The task name
     */
    public String getTaskName(){
    	return getAbsoluteName();
    }
    
    private boolean enable;
}
