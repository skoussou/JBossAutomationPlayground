/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.process.core.timer;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.Arrays;
import static java.util.stream.Collectors.toMap;

import org.jbpm.process.core.timer.BusinessCalendar;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.util.PatternConstants;
import org.kie.api.time.SessionClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of BusinessCalendar interface that is configured with properties.
 * Following are supported properties:
 * <ul>
 *  <li>business.days.per.week - specifies number of working days per week (default 5)</li>
 *  <li>business.hours.per.day - specifies number of working hours per day (default 8)</li>
 *  <li>business.start.hour - specifies starting hour of work day (default 9AM)</li>
 *  <li>business.end.hour - specifies ending hour of work day (default 5PM)</li>
 *  <li>business.holidays - specifies holidays (see format section for details on how to configure it)</li>
 *  <li>business.holiday.date.format - specifies holiday date format used (default yyyy-MM-dd)</li>
 *  <li>business.weekend.days - specifies days of the weekend (default Saturday and Sunday)</li>
 *  <li>business.cal.timezone - specifies time zone to be used (if not given uses default of the system it runs on)</li>
 *  </ul>
 *
 *  The following are necessary for <b>Fraction Hours</b> to work
 *  <ul>
 *  <li>hour.format - specifies business hours start (business.start.hour) and end  (business.end.hour) fraction format (only HH:mm currently supported) </li> 
 *  <li>Both business.start.hour and business.end.hour must be provided with a fraction when hour.format is present even if fraction is 8:00 </li> 
 * </ul>
 * 
 * <b>Format</b><br/>
 * 
 * Holidays can be given in two formats:
 * <ul>
 *  <li>as date range separated with colon - for instance 2012-05-01:2012-05-15</li>
 *  <li>single day holiday - for instance 2012-05-01</li>
 * </ul>
 * each holiday period should be separated from next one with comma: 2012-05-01:2012-05-15,2012-12-24:2012-12-27
 * <br/> 
 * Holiday date format must be given in pattern that is supported by <code>java.text.SimpleDateFormat</code>.<br/>
 * 
 * Weekend days should be given as integer that corresponds to <code>java.util.Calendar</code> constants.
 * <br/>
 * 
 */
public class HourFractionsBusinessCalendarImpl implements BusinessCalendar {
	
	private static final Logger logger = LoggerFactory.getLogger(HourFractionsBusinessCalendarImpl.class);

    private Properties businessCalendarConfiguration;
    
    private static final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    
    private int daysPerWeek;
    private int hoursInDay;
    private int startHour; 
    private int endHour; 
    private  String timezone;
    
    private List<TimePeriod> holidays;
    private List<Integer> weekendDays= new ArrayList<Integer>();
    private SessionClock clock;
    
    private static final int     SIM_WEEK = 3;
    private static final int     SIM_DAY = 5;
    private static final int     SIM_HOU = 7;
    private static final int     SIM_MIN = 9;
    private static final int     SIM_SEC = 11;

    /* Handling business start/end hour with minute fractions */
    /* Only supported format right now is hour.format=HH:mm */
    public static final String BUSINESS_HOUR_FORMAT = "hour.format";
    public static final String NONE = "none";
    public static final String START_HOUR_MINS = "business.start.hour";
    public static final String END_HOUR_MINS = "business.end.hour";

    private boolean businessHourContainsMins;
    private int startHourMins;
    private int endHourMins;


    public static final String DAYS_PER_WEEK = "business.days.per.week";
    public static final String HOURS_PER_DAY = "business.hours.per.day";
    public static final String START_HOUR = "business.start.hour";
    public static final String END_HOUR = "business.end.hour";
    // holidays are given as date range and can have more than one value separated with comma
    public static final String HOLIDAYS = "business.holidays";
    public static final String HOLIDAY_DATE_FORMAT = "business.holiday.date.format";
    
    public static final String WEEKEND_DAYS = "business.weekend.days";
    public static final String TIMEZONE = "business.cal.timezone";

    private static final String DEFAULT_PROPERTIES_NAME = "/jbpm.business.calendar.properties";
    
    
    
    
    public HourFractionsBusinessCalendarImpl() {
        String propertiesLocation = System.getProperty("jbpm.business.calendar.properties");
        
        if (propertiesLocation == null) {
            propertiesLocation = DEFAULT_PROPERTIES_NAME;
        }
        businessCalendarConfiguration = new Properties();
        
        InputStream in = this.getClass().getResourceAsStream(propertiesLocation);
        if (in != null) {
            
            try {
                businessCalendarConfiguration.load(in);
            } catch (IOException e) {
               logger.error("Error while loading properties for business calendar", e);

            }
        }
        init();
        
    }
    
