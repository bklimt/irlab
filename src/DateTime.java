
public class DateTime {
	public Integer weekday = null;
	public Integer month = null;
	public Integer day = null;
	public Integer year = null;
	public Integer hour = null;
	public Integer minute = null;
	public Boolean pm = null;

	public String toString() {
		String sweekday = ( weekday == null ? "??" : weekday.toString() );
		String smonth = ( month == null ? "??" : month.toString() );
		String sday = ( day == null ? "??" : day.toString() );
		String syear = ( year == null ? "????" : year.toString() );
		String shour = ( hour == null ? "??" : hour.toString() );
		String sminute = ( minute == null ? "??" : minute.toString() );
		String spm = ( pm == null ? "??" : (pm.booleanValue() ? "pm" : "am") );
		if ( sminute.equals("0") ) {
			sminute = "00";
		}
		return sweekday + ", " + syear + "-" + smonth + "-" + sday + " " + shour + ":" + sminute + " " + spm;
	}

	public static boolean consistent(DateTime time1, DateTime time2) {
		if ( time1.weekday != null && time2.weekday != null && !time1.weekday.equals(time2.weekday) ) { return false; }
		if ( time1.month != null && time2.month != null && !time1.month.equals(time2.month) ) { return false; }
		if ( time1.day != null && time2.day != null && !time1.day.equals(time2.day) ) { return false; }
		if ( time1.year != null && time2.year != null && !time1.year.equals(time2.year) ) { return false; }
		if ( time1.hour != null && time2.hour != null && !time1.hour.equals(time2.hour) ) { return false; }
		if ( time1.minute != null && time2.minute != null && !time1.minute.equals(time2.minute) ) { return false; }
		if ( time1.pm != null && time2.pm != null && !time1.pm.equals(time2.pm) ) { return false; }
		return true;
	}
}

