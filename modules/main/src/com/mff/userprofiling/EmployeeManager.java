package com.mff.userprofiling;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import com.mff.commerce.profile.MFFPropertyManager;

import atg.adapter.gsa.GSARepository;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.ParameterSupportQueryBuilder;
import atg.repository.ParameterSupportView;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

public class EmployeeManager extends GenericService {
	// property names in employee item descriptor in EmployeeRepository
	protected static final String SOM_CARD_PTY_NAME = "somCard";
	protected static final String PHONE_NUM_PTY_NAME = "phoneNumber";
	
	protected static final String EMPLOYEE_ITEM_DESC = "employee";
	
	GSARepository employeeRepository;
	MFFPropertyManager commercePropertyManager;
	TransactionManager transactionManager;
	
	

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		transactionManager = pTransactionManager;
	}

	public GSARepository getEmployeeRepository() {
		return employeeRepository;
	}

	public void setEmployeeRepository(GSARepository pEmployeeRepository) {
		employeeRepository = pEmployeeRepository;
	}
	
	public MFFPropertyManager getCommercePropertyManager() {
		return commercePropertyManager;
	}

	public void setCommercePropertyManager(MFFPropertyManager pCommercePropertyManager) {
		commercePropertyManager = pCommercePropertyManager;
	}

	/**
	 * Searches EmployeeRepository for a record matching given som & phone number
	 * If one is found, then the 
	 * @param pSomCard
	 * @param pPhoneNumber
	 * @return employeeId
	 */

