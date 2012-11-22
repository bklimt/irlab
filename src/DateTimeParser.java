
import java.util.regex.*;

public class DateTimeParser {
	public static DateTime parse(String datetime) {
		if ( datetime.toUpperCase().equals("NOON") || datetime.toUpperCase().matches("12(:00)? ?NOON") ) {
			DateTime time = new DateTime();
			time.hour = new Integer(12);
			time.minute = new Integer(0);
			return time;
		}

		datetime = datetime.toUpperCase();
		datetime = datetime.replaceAll(" TO ", " - ");
		datetime = datetime.replaceAll("--", "-");
		datetime = datetime.replaceAll("\\(GMT\\+[0-9]*:00\\)", "");
		datetime = datetime.replaceAll(" *$", "");

		Matcher dateMatcher4 = Pattern.compile("([0-9]*)[/-]([0-9]*)(?:[/-]([0-9]*))?").matcher(datetime);
		if ( dateMatcher4.matches() ) {
			DateTime date = new DateTime();
			date.month = new Integer(dateMatcher4.group(1));
			date.day = new Integer(dateMatcher4.group(2));
			if ( dateMatcher4.group(3) != null ) {
				date.year = new Integer(dateMatcher4.group(3));
				if ( date.year.intValue() < 100 ) {
					date.year = new Integer(date.year.intValue()+2000);
				}
			}
			return date;
		}

		Matcher timeMatcher1 = Pattern.compile("([0-9]*)[:.]([0-9]*) ?(([aApP])\\.?[mM]\\.?)?").matcher(datetime);
		if ( timeMatcher1.matches() ) {
			DateTime time = new DateTime();
			time.hour = new Integer(timeMatcher1.group(1));
			time.minute = new Integer(timeMatcher1.group(2));
			if ( timeMatcher1.group(4) != null && timeMatcher1.group(4).equals("P") ) {
				time.pm = new Boolean(true);
			}
			if ( timeMatcher1.group(4) != null && timeMatcher1.group(4).equals("A") ) {
				time.pm = new Boolean(false);
			}
			return time;
		}

		Matcher timeMatcher3 = Pattern.compile("([0-9]*) ?([aApP])\\.?[mM]\\.?").matcher(datetime);
		if ( timeMatcher3.matches() ) {
			DateTime time = new DateTime();
			time.hour = new Integer(timeMatcher3.group(1));
			time.minute = new Integer(0);
			if ( timeMatcher3.group(2).equals("P") ) {
				time.pm = new Boolean(true);
			} else {
				time.pm = new Boolean(false);
			}
			return time;
		}

		String pattern5 = "([0-9]*)([:.]([0-9]*))? ?([AP]\\.?[M]\\.?)? ?- ?[0-9]*([:.][0-9]*)? ?(([aApP])\\.?[mM]\\.?)?";
		Matcher timeMatcher5 = Pattern.compile(pattern5).matcher(datetime);
		if ( timeMatcher5.matches() ) {
			DateTime time = new DateTime();
			time.hour = new Integer(timeMatcher5.group(1));
			time.minute = new Integer( ((timeMatcher5.group(3)!=null)? timeMatcher5.group(3) : "0") );
			if ( timeMatcher5.group(6) != null && timeMatcher5.group(6).equals("P") ) {
				time.pm = new Boolean(true);
			}
			if ( timeMatcher5.group(6) != null && timeMatcher5.group(6).equals("A") ) {
				time.pm = new Boolean(false);
			}
			return time;
		}

		String weekday = "((?:MON|TUES?|WED(?:NES)?|THURS?|FRI|SAT(?:UR)?|SUN)(?:DAY)?)";
		String month = "(?:(JAN(?:UARY)?|FEB(?:RUARY)?|MAR(?:CH)?|APR(?:IL)?|MAY|JUNE?|JULY?|AUG(?:UST)?|SEP(?:T(?:EMBER)?)?|OCT(?:OBER)?|NOV(?:EMBER)?|DEC(?:EMBER)?)\\.?)?";
		String day = "(?:([0-9][0-9]*)(?:RD|ND|ST|TH)?)";

		Matcher dateMatcher1 = Pattern.compile("\\*?"+weekday+"?\\*?[/,]? ?\\(?"+month+"? ?"+day+"\\)?(?:[, ] ?([0-9]*))?").matcher(datetime);
		if ( dateMatcher1.matches() ) {
			DateTime date = new DateTime();
			if ( dateMatcher1.group(1) != null ) {
				date.weekday = resolveWeekday(dateMatcher1.group(1));
			}
			date.day = new Integer(dateMatcher1.group(3));
			if ( dateMatcher1.group(2) != null ) {
				date.month = resolveMonth(dateMatcher1.group(2));
			}
			if ( dateMatcher1.group(4) != null ) {
				date.year = new Integer(dateMatcher1.group(4));
			}
			return date;
		}

		Matcher dateMatcher2 = Pattern.compile("\\*?"+weekday+"?\\*?[/,]? ?"+day+" ?(?:OF )?"+month+"?(?:[, ] ?([0-9]*))?").matcher(datetime);
		if ( dateMatcher2.matches() ) {
			DateTime date = new DateTime();
			if ( dateMatcher2.group(1) != null ) {
				date.weekday = resolveWeekday(dateMatcher2.group(1));
			}
			date.day = new Integer(dateMatcher2.group(2));
			if ( dateMatcher2.group(3) != null ) {
				date.month = resolveMonth(dateMatcher2.group(3));
			}
			if ( dateMatcher2.group(4) != null ) {
				date.year = new Integer(dateMatcher2.group(4));
			}
			return date;
		}

		Matcher dateMatcher3 = Pattern.compile(month+"? ?"+day+"(?:[, ] ?([0-9][0-9]*))?,? ?\\(?"+weekday+"?\\)?").matcher(datetime);
		if ( dateMatcher3.matches() ) {
			DateTime date = new DateTime();
			if ( dateMatcher3.group(4) != null ) {
				date.weekday = resolveWeekday(dateMatcher3.group(4));
			}
			date.day = new Integer(dateMatcher3.group(2));
			if ( dateMatcher3.group(1) != null ) {
				date.month = resolveMonth(dateMatcher3.group(1));
			}
			if ( dateMatcher3.group(3) != null ) {
				date.year = new Integer(dateMatcher3.group(3));
			}
			return date;
		}

		Matcher dateMatcher5 = Pattern.compile(weekday + ",? ?\\(?([0-9]*)[/-]([0-9]*)(?:[/-]([0-9]*))?\\)?").matcher(datetime);
		if ( dateMatcher5.matches() ) {
			DateTime date = new DateTime();
			if ( dateMatcher5.group(1) != null ) {
				date.weekday = resolveWeekday(dateMatcher5.group(1));
			}
			date.month = new Integer(dateMatcher5.group(2));
			date.day = new Integer(dateMatcher5.group(3));
			if ( dateMatcher5.group(4) != null ) {
				date.year = new Integer(dateMatcher5.group(4));
				if ( date.year.intValue() < 100 ) {
					date.year = new Integer(date.year.intValue()+2000);
				}
			}
			return date;
		}

		Matcher dateMatcher6 = Pattern.compile(weekday).matcher(datetime);
		if ( dateMatcher6.matches() ) {
			DateTime date = new DateTime();
			date.weekday = resolveWeekday(dateMatcher6.group(1));
			return date;
		}

		Matcher dateMatcher7 = Pattern.compile(month+"? ?"+day+"(?: \\(?"+weekday+"\\)?)?(?:[, ] ?([0-9][0-9]*))?").matcher(datetime);
		if ( dateMatcher7.matches() ) {
			DateTime date = new DateTime();
			if ( dateMatcher7.group(3) != null ) {
				date.weekday = resolveWeekday(dateMatcher7.group(3));
			}
			date.day = new Integer(dateMatcher7.group(2));
			if ( dateMatcher7.group(1) != null ) {
				date.month = resolveMonth(dateMatcher7.group(1));
			}
			if ( dateMatcher7.group(4) != null ) {
				date.year = new Integer(dateMatcher7.group(4));
			}
			return date;
		}

		/*
		Thursday and &gt;&gt;&gt;Friday December 4th &amp; 5th
		*/

		return null;
	}

