package com.tinsys.itc_reporting.server.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;

public class DateUtils {

    public static PeriodDTO monthYearToPeriod(Long id,int aMonth,int aYear,String aPeriodType){
        PeriodDTO period = new PeriodDTO();
        Calendar cal1 = new GregorianCalendar();
        cal1.set(Calendar.YEAR , aYear);
        cal1.set(Calendar.MONTH, aMonth-1);
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal1.set(2012, 0, 1);
        period.setStartDate(cal1.getTime());
        Date endOfMonthDate = cal1.getTime();
        CalendarUtil.addMonthsToDate(endOfMonthDate,1);
        CalendarUtil.addDaysToDate(endOfMonthDate,-1);
        cal1.set(2012, 1, 4);

        period.setStopDate(cal1.getTime());
        period.setPeriodType(aPeriodType);
        period.setId(id);
        return period;
    }

}
