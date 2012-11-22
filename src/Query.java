
public class Query {
	public static abstract class Filter {
		public abstract boolean matches(Seminar seminar);
	}

	public static class ORFilter extends Filter {
		Filter[] filters;

		public ORFilter(Filter[] filters) {
			this.filters = filters;
		}

		public boolean matches(Seminar seminar) {
			if ( filters.length == 0 ) {
				return true;
			}
			for (int i=0; i<filters.length; i++) {
				if (filters[i].matches(seminar)) {
					return true;
				}
			}
			return false;
		}

		public String toString() {
			String ans = "(*OR*";
			for ( int i=0; i<filters.length; i++ ) {
				ans += (" " + filters[i].toString());
			}
			ans += ")";
			return ans;
		}
	}

	public static class ANDFilter extends Filter {
		Filter[] filters;

		public ANDFilter(Filter[] filters) {
			this.filters = filters;
		}

		public boolean matches(Seminar seminar) {
			for (int i=0; i<filters.length; i++) {
				if (!filters[i].matches(seminar)) {
					return false;
				}
			}
			return true;
		}

		public String toString() {
			String ans = "(*AND*";
			for ( int i=0; i<filters.length; i++ ) {
				ans += (" " + filters[i].toString());
			}
			ans += ")";
			return ans;
		}
	}

	public static class NAMEFilter extends Filter {
		String name;
		public NAMEFilter(String name) {
			this.name = name;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasSimilarName(name);
		}
		public String toString() {
			return "(NAME \"" + name + "\")";
		}
	}

	public static class TITLEFilter extends Filter {
		String title;
		public TITLEFilter(String title) {
			this.title = title;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasSimilarTitle(title);
		}
		public String toString() {
			return "(TITLE \"" + title + "\")";
		}
	}

	public static class SERIESFilter extends Filter {
		String series;
		public SERIESFilter(String series) {
			this.series = series;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasSimilarSeries(series);
		}
		public String toString() {
			return "(SERIES \"" + series + "\")";
		}
	}

	public static class AFFILIATIONFilter extends Filter {
		String affiliation;
		public AFFILIATIONFilter(String affiliation) {
			this.affiliation = affiliation;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasSimilarAffiliation(affiliation);
		}
		public String toString() {
			return "(AFFILIATION \"" + affiliation + "\")";
		}
	}

	public static class LOCATIONFilter extends Filter {
		String location;
		public LOCATIONFilter(String location) {
			this.location = location;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasLocation(location);
		}
		public String toString() {
			return "(LOCATION \"" + location + "\")";
		}
	}

	public static class TIMEFilter extends Filter {
		DateTime time;
		public TIMEFilter(DateTime time) {
			this.time = time;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasTime(time);
		}
		public String toString() {
			return "(TIME \"" + time.toString() + "\")";
		}
	}

	public static class DATEFilter extends Filter {
		DateTime datetime;
		public DATEFilter(DateTime datetime) {
			this.datetime = datetime;
		}
		public boolean matches(Seminar seminar) {
			return seminar.hasDate(datetime);
		}
		public String toString() {
			return "(DATE \"" + datetime.toString() + "\")";
		}
	}

	String[] fields = null;
	Filter filter = null;

	public Query(String[] fields, Filter filter) {
		this.fields = fields;
		this.filter = filter;
	}

	public boolean matches(Seminar seminar) {
		return filter.matches(seminar);
	}

	public boolean isExistQuery() {
		for ( int i=0; i<fields.length; i++ ) {
			if ( fields[i].equals("EXIST") ) {
				return true;
			}
		}
		return false;
	}

	public String[] getFields(Seminar seminar) throws NoSuchFieldException {
		String[] answers = new String[fields.length];
		for ( int i=0; i<fields.length; i++ ) {
			String field = fields[i];
			if ( field.equals("LOCATION") ) {
				answers[i] = seminar.getLocation();
			} else if ( field.equals("AFFILIATION") ) {
				answers[i] = seminar.getAffiliation();
			} else if ( field.equals("NAME") ) {
				answers[i] = seminar.getName();
			} else if ( field.equals("TIME") ) {
				answers[i] = seminar.getTime();
			} else if ( field.equals("TITLE") ) {
				answers[i] = seminar.getTitle();
			} else if ( field.equals("DATE") )  {
				answers[i] = seminar.getDate();
			} else if ( field.equals("DATETIME") ) {
				answers[i] = seminar.getDate() + " at " + seminar.getTime();
			} else if ( field.equals("SERIES") ) {
				answers[i] = seminar.getSeries();
			} else if ( field.equals("EXIST") ) {
				answers[i] = "yes";
			} else {
				throw new NoSuchFieldException(field);
			}
		}
		return answers;
	}

	public String toString() {
		String ans = "((FIELD";
		for (int i=0; i<fields.length; i++) {
			ans += " ";
			ans += fields[i];
		}
		ans += (")(FILTER " + filter.toString() + "))");
		return Lisp.format(ans);
	}
}