    public HourFractionsBusinessCalendarImpl(Properties configuration) {
        this.businessCalendarConfiguration = configuration;
        init();
    }
    
    public HourFractionsBusinessCalendarImpl(Properties configuration, SessionClock clock) {
        this.businessCalendarConfiguration = configuration;
        this.clock = clock;
        init();
    }
    
    protected void init() {
        if (this.businessCalendarConfiguration == null) {
            throw new IllegalArgumentException("BusinessCalendar configuration was not provided.");
        }

        /* RFE-0001 to handle business start/end hour minutes */
        businessHourContainsMins = hasBusinessHourMinutes(getPropertyAsString(BUSINESS_HOUR_FORMAT, NONE));

        if (businessHourContainsMins) {
            String tmpStartHour = getPropertyAsString(START_HOUR, "9");
            String tmpEndHour = getPropertyAsString(END_HOUR, "17");

            Map<Integer, Integer> startTimeMap = hoursminsToMapInt(tmpStartHour);
            Map<Integer, Integer> endTimeMap = hoursminsToMapInt(tmpEndHour);

            startHour = (Integer) startTimeMap.keySet().toArray()[0];
            startHourMins = (Integer) startTimeMap.values().toArray()[0];

            endHour = (Integer) endTimeMap.keySet().toArray()[0];
            endHourMins = (Integer) endTimeMap.values().toArray()[0];

        } else {
            startHour = getPropertyAsInt(START_HOUR, "9");
            endHour = getPropertyAsInt(END_HOUR, "17");
        }

        daysPerWeek = getPropertyAsInt(DAYS_PER_WEEK, "5");
        hoursInDay = getPropertyAsInt(HOURS_PER_DAY, "8");
        holidays = parseHolidays();
        parseWeekendDays();
        this.timezone = businessCalendarConfiguration.getProperty(TIMEZONE);
    }
    
    protected String adoptISOFormat(String timeExpression) {

        try {
            Duration p = null;
            if (DateTimeUtils.isPeriod(timeExpression)) {
                p = Duration.parse(timeExpression);
            } else if (DateTimeUtils.isNumeric(timeExpression)) {
                p = Duration.of(Long.valueOf(timeExpression), ChronoUnit.MILLIS);
            } else {
                OffsetDateTime dateTime = OffsetDateTime.parse(timeExpression, DateTimeFormatter.ISO_DATE_TIME);
                p = Duration.between(OffsetDateTime.now(), dateTime);
            }

            long days = p.toDays();
            long hours = p.toHours() % 24;
            long minutes = p.toMinutes() % 60;
            long seconds = p.getSeconds() % 60;
            long milis = p.toMillis() % 1000;

            StringBuffer time = new StringBuffer();
            if (days > 0) {
                time.append(days + "d");
            }
            if (hours > 0) {
                time.append(hours + "h");
            }
            if (minutes > 0) {
                time.append(minutes + "m");
            }
            if (seconds > 0) {
                time.append(seconds + "s");
            }
            if (milis > 0) {
                time.append(milis + "ms");
            }

            return time.toString();
        } catch (Exception e) {
            return timeExpression;
        }
    }
    
    public long calculateBusinessTimeAsDuration(String timeExpression) {
    	timeExpression = adoptISOFormat(timeExpression);

        /* RFE-0001 to handle business start/end hour minutfes */
        //        Date calculatedDate = calculateBusinessTimeAsDate(timeExpression);
        Date calculatedDate;

        // Option to use default implementation vs Implementation with business day minutes
        if (businessHourContainsMins) {  calculatedDate = calculateBusinessTimeAsDateWithMins(timeExpression, businessHourContainsMins); }
        else calculatedDate = calculateBusinessTimeAsDate(timeExpression);

        return (calculatedDate.getTime() - getCurrentTime());
    }