/*	public String isValidEmployee(String pSomCard, String pPhoneNumber) {
		boolean isValid = false;
		
		if(pSomCard == null) {
			vlogError("No SOM Card entered");
			return isValid;
		}

		if(pPhoneNumber == null) {
			vlogError("No phone entered");
			return isValid;
		}
		
		vlogDebug("SOM Card and phone number entered. Finding matching employee record");
		
		try {
			RepositoryItem employee = findEmployee(pSomCard, pPhoneNumber);
			
			if(employee != null) {
				vlogDebug("matching emp record found");
				isValid = true;
			} else {
				logError("No matching record found");
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			isValid = false;
		}
		vlogDebug("Returning {0} ", isValid);
		return isValid;
	}
*/	
/*	public boolean isValidEmployee(RepositoryItem pProfile) {
		boolean isValid = false;
		
		if(pProfile == null) {
			vlogError("Profile is null");
			return isValid;
		} else {
			vlogDebug("Validating profile id {0}", pProfile.getRepositoryId());
		}
		
		String profileSomCard = (String)pProfile.getPropertyValue(getCommercePropertyManager().getSomCardPropertyName());
		String profilePhone = (String)pProfile.getPropertyValue(getCommercePropertyManager().getAddressPhoneNumberPropertyName());
		
		if(profileSomCard == null) {
			vlogError("No SOM Card on Profile");
			return isValid;
		}

		if(profilePhone == null) {
			vlogError("No phone on Profile");
			return isValid;
		}
		
		vlogDebug("Profile has SOM Card and phone number. Finding matching employee record");
		
		try {
			RepositoryItem employee = findEmployee(profileSomCard, profilePhone);
			
			if(employee != null) {
				vlogDebug("matching emp record found");
				isValid = true;
			} else {
				logError("No matching record found");
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			isValid = false;
		}
		vlogDebug("Returning {0} ", isValid);
		return isValid;
	}
*/	
	public RepositoryItem findEmployee(String pSomCard, String pPhoneNumber)  {
		if(pSomCard == null) {
			vlogError("No SOM Card entered");
			return null;
		}

		if(pPhoneNumber == null) {
			vlogError("No phone entered");
			return null;
		}
		try {
			RepositoryView employeeView = getEmployeeRepository().getItemDescriptor(EMPLOYEE_ITEM_DESC).getRepositoryView();
			QueryBuilder employeeQb = employeeView.getQueryBuilder();
			Query employeeQuery = generateValidateEmpQuery(pSomCard, pPhoneNumber, employeeQb);
			
			// dont expect to find multiple employees for the phone-som. But we'll take the first match
			RepositoryItem[] employees = employeeView.executeQuery(employeeQuery, 0, 1);
			
			if ((employees != null) && (employees.length > 0)) {
				vlogDebug("Found {0} employees", employees.length);
				return employees[0];
			}			
		} catch (RepositoryException e) {
			vlogError(e, "Unable to find emp record");
		}

		vlogDebug("Nothing found.. returning no match");
		
		return null;
	}
	
	/**
	 * Method to check if the profile is that of an employeed
	 * and if it needs to be validated in the current session
	 * @param pProfile
	 * @return
	 * @throws ServletException 
	 */
	public boolean isValidateEmployee(MutableRepositoryItem pProfile) throws ServletException {
		
		boolean isValidate = false;
		
		boolean isValidated = false;
		boolean isEmployee = false;
		
		String somCard = (String)pProfile.getPropertyValue(SOM_CARD_PTY_NAME);
		
		// profile doesnt have a somCard... exit.. not an employee
		if(StringUtils.isBlank(somCard)){
			setEmployee(pProfile, false, false);
			return false;
		} else {
			// som card present.. we know this is an employee
			isEmployee = true;
			
			// lets get the phone number
			String phoneNumber = (String)pProfile.getPropertyValue(PHONE_NUM_PTY_NAME);
			if(!StringUtils.isBlank(phoneNumber)){
				phoneNumber = phoneNumber.replaceAll("-", "");
			}
			
			// This shouldn't happen..
			if(StringUtils.isBlank(phoneNumber)){
				setEmployee(pProfile, isEmployee, false);
				return false;
			} else {
				
				// we have both a SOM Card and a phone... lets validate
				isValidate = true;
				RepositoryItem employee = findEmployee(somCard,phoneNumber);
				if(employee != null) {
					setEmployee(pProfile, true, true);
				} else {
					setEmployee(pProfile, isEmployee, false);
				}
/*				if(!isValidEmployee(somCard,phoneNumber)) {
					//Set our employee transient props
					setEmployee(pProfile, isEmployee, false);
					
				} else {
					setEmployee(pProfile, true, true);
				}*/
			}
		}
		return isValidate;
	}
	
	public void setEmployee(MutableRepositoryItem pProfile, boolean bIsEmployee, boolean bIsValidated) throws ServletException {
		
		boolean employee = false;
		boolean validated = false;
		
		if(bIsEmployee) {
			employee = bIsEmployee;
			validated = bIsValidated;
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();

		try {
			if (tm != null) {
				td.begin(tm, TransactionDemarcation.REQUIRED);
			}
			
			MutableRepository repository = (MutableRepository) pProfile.getRepository();
			try {
				MutableRepositoryItem mutProfileItem = repository.getItemForUpdate(pProfile.getRepositoryId(), "user");
				mutProfileItem.setPropertyValue("validated", validated);
				mutProfileItem.setPropertyValue("employee", employee);
				if(!validated) {
					mutProfileItem.setPropertyValue("employeeId", "");
					mutProfileItem.setPropertyValue("somCard", "");					
				}

				repository.updateItem(mutProfileItem);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}

		} catch (TransactionDemarcationException e) {
			throw new ServletException(e);
		} finally {
			try {
				if (tm != null) {
					td.end();
				}
			} catch (TransactionDemarcationException e) {
			}
		}		
	}
	
	public Query generateValidateEmpQuery(String pSomCard, String pPhoneNumber, QueryBuilder pEmployeeQb) throws RepositoryException {
	
		QueryExpression somCardProperty = pEmployeeQb.createPropertyQueryExpression(SOM_CARD_PTY_NAME);
		QueryExpression somCardValue = pEmployeeQb.createConstantQueryExpression(pSomCard);
		Query somCardQuery = pEmployeeQb.createComparisonQuery
		            (somCardProperty, somCardValue, QueryBuilder.EQUALS);
		
		QueryExpression phoneNumProperty = pEmployeeQb.createPropertyQueryExpression(PHONE_NUM_PTY_NAME);
		QueryExpression phoneNumberValue = pEmployeeQb.createConstantQueryExpression(pPhoneNumber);
		Query phoneNumberQuery = pEmployeeQb.createComparisonQuery
		            (phoneNumProperty, phoneNumberValue, QueryBuilder.EQUALS);
		
		Query[] compoundQuery = new Query[]{somCardQuery, phoneNumberQuery};
		
		Query ValidateEmpQuery = pEmployeeQb.createAndQuery(compoundQuery);
		return ValidateEmpQuery;
	}
}