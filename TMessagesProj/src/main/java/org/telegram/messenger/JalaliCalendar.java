package org.telegram.messenger;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class JalaliCalendar {

    public static class YearMonthDay {
        public int year;
        public int month;
        public int day;

        public YearMonthDay(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        public String toString() {
            return year + "/" + (month + 1) + "/" + day;
        }
    }

    public static YearMonthDay gregorianToJalali(int g_y, int g_m, int g_d) {
        int[] g_days_in_month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] j_days_in_month = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

        int gy = g_y - 1600;
        int gm = g_m - 1;
        int gd = g_d - 1;

        int g_day_no = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400;

        for (int i = 0; i < gm; ++i)
            g_day_no += g_days_in_month[i];
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)))
            /* leap and after Feb */
            g_day_no++;
        g_day_no += gd;

        int j_day_no = g_day_no - 79;

        int j_np = j_day_no / 12053;
        j_day_no = j_day_no % 12053;

        int jy = 979 + 33 * j_np + 4 * (j_day_no / 1461);

        j_day_no %= 1461;

        if (j_day_no >= 366) {
            jy += (j_day_no - 1) / 365;
            j_day_no = (j_day_no - 1) % 365;
        }

        int i;
        for (i = 0; i < 11 && j_day_no >= j_days_in_month[i]; ++i)
            j_day_no -= j_days_in_month[i];
        int jm = i;
        int jd = j_day_no + 1;

        return new YearMonthDay(jy, jm, jd);
    }

    public static String getJalaliDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date * 1000);
        YearMonthDay j = gregorianToJalali(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        return j.year + " " + getMonthName(j.month) + " " + j.day;
    }

    public static String getMonthName(int month) {
        switch (month) {
            case 0: return "فروردین";
            case 1: return "اردیبهشت";
            case 2: return "خرداد";
            case 3: return "تیر";
            case 4: return "مرداد";
            case 5: return "شهریور";
            case 6: return "مهر";
            case 7: return "آبان";
            case 8: return "آذر";
            case 9: return "دی";
            case 10: return "بهمن";
            case 11: return "اسفند";
        }
        return "";
    }
}
