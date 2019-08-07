package qa.com.qatarpost.wih.timers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.joda.time.LocalDate;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author stelios@redhat
 * 
 *  businessExceptionHandle : Handle errors in a business manner with boundary Error Event against WorkItemHandlerRuntimeException
 *  dateGiven          : DATE in ISO-8601 Date format eg. yyyy-MM-dd'T'HH:mm:ss.SSS or yyyy-MM-dd'T'HH:mm:ss.SSSXXX of when duration should end
 *  dateGivenFormat    : The format of the dateGiven date we only accept yyyy-MM-dd'T'HH:mm:ss.SSS
 */
public class IsDateTodayWIH extends AbstractLogOrThrowWorkItemHandler {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IsDateTodayWIH.class.getSimpleName());

	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String businessExceptionHandle = (String) workItem.getParameter("businessExceptionHandle");
		String dateGiven = (String) workItem.getParameter("dateGiven");
		//String dateGivenFormat = (String) workItem.getParameter("dateGivenFormat");
		String dateGivenFormat =  "yyyy-MM-dd'T'HH:mm:ss.SSS";

		Map<String, Object> results = new HashMap<String, Object>();

		try {
		    Boolean isToday = false;
			
		    logger.info("Date given ="+dateGiven);
		    
			if (dateGiven != null) {
				DateFormat df2 = new SimpleDateFormat(dateGivenFormat);
				Date givenDate = df2.parse(dateGiven);
				Date today = new Date();
				
				logger.info("Checking today's date ["+today+"] versus given ["+givenDate+"]");

				logger.info("Today year ["+today.getYear()+"] month ["+today.getMonth()+"] day ["+today.getDay()+"] ");
				logger.info("Given year ["+givenDate.getYear()+"] month ["+givenDate.getMonth()+"] day ["+givenDate.getDay()+"] ");

				
				if ((givenDate.getYear() == today.getYear()) && 
				     (givenDate.getMonth() == today.getMonth()) && 
				     (givenDate.getDay() == today.getDay())) {
					isToday = true;
					logger.info("Defining isToday ="+isToday);
				}
			}
			
		    logger.info("results = null= ["+(results == null )+"]");
		    logger.info("isToday = ["+isToday+"]");

		    
			results.put("isToday", isToday);
		    logger.info("assigning isToday to workitem results ["+(manager == null)+"]");

			manager.completeWorkItem(workItem.getId(), results);
			
		} catch (Throwable cause) {
			handleException(Boolean.parseBoolean(businessExceptionHandle.toLowerCase()), cause, workItem.getParameters());
		}
	}
	
	protected void handleException(Boolean isHandledExceptionAtBoundaryBusinessLevel, Throwable cause, Map<String, Object> inputData) {

		if (isHandledExceptionAtBoundaryBusinessLevel) {
			super.logThrownException = false;
			super.handleException(cause);

		} else {
			// delegate to AbstractLogOrThrowWorkItemHandler.handleException()
			super.logThrownException = true;
			super.handleException(cause, inputData);

		}
	}

}
