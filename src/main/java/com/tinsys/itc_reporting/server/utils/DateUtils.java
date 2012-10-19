package com.tinsys.itc_reporting.server.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;

public class DateUtils {

    public static PeriodDTO monthYearToPeriod(long id,int aMonth,int aYear){
        PeriodDTO period = new PeriodDTO();
        Calendar cal1 = new GregorianCalendar();
        cal1.set(Calendar.YEAR , aYear);
        cal1.set(Calendar.MONTH, aMonth);
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        period.setStartDate(cal1.getTime());
        
        CalendarUtil.addMonthsToDate(cal1.getTime(),1);
        CalendarUtil.addDaysToDate(cal1.getTime(),-1);
        period.setStopDate(cal1.getTime());
        period.setId(id);
        return period;
    }

}