	public static Integer resolveWeekday(String weekday) {
		String s = weekday.toUpperCase();
		if ( s.equals("TUE") ) { s = s+"S"; }
		if ( s.equals("WED") ) { s = s+"NES"; }
		if ( s.equals("THUR") ) { s = s+"S"; }
		if ( s.equals("SAT") ) { s = s+"UR"; }
		if ( !s.matches(".*DAY$") ) { s = s+"DAY"; }
		if ( s.equals("SUNDAY") ) { return new Integer(0); }
		if ( s.equals("MONDAY") ) { return new Integer(1); }
		if ( s.equals("TUESDAY") ) { return new Integer(2); }
		if ( s.equals("WEDNESDAY") ) { return new Integer(3); }
		if ( s.equals("THURSDAY") ) { return new Integer(4); }
		if ( s.equals("FRIDAY") ) { return new Integer(5); }
		if ( s.equals("SATURDAY") ) { return new Integer(6); }
		return new Integer(weekday);
	}

	public static Integer resolveMonth(String month) {
		String s = month.toUpperCase();
		if ( s.equals("JAN") ) { s+="UARY"; }
		if ( s.equals("FEB") ) { s+="RUARY"; }
		if ( s.equals("MAR") ) { s+="CH"; }
		if ( s.equals("APR") ) { s+="IL"; }
		if ( s.equals("JUN") ) { s+="E"; }
		if ( s.equals("JUL") ) { s+="Y"; }
		if ( s.equals("AUG") ) { s+="UST"; }
		if ( s.equals("SEP") ) { s+="T"; }
		if ( s.equals("SEPT") ) { s+="EMBER"; }
		if ( s.equals("OCT") ) { s+="OBER"; }
		if ( s.equals("NOV") ) { s+="EMBER"; }
		if ( s.equals("DEC") ) { s+="EMBER"; }
		if ( s.equals("JANUARY") ) { return new Integer(1); }
		if ( s.equals("FEBRUARY") ) { return new Integer(2); }
		if ( s.equals("MARCH") ) { return new Integer(3); }
		if ( s.equals("APRIL") ) { return new Integer(4); }
		if ( s.equals("MAY") ) { return new Integer(5); }
		if ( s.equals("JUNE") ) { return new Integer(6); }
		if ( s.equals("JULY") ) { return new Integer(7); }
		if ( s.equals("AUGUST") ) { return new Integer(8); }
		if ( s.equals("SEPTEMBER") ) { return new Integer(9); }
		if ( s.equals("OCTOBER") ) { return new Integer(10); }
		if ( s.equals("NOVEMBER") ) { return new Integer(11); }
		if ( s.equals("DECEMBER") ) { return new Integer(12); }
		return new Integer(month);
	}

	public static void main(String[] args) {
		for ( int i=0; i<args.length; i++ ) {
			System.out.println(parse(args[i]));
		}
	}
}