    public Date calculateBusinessTimeAsDate(String timeExpression) {
    	timeExpression = adoptISOFormat(timeExpression);

        String trimmed = timeExpression.trim();
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int min = 0;
        int sec = 0;
        
        if( trimmed.length() > 0 ) {
            Matcher mat = PatternConstants.SIMPLE_TIME_DATE_MATCHER.matcher(trimmed );
            if ( mat.matches() ) {
                weeks = (mat.group( SIM_WEEK ) != null) ? Integer.parseInt( mat.group( SIM_WEEK ) ) : 0;
                days = (mat.group( SIM_DAY ) != null) ? Integer.parseInt( mat.group( SIM_DAY ) ) : 0;
                hours = (mat.group( SIM_HOU ) != null) ? Integer.parseInt( mat.group( SIM_HOU ) ) : 0;
                min = (mat.group( SIM_MIN ) != null) ? Integer.parseInt( mat.group( SIM_MIN ) ) : 0;
                sec = (mat.group( SIM_SEC ) != null) ? Integer.parseInt( mat.group( SIM_SEC ) ) : 0;
            }
        }
        int time = 0;
        
        Calendar c = new GregorianCalendar();
        if (timezone != null) {
            c.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        if (this.clock != null) {
            c.setTimeInMillis(this.clock.getCurrentTime());
        }
        
        
        // calculate number of weeks
        int numberOfWeeks = days/daysPerWeek + weeks;
        if (numberOfWeeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
        }
        handleWeekend(c, hours > 0 || min > 0);
        hours += (days - (numberOfWeeks * daysPerWeek)) * hoursInDay;
        
        // calculate number of days
        int numberOfDays = hours/hoursInDay;
        if (numberOfDays > 0) {
            for (int i = 0; i < numberOfDays; i++) {
                c.add(Calendar.DAY_OF_YEAR, 1);
                handleWeekend(c, false);
                handleHoliday(c, hours > 0 || min > 0);
            }
        }

        int currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.add(Calendar.HOUR_OF_DAY, startHour-currentCalHour);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
        } else if (currentCalHour < startHour) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        }

        // calculate remaining hours
        time = hours - (numberOfDays * hoursInDay);
        c.add(Calendar.HOUR, time);
        handleWeekend(c, true);
        handleHoliday(c, hours > 0 || min > 0);
        
        currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            c.set(Calendar.HOUR_OF_DAY, startHour);
            c.add(Calendar.HOUR_OF_DAY, currentCalHour - endHour);
        } else if (currentCalHour < startHour) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        }
        
        // calculate minutes
        int numberOfHours = min/60;
        if (numberOfHours > 0) {
            c.add(Calendar.HOUR, numberOfHours);
            min = min-(numberOfHours * 60);
        }
        c.add(Calendar.MINUTE, min);
        
        // calculate seconds
        int numberOfMinutes = sec/60;
        if (numberOfMinutes > 0) {
            c.add(Calendar.MINUTE, numberOfMinutes);
            sec = sec-(numberOfMinutes * 60);
        }
        c.add(Calendar.SECOND, sec);
        
        currentCalHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            c.set(Calendar.HOUR_OF_DAY, startHour);
            c.add(Calendar.HOUR_OF_DAY, currentCalHour - endHour);
        } else if (currentCalHour < startHour) {
            c.add(Calendar.HOUR_OF_DAY, startHour);
        }
        // take under consideration weekend
        handleWeekend(c, false);
        // take under consideration holidays
        handleHoliday(c, false);
 
        return c.getTime();
    }

    /* RFE-0001 to handle business start/end hour minutes */
    public Date calculateBusinessTimeAsDateWithMins(String timeExpression, Boolean businessHourContainsMins ) {
        timeExpression = adoptISOFormat(timeExpression);

        String trimmed = timeExpression.trim();
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int min = 0;
        int sec = 0;

        if( trimmed.length() > 0 ) {
            Matcher mat = PatternConstants.SIMPLE_TIME_DATE_MATCHER.matcher(trimmed );
            if ( mat.matches() ) {
                weeks = (mat.group( SIM_WEEK ) != null) ? Integer.parseInt( mat.group( SIM_WEEK ) ) : 0;
                days = (mat.group( SIM_DAY ) != null) ? Integer.parseInt( mat.group( SIM_DAY ) ) : 0;
                hours = (mat.group( SIM_HOU ) != null) ? Integer.parseInt( mat.group( SIM_HOU ) ) : 0;
                min = (mat.group( SIM_MIN ) != null) ? Integer.parseInt( mat.group( SIM_MIN ) ) : 0;
                sec = (mat.group( SIM_SEC ) != null) ? Integer.parseInt( mat.group( SIM_SEC ) ) : 0;
            }
        }
        int time = 0;
        int timeHours = 0;
        int timeMins = 0;
        boolean isTimerStartDayHoliday = false;


        Calendar c = new GregorianCalendar();
        if (timezone != null) {
            c.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        if (this.clock != null) {
            c.setTimeInMillis(this.clock.getCurrentTime());
        }


        // calculate number of weeks
        int numberOfWeeks = days/daysPerWeek + weeks;
        if (numberOfWeeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
        }
        isTimerStartDayHoliday = isTodayHoliday(c);
        handleWeekend(c, hours > 0 || min > 0);
        // Handle holidays in case today is a holiday
        handleHoliday(c, false);
        hours += (days - (numberOfWeeks * daysPerWeek)) * hoursInDay;


        // calculate number of days
        int numberOfDays = hours/hoursInDay;
        if (numberOfDays > 0) {
            for (int i = 0; i < numberOfDays; i++) {
                c.add(Calendar.DAY_OF_YEAR, 1);
                handleWeekend(c, false);
                handleHoliday(c, false);
            }
        }

        int currentCalHour = c.get(Calendar.HOUR_OF_DAY);

        // calculate remaining hours
        timeHours = hours - (numberOfDays * hoursInDay);
        timeHours += min/60;

        // calculate minutes
        timeMins = min-(min/60);

        // Starting timer before business day start
        if (currentCalHour < startHour || (currentCalHour == startHour && c.get(Calendar.MINUTE) < startHourMins)) {
            c.set(Calendar.HOUR_OF_DAY, startHour+timeHours);
            c.set(Calendar.MINUTE, startHourMins+timeMins);
            // TODO - Logical bug it should be calculatinng remainder secs and modifying mis. But simplifying as it can change day
            c.set(Calendar.SECOND, sec);
        }
        // Starting timer during business day hours
        else if (currentCalHour > startHour &&
                (currentCalHour < endHour || (currentCalHour == endHour && c.get(Calendar.MINUTE) < endHourMins))) {
            // Remainder Time hours [after time = hours - (numberOfDays * hoursInDay); + numberOfHours = min/60] + remainder minutes are added to the beginning of the day and check if they crossed to another day
            // 3-a if yes add day +1 + check holidays/weekends as it may add more days
            // and then time must adjusted to be beginning of day + hours and minutes greater than end of day
            if ((currentCalHour + timeHours) > endHour
                    || ((currentCalHour + timeHours) == endHour && endHourMins < c.get(Calendar.MINUTE)+timeMins)
                    || (((currentCalHour + timeHours) == endHour-1) && endHourMins < (c.get(Calendar.MINUTE)+timeMins)-60)) {
                // 3-a if yes add day +1 + check holidays/weekends as it may add more days
                if (!isTimerStartDayHoliday) c.add(Calendar.DAY_OF_YEAR, 1);
                handleWeekend(c, false);
                handleHoliday(c, false);

                // and then time must adjusted to be beginning of day + hours and minutes beyond end of day calculated and added
                c.set(Calendar.HOUR_OF_DAY, startHour+timeHours);
                int addMins = 0;
                // If today was holiday no minutes from today should be used simply the timeMins
                if (!isTimerStartDayHoliday) {
                    addMins = (endHourMins < c.get(Calendar.MINUTE)+timeMins) ? ((c.get(Calendar.MINUTE)+timeMins)-endHourMins) : (endHourMins-(c.get(Calendar.MINUTE)+timeMins));
                }
                else {
                    addMins = timeMins;
                }
                c.set(Calendar.MINUTE, startHourMins+addMins);
                // TODO - Logical bug it should be calculatinng remainder secs and modifying mis. But simplifying as it can change day
                c.set(Calendar.SECOND, sec);
                // 3-b if no then time is going to be + The remainder hours [after time = hours - (numberOfDays * hoursInDay); + numberOfHours = min/60] + remainder minutes
            } else {
                if (!isTimerStartDayHoliday) {
                    c.add(Calendar.HOUR_OF_DAY, timeHours);
                    c.add(Calendar.MINUTE, timeMins);
                    // TODO - Logical bug it should be calculatinng remainder secs and modifying mis. But simplifying as it can change day
                    c.set(Calendar.SECOND, sec);
                } else {
                    c.set(Calendar.HOUR_OF_DAY, startHour+timeHours);
                    c.set(Calendar.MINUTE, startHourMins+timeMins);
                    // TODO - Logical bug it should be calculatinng remainder secs and modifying mis. But simplifying as it can change day
                    c.set(Calendar.SECOND, 0);
                }
            }
        }
        // Starting Timer after business day end
        else { // currentCalHour > endHour
            // Add + 1 day to go to next day and check weekends and holidays
            if (!isTimerStartDayHoliday) { c.add(Calendar.DAY_OF_YEAR, 1);}
            handleWeekend(c, false);
            handleHoliday(c, false);
            // The remaining the same as Option 1
            c.set(Calendar.HOUR_OF_DAY, startHour+timeHours);
            c.set(Calendar.MINUTE, startHourMins+timeMins);
            // TODO - Logical bug it should be calculatinng remainder secs and modifying mis. But simplifying as it can change day
            c.set(Calendar.SECOND, sec);
        }

        // take under consideration weekend
        handleWeekend(c, false);
        // take under consideration holidays
        handleHoliday(c, false);

        return c.getTime();
    }

    protected void handleHoliday(Calendar c, boolean resetTime) {
        if (!holidays.isEmpty()) {
            Date current = c.getTime();
            boolean holidayMatchFound = false;
            for (TimePeriod holiday : holidays) {
                // check each holiday if it overlaps current date and break after first match
                if (current.after(holiday.getFrom()) && current.before(holiday.getTo())) {
                    
                    Calendar tmp = new GregorianCalendar();
                    tmp.setTime(holiday.getTo());
                    tmp.set(Calendar.HOUR_OF_DAY, startHour);
                    tmp.set(Calendar.MINUTE, startHourMins);
                    tmp.set(Calendar.SECOND, 0);
                    tmp.set(Calendar.MILLISECOND, 0);

                    Calendar tmp2 = new GregorianCalendar();
                    tmp2.setTime(current);
                    tmp2.set(Calendar.HOUR_OF_DAY, startHour);
                    tmp2.set(Calendar.MINUTE, startHourMins);
                    tmp2.set(Calendar.SECOND, 0);
                    tmp2.set(Calendar.MILLISECOND, 0);

                    long difference = tmp.getTimeInMillis() - tmp2.getTimeInMillis();
                    
                    c.add(Calendar.HOUR_OF_DAY, (int) (difference/HOUR_IN_MILLIS));
                    
                    handleWeekend(c, resetTime);
                    // Logic to NOT break after first match but instead set flag that match found
                    holidayMatchFound = true;
                    current = c.getTime();
                    //break;
                } else if (holidayMatchFound) {
                    // if holidayMatchFound=true in previous run BUT in this run not found then break
                    break;
                }
            }
        }
    }

    protected int getPropertyAsInt(String propertyName, String defaultValue) {
        String value = businessCalendarConfiguration.getProperty(propertyName, defaultValue);
        
        return Integer.parseInt(value);
    }

    // Can return null
    protected String getPropertyAsString(String propertyName, String defaultValue) {
        String value = businessCalendarConfiguration.getProperty(propertyName, defaultValue);

        return value;
    }
    
    protected List<TimePeriod> parseHolidays() {
        String holidaysString = businessCalendarConfiguration.getProperty(HOLIDAYS);
        List<TimePeriod> holidays = new ArrayList<TimePeriod>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (holidaysString != null) {
            String[] hPeriods = holidaysString.split(",");
            SimpleDateFormat sdf = new SimpleDateFormat(businessCalendarConfiguration.getProperty(HOLIDAY_DATE_FORMAT, "yyyy-MM-dd"));
            for (String hPeriod : hPeriods) {
                boolean addNextYearHolidays = false;
                
                String[] fromTo = hPeriod.split(":");
                if (fromTo[0].startsWith("*")) {
                    addNextYearHolidays = true;
                    
                    fromTo[0] = fromTo[0].replaceFirst("\\*", currentYear+"");
                }
                try {
                    if (fromTo.length == 2) {
                        Calendar tmpFrom = new GregorianCalendar();
                        if (timezone != null) {
                            tmpFrom.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        tmpFrom.setTime(sdf.parse(fromTo[0]));
  
                        if (fromTo[1].startsWith("*")) {
                            
                            fromTo[1] = fromTo[1].replaceFirst("\\*", currentYear+"");
                        }
                        
                        Calendar tmpTo = new GregorianCalendar();
                        if (timezone != null) {
                            tmpTo.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        tmpTo.setTime(sdf.parse(fromTo[1]));
                        Date from = tmpFrom.getTime();
                        
                        
                        tmpTo.add(Calendar.DAY_OF_YEAR, 1);
                        
                        if ((tmpFrom.get(Calendar.MONTH) > tmpTo.get(Calendar.MONTH)) && (tmpFrom.get(Calendar.YEAR) == tmpTo.get(Calendar.YEAR))) {
                            tmpTo.add(Calendar.YEAR, 1);
                        }
                        
                        Date to = tmpTo.getTime();
                        holidays.add(new TimePeriod(from, to));
                        
                        holidays.add(new TimePeriod(from, to));
                        if (addNextYearHolidays) {
                            tmpFrom = new GregorianCalendar();
                            if (timezone != null) {
                                tmpFrom.setTimeZone(TimeZone.getTimeZone(timezone));
                            }
                            tmpFrom.setTime(sdf.parse(fromTo[0]));
                            tmpFrom.add(Calendar.YEAR, 1);
                            
                            from = tmpFrom.getTime();
                            tmpTo = new GregorianCalendar();
                            if (timezone != null) {
                                tmpTo.setTimeZone(TimeZone.getTimeZone(timezone));
                            }
                            tmpTo.setTime(sdf.parse(fromTo[1]));
                            tmpTo.add(Calendar.YEAR, 1);
                            tmpTo.add(Calendar.DAY_OF_YEAR, 1);
                            
                            if ((tmpFrom.get(Calendar.MONTH) > tmpTo.get(Calendar.MONTH)) && (tmpFrom.get(Calendar.YEAR) == tmpTo.get(Calendar.YEAR))) {
                                tmpTo.add(Calendar.YEAR, 1);
                            }
                            
                            to = tmpTo.getTime();
                            holidays.add(new TimePeriod(from, to));
                        }
                    } else {
                        
                        Calendar c = new GregorianCalendar();
                        c.setTime(sdf.parse(fromTo[0]));
                        c.add(Calendar.DAY_OF_YEAR, 1);
                        // handle one day holiday
                        holidays.add(new TimePeriod(sdf.parse(fromTo[0]), c.getTime()));
                        if (addNextYearHolidays) {
                            Calendar tmp = Calendar.getInstance();
                            tmp.setTime(sdf.parse(fromTo[0]));
                            tmp.add(Calendar.YEAR, 1);
                            
                            Date from = tmp.getTime();
                            c.add(Calendar.YEAR, 1);
                            holidays.add(new TimePeriod(from, c.getTime()));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while parsing holiday in business calendar", e);
                }
            }
        }
        return holidays;
    }
    
    protected void parseWeekendDays() {
        String weekendDays = businessCalendarConfiguration.getProperty(WEEKEND_DAYS);
        
        if (weekendDays == null) {
            this.weekendDays.add(Calendar.SATURDAY);
            this.weekendDays.add(Calendar.SUNDAY);
        } else {
            String[] days = weekendDays.split(",");
            for (String day : days) {
                this.weekendDays.add(Integer.parseInt(day));
            }
        }
    }

    private Boolean hasBusinessHourMinutes(String propValue){
        return(!propValue.equals(NONE));
    }

    private Map<Integer, Integer> hoursminsToMapInt(String businessHoursMins) {
        return Arrays.asList(businessHoursMins)
                .stream()
                .map(str -> str.split(":"))
                .collect(toMap(str -> Integer.valueOf(str[0]), str -> Integer.valueOf(str[1])));
    }

    private boolean isTodayHoliday(Calendar c) {
        if (!holidays.isEmpty()) {
            Date current = c.getTime();
            for (TimePeriod holiday : holidays) {
                if (current.after(holiday.getFrom()) && current.before(holiday.getTo())) {
                    return true;
                }
            }
        }
        return false;
    }

    private class TimePeriod {
        private Date from;
        private Date to;

        protected TimePeriod(Date from, Date to) {
            this.from = from;
            this.to = to;
        }

        protected Date getFrom() {
            return this.from;
        }
        
        protected Date getTo() {
            return this.to;
        }
    }

    protected long getCurrentTime() {
        if (clock != null) {
            return clock.getCurrentTime();
        } else {
            return System.currentTimeMillis();
        }
    }
    
    protected boolean isWorkingDay(int day) {
        if (weekendDays.contains(day)) {
            return false;
        }
        
        return true;
    }
    protected void handleWeekend(Calendar c, boolean resetTime) {
        int dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        while (!isWorkingDay(dayOfTheWeek)) {
            c.add(Calendar.DAY_OF_YEAR, 1);
            if (resetTime) {
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
            }
            dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        }
    }
}

