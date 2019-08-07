package qa.com.qatarpost.wih.timers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author stelios@redhat.com
 * 
 * 
 * without timezone setttings this is how to provide dates and format
 * fromDateValue : For DateTime, value is : 2013-04-13T22:56:27.000
 * fromDateFormat:                         yyyy-MM-dd'T'HH:mm:ss.SSS
 * 
 * with timezone setttings this is how to provide dates and format
 * untilDateValue: For DateTime, value is : 2013-04-13T22:56:27.000+03:00
 * untilDateFormat:                         yyyy-MM-dd'T'HH:mm:ss.SSSXXX
 * 
 * https://access.redhat.com/documentation/en-us/red_hat_jboss_brms/6.2/html/development_guide/timers
 * Date - 2013-12-24T20:00:00.000+02:00 - fires exactly at Christmas Eve at 8PM
    Duration - PT1S - fires once after 1 second
    Repeatable intervals - R/PT1S - fires every second, no limit. Alternatively R5/PT1S fires 5 times every second
 * 
 * Timer Intermediate Event
 * https://developer.jboss.org/thread/232477
 * Timer Cycle - the actual time expression that shall be used - can be string (interval based 2m or 2m###5m - where first is the initial delay and the delay for repeated fires), string cron expression or can be process variables and in v6 it can be ISO-8601 date format
 * Timer Cycle Language - can be default interval or cron
 * Timer Duration - one time expiration timer - delay after which timer should fire, string interval, process variable and in v6 ISO-8601 date format for duration
 *
 *
 * ************************************************
 * WORK ITEM HANDLER PARAMETERS
 * ************************************************
 *  untilDateValue          : DATE in ISO-8601 Date format eg. yyyy-MM-dd'T'HH:mm:ss.SSS or yyyy-MM-dd'T'HH:mm:ss.SSSXXX of when duration should end
 *  untilDateFormat         : The format of the untilDateValue date 
 *  millisecondsTimeEnd     : long value of milliseconds when time duration will end
 *  fromDateValue           : DATE in ISO-8601 Date format eg. yyyy-MM-dd'T'HH:mm:ss.SSS or yyyy-MM-dd'T'HH:mm:ss.SSSXXX duration should be calculated from if given in this format
 *  fromDateFormat          : The format of the fromDateValue dat
 *  millisecondsTimeStart   : long value of milliseconds duration should be calculated from if given in this format
 *  fromNow                 : Boolean signifying if duration should be calculate from now
 *  businessExceptionHandle : Handle errors in a business manner with boundary Error Event against WorkItemHandlerRuntimeException
 *  dateTomorrow6AM         : define the time for the duration as tomorrow 6AM
 *
 * ************************************************
 * POSSIBLE COMBINATIONS OF PARAMETERS
 * ************************************************
 * [IMPLEMENTED] dateTomorrow6AM
 * [TODO] untilDateValue, untilDateFormat, fromNow
 * [TODO] untilDateValue, untilDateFormat, millisecondsTimeStart
 * [TODO] untilDateValue, untilDateFormat, fromDateValue, fromDateFormat
 * [TODO] millisecondsTimeStart, fromNow
 * [TODO] millisecondsTimeStart, fromDateValue, fromDateFormat
 * [TODO] millisecondsTimeStart, millisecondsTimeStart
 * [TODO]
 * [TODO]
 * [TODO]
 * [TODO]

 */
public class CalculateTimerWIH extends AbstractLogOrThrowWorkItemHandler {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CalculateTimerWIH.class.getSimpleName());
	
	enum CalculationType{
		FROM_DATE_STARTING_DATE_GIVEN,
		FROM_DATE_STARTING_MILLIS_GIVEN,
		FROM_DATE_STARTING_NOW,
		FROM_MILLIS_STARTING_DATE_GIVEN,
		FROM_MILLIS_STARTING_MILLIS_GIVEN,
		FROM_MILLIS_STARTING_NOW,
		DATE_TOMORROW_6AM,
		CACLCULATION_ERROR;
	}
	
	private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;
	private static final String ISO_FORMAT_NO_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	private static final String ISO_FORMAT_WITH_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

	
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String untilDateValue = (String) workItem.getParameter("untilDateValue");
		String untilDateFormat = (String) workItem.getParameter("untilDateFormat");
		Long millisecondsTimeEnd = (Long) workItem.getParameter("millisecondsTimeEnd");
		String fromDateValue = (String) workItem.getParameter("fromDateValue");
		String fromDateFormat = (String) workItem.getParameter("fromDateFormat");
		Long millisecondsTimeStart = (Long) workItem.getParameter("millisecondsTimeStart");
		
		String fN = (String) workItem.getParameter("fromNow");
		Boolean fromNow = Boolean.parseBoolean(fN);
        
		String beh = (String) workItem.getParameter("businessExceptionHandle");
		Boolean businessExceptionHandle = Boolean.parseBoolean(beh);

		String dT6am = (String) workItem.getParameter("dateTomorrow6AM");
		Boolean dateTomorrow6AM = Boolean.parseBoolean(dT6am);



		CalculationType calcDecision = typeOfCalculation(untilDateValue, untilDateFormat, millisecondsTimeEnd,
				fromNow, fromDateValue, fromDateFormat, millisecondsTimeStart, dateTomorrow6AM);

		Map<String, Object> results = new HashMap<String, Object>();
        String timer = null;
        
		try {
			Date todaynow = null;
			
			switch (calcDecision) {
			case DATE_TOMORROW_6AM:
				logger.info(CalculationType.DATE_TOMORROW_6AM.name());

				TimeZone tz = TimeZone.getTimeZone("AST");
				DateFormat df = new SimpleDateFormat(ISO_FORMAT_WITH_TZ);
//				//df.setTimeZone(tz);
//				String nowAsISO = df.format(new Date());
				
				
				todaynow = new Date();
				
				logger.info("==============================================================");
				logger.info("Date initially ["+df.format(todaynow)+"]");
				logger.info("==============================================================");

				LocalDateTime lt;
				
				// set correctly hour to 6AM 
				org.joda.time.LocalDateTime.Property todayHourProp = LocalDateTime.fromDateFields(todaynow).hourOfDay();
				if (todayHourProp.get() > 6) {
					lt = LocalDateTime.fromDateFields(todaynow).minusHours(todayHourProp.get() - 6);
					logger.info("convert hours to 6am ["+lt+"]");
				} else {
					lt = LocalDateTime.fromDateFields(todaynow).plusHours(6 - todayHourProp.get());
					logger.info("convert hours to 6am ["+lt+"]");
				}
				
				Date newdate = lt.toLocalDate().toDateMidnight().toDate();
				logger.info("date after convert hours to 6am ["+newdate+"]");

				
				// set correctly hour to 0 minutes 
				org.joda.time.LocalDateTime.Property todayMinProp = LocalDateTime.fromDateFields(todaynow).minuteOfHour();
				lt = LocalDateTime.fromDateFields(newdate).minusMinutes(todayMinProp.get());
				
				newdate = lt.toLocalDate().toDateMidnight().toDate();
				logger.info("date after convert minutes to 6.00am ["+newdate+"]");
				
				// set correctly hour to 0 seconds 
				org.joda.time.LocalDateTime.Property todaySecsProp = LocalDateTime.fromDateFields(todaynow).secondOfMinute();
				lt = LocalDateTime.fromDateFields(newdate).minusSeconds(todaySecsProp.get());
				
				newdate = lt.toLocalDate().toDateMidnight().toDate();
				logger.info("date after convert seconds to 6.00:00am ["+newdate+"]");
				
				//df.setTimeZone(tz);
				timer = df.format(todaynow);
				//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				DateFormat dateFormat = new SimpleDateFormat(ISO_FORMAT_NO_TZ);
				dateFormat.setTimeZone(TimeZone.getTimeZone("AST"));
				timer = dateFormat.format(newdate);
				
				logger.info("==============================================================");
				logger.info("Date Finally for 6AM conversion ["+df.format(todaynow)+"]");
				logger.info("==============================================================");
				
				break;
			case FROM_DATE_STARTING_DATE_GIVEN: 
				logger.info(CalculationType.FROM_DATE_STARTING_DATE_GIVEN.name());
						
				timer = calculateMillisDuration(untilDateValue, untilDateFormat, fromDateValue, fromDateFormat);
				
				break;
			case FROM_DATE_STARTING_MILLIS_GIVEN: 
				logger.info(CalculationType.FROM_DATE_STARTING_MILLIS_GIVEN.name());

				timer = calculateMillisDuration(untilDateValue, untilDateFormat, millisecondsTimeStart);
				
				break;
			case FROM_DATE_STARTING_NOW:
				logger.info(CalculationType.FROM_DATE_STARTING_NOW.name());
				
				timer = calculateMillisDuration(todaynow.getTime(), untilDateFormat, millisecondsTimeStart);
				
				break;
			case FROM_MILLIS_STARTING_MILLIS_GIVEN: 
				logger.info(CalculationType.FROM_MILLIS_STARTING_MILLIS_GIVEN.name());

				timer = calculateMillisDuration(millisecondsTimeStart, millisecondsTimeEnd);
				
				break;
			case FROM_MILLIS_STARTING_NOW: 
				logger.info(CalculationType.FROM_MILLIS_STARTING_NOW.name());

				timer = calculateMillisDuration(todaynow.getTime(), millisecondsTimeEnd);
				
				break;
			default:
				logger.error(CalculationType.CACLCULATION_ERROR.name()+" : No possible calculation as not all information has been provided as exepected ");
				break;
			}
			
			results.put("timer", timer);
			manager.completeWorkItem(workItem.getId(), results);

		} catch (Throwable t) {
			handleException(businessExceptionHandle, t, workItem.getParameters());
			//throw new WorkItemHandlerRuntimeException(t);
		}

	}

	/*
	 * Implements FROM_MILLIS_STARTING_MILLIS_GIVEN, FROM_MILLIS_STARTING_NOW
	 */
	private String calculateMillisDuration(long time, Long millisecondsTimeEnd) {
		return String.valueOf(millisecondsTimeEnd - time);
	}

	/*
	 * Implements FROM_DATE_STARTING_NOW
	 */
	private String calculateMillisDuration(long time, String untilDateFormat, Long millisecondsTimeStart) {
		// TODO Auto-generated method stub
		return null;
	}

	/* Implements FROM_DATE_STARTING_DATE_GIVEN
	 * 
	 */
	private String calculateMillisDuration(String untilDateValue, String untilDateFormat, Long millisecondsTimeStart) {
		// TODO Auto-generated method stub
		return null;
	}

	/* Implements FROM_DATE_STARTING_MILLIS_GIVEN
	 * 
	 */
	private String calculateMillisDuration(String untilDateValue, String untilDateFormat, String fromDateValue,
			String fromDateFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	private CalculationType typeOfCalculation(String untilDateValue, String untilDateFormat, Long millisecondsTimeEnd,
			Boolean fromNow, String fromDateValue, String fromDateFormat, Long millisecondsTimeStart, Boolean dateTomorrow6AM ) {
		// If fromNow we expect the date or milliseconds provided to be calculated and the
		// remaining time when subtracting the Time.NOW (in millisecods) to be the duration returned
		if (dateTomorrow6AM) {
			return CalculationType.DATE_TOMORROW_6AM;
		} else if (fromNow) {
			if (untilDateValue != null && untilDateFormat != null && fromDateValue == null ) {
				return CalculationType.FROM_DATE_STARTING_NOW;
			} else if (millisecondsTimeEnd != null && millisecondsTimeStart == null) {
				return CalculationType.FROM_MILLIS_STARTING_NOW;
			}
			return CalculationType.CACLCULATION_ERROR;
		} 
		else { 
			if (untilDateValue != null && untilDateFormat != null && fromDateValue != null && fromDateFormat != null) {
				return CalculationType.FROM_DATE_STARTING_DATE_GIVEN;
			} else if (untilDateValue != null && untilDateFormat != null && millisecondsTimeStart != null) {
				return CalculationType.FROM_DATE_STARTING_MILLIS_GIVEN;
			} else if (millisecondsTimeEnd != null && fromDateValue != null && fromDateFormat != null) {
				return CalculationType.FROM_MILLIS_STARTING_DATE_GIVEN;				
			} else if (millisecondsTimeEnd != null && millisecondsTimeStart != null) {
				return CalculationType.FROM_MILLIS_STARTING_MILLIS_GIVEN;
			}
			return CalculationType.CACLCULATION_ERROR;
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
