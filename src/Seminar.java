
import java.util.*;

public class Seminar {
	private Annotation[] annotations = null;
	static HashMap editDistanceCache = new HashMap();

	public Seminar(Annotation[] annotations) {
		this.annotations = annotations;
	}

	public Object clone() {
		if ( annotations == null ) {
			return new Seminar(null);
		} else {
			return new Seminar((Annotation[])annotations.clone());
		}
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public static void clearCache() {
		if ( editDistanceCache.size() > 12000 ) {
			editDistanceCache = new HashMap();
		}
	}

	public String getAttribute(String type) {
		String temp = "";
		HashSet seen = new HashSet();
		for (int i=0; i<annotations.length; i++ ) {
			if ( type.equals(annotations[i].type) ) {
				if ( annotations[i].text == null ) {
					annotations[i].text = "NULL";
				}
				if ( temp.equals("") ) {
					temp = annotations[i].text;
					seen.add(annotations[i].text);
					if ( !type.equals("name") ) {
						return temp;
					}
				} else {
					boolean found = false;
					Iterator iterator = seen.iterator();
					while ( iterator.hasNext() ) {
						String text = (String)iterator.next();
						if ( similarName(text,annotations[i].text) ) {
							found = true;
						}
					}
					if ( !found ) {
						temp = temp + " and " + annotations[i].text;
					}
					seen.add(annotations[i].text);
				}
			}
		}
		return temp;
	}

	public String getName() { return getAttribute("name"); }
	public String getTitle() { return getAttribute("title"); }
	public String getDate() { return getAttribute("date"); }
	public String getTime() { return getAttribute("time"); }
	public String getAffiliation() { return getAttribute("affiliation"); }
	public String getSeries() { return getAttribute("series"); }
	public String getLocation() { return getAttribute("location"); }

	public String toString() {
		return "<HTML>seminar {<br>\n" +
			"&nbsp;&nbsp;title = " + getTitle() + "<br>\n" +
			"&nbsp;&nbsp;name  = " + getName() + "<br>\n" +
			"&nbsp;&nbsp;time  = " + getTime() + "<br>\n" +
			"&nbsp;&nbsp;date  = " + getDate() + "<br>\n" +
			"&nbsp;&nbsp;location = " + getLocation() + "<br>\n" +
			"&nbsp;&nbsp;affiliation  = " + getAffiliation() + "<br>\n" +
			"&nbsp;&nbsp;series  = " + getSeries() + "<br>\n" +
		"}</HTML>";
		//return date+"\t"+time+"\t"+name+"\t"+title+"\t"+affiliation+"\t"+series+"\n";
	}

	public boolean hasSimilarName(String name) {
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("name") ) {
				if ( similarName(name, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasName(String name) {
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("name") ) {
				if ( sameName(name, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasSimilarTitle(String title) {
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("title") ) {
				if ( similarTitle(title, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasTitle(String title) {
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("title") ) {
				if ( sameTitle(title, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasDate(String date) {
		boolean foundDate = false;
		boolean dateCorrect = true;
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("date") ) {
				foundDate = true;
				if ( !sameDate(date, annotations[i].text) ) {
					dateCorrect = false;
				}
			}
		}
		if ( !foundDate && date.equals("") ) {
			return true;
		}
		return (foundDate && dateCorrect);
	}

	public boolean hasDate(DateTime date) {
		boolean foundDate = false;
		boolean dateCorrect = true;
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("date") ) {
				foundDate = true;
				if ( !sameDate(date, annotations[i].text) ) {
					dateCorrect = false;
				}
			}
		}
		if ( !foundDate && date==null ) {
			return true;
		}
		return (foundDate && dateCorrect);
	}

	public boolean hasTime(String time) {
		boolean foundTime = false;
		boolean timeCorrect = true;
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("time") ) {
				foundTime = true;
				if ( !sameTime(time, annotations[i].text) ) {
					timeCorrect = false;
				}
			}
		}
		if ( !foundTime && time.equals("") ) {
			return true;
		}
		return (foundTime && timeCorrect);
	}

	public boolean hasTime(DateTime time) {
		boolean foundTime = false;
		boolean timeCorrect = true;
		for ( int i=0; i<annotations.length; i++ )  {
			if ( annotations[i].type.equals("time") ) {
				foundTime = true;
				if ( !sameTime(time, annotations[i].text) ) {
					timeCorrect = false;
				}
			}
		}
		if ( !foundTime && time==null ) {
			return true;
		}
		return (foundTime && timeCorrect);
	}

	public boolean hasLocation(String location) {
		for ( int i=0; i<annotations.length; i++ ) {
			if ( annotations[i].type.equals("location") ) {
				if ( sameLocation(location, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasSimilarSeries(String series) {
		for ( int i=0; i<annotations.length; i++ ) {
			if ( annotations[i].type.equals("series") ) {
				if ( similarSeries(series, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasSeries(String series) {
		for ( int i=0; i<annotations.length; i++ ) {
			if ( annotations[i].type.equals("series") ) {
				if ( sameSeries(series, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasSimilarAffiliation(String affiliation) {
		for ( int i=0; i<annotations.length; i++ ) {
			if ( annotations[i].type.equals("affiliation") ) {
				if ( similarAffiliation(affiliation, annotations[i].text) ) {
					return true;
				}
			}
		}
		return false;
	}


	public static int editDistance(String s1, String s2) {
		int ans = 0;
		String key = s1 + "<->" + s2;
		if ( editDistanceCache.get(key) != null ) {
			ans = ((Integer)editDistanceCache.get(key)).intValue();
		} else if ( s1.equals(s2) ) {
			ans = 0;
		} else if ( s1.length()==0 && s2.length()==0 ) {
			ans = 0;
		} else if ( s1.length()==0 || s2.length()==0 ) {
			ans = s1.length() + s2.length();
		} else {
			char c1 = s1.charAt(s1.length()-1);
			char c2 = s2.charAt(s2.length()-1);
			String ss1 = s1.substring(0,s1.length()-1);
			String ss2 = s2.substring(0,s2.length()-1);
			int v1 = editDistance(ss1,ss2) + (c1==c2 ? 0 : 1);
			int v2 = editDistance(s1,ss2)+1;
			int v3 = editDistance(ss1,s2)+1;
			if ( v1 <= v2 && v1 <= v3 ) {
				ans = v1;
			} else if ( v2 <= v3 ) {
				ans = v2;
			} else {
				ans = v3;
			}
		}
	    editDistanceCache.put(key,new Integer(ans));
		return ans;
	}

	public static String normalizeName(String name) {
		name = name.toUpperCase();
		name = name.replaceAll("[^A-Z ]", "");
		name = name.replaceAll("^DR ", "");
		name = name.replaceAll("^PROF ", "");
		name = name.replaceAll("^PROFESSOR ", "");
		name = name.replaceAll("^SIR ", "");
		return name;
	}

	public static String normalizeTitle(String name) {
		name = name.toUpperCase();
		name = name.replaceAll("[^A-Z ]"," ");
		while ( name.matches("^.*  .*$") ) {
			name = name.replaceAll("  "," ");
		}
		return name;
	}

	public static String normalizeSeries(String series) {
		series = series.toUpperCase();
		series = series.replaceAll(" ","");
		return series;
	}

	public static String normalizeAffiliation(String affiliation) {
		return normalizeSeries(affiliation);
	}

	public static boolean sameName(String name1, String name2) {
		return (editDistance(normalizeName(name1), normalizeName(name2))<=2);
	}

	public static boolean similarName(String name1, String name2) {
		return normalizeName(name2).indexOf(normalizeName(name1))!=-1 || 
			(editDistance(normalizeName(name1), normalizeName(name2))<=3);
	}

	public static boolean sameSeries(String series1, String series2) {
		return normalizeSeries(series1).equals(normalizeSeries(series2));
	}

	public static boolean similarSeries(String series1, String series2) {
		//System.out.println("Does "+series2+" contain "+series1);
		return normalizeSeries(series2).indexOf(normalizeSeries(series1))!=-1;
	}

	public static boolean sameAffiliation(String affiliation1, String affiliation2) {
		return normalizeAffiliation(affiliation1).equals(normalizeAffiliation(affiliation2));
	}

	public static boolean similarAffiliation(String affiliation1, String affiliation2) {
		return sameAffiliation(affiliation1, affiliation2);
	}

	public static boolean sameTitle(String title1, String title2) {
		//System.out.println("comparing "+normalizeTitle(title1)+" to "+normalizeTitle(title2));
		return normalizeTitle(title1).equals(normalizeTitle(title2));
	}

	public static boolean similarTitle(String title1, String title2) {
		return sameTitle(title1,title2);
	}

	public static boolean sameDate(String s1, String s2) {
		DateTime date1 = DateTimeParser.parse(s1);
		DateTime date2 = DateTimeParser.parse(s2);
		if ( date1 == null || date2 == null ) {
			return false;
		}
		return DateTime.consistent(date1,date2);
	}

	public static boolean sameDate(DateTime date1, String s2) {
		DateTime date2 = DateTimeParser.parse(s2);
		//System.out.println("comparing "+date1+" to "+date2);
		if ( date1 == null || date2 == null ) {
			return false;
		}
		return DateTime.consistent(date1,date2);
	}

	public static boolean sameTime(String s1, String s2) {
		DateTime time1 = DateTimeParser.parse(s1);
		DateTime time2 = DateTimeParser.parse(s2);
		if ( time1 == null || time2 == null ) {
			return false;
		}
		return DateTime.consistent(time1,time2);
	}

	public static boolean sameTime(DateTime time1, String s2) {
		DateTime time2 = DateTimeParser.parse(s2);
		if ( time1 == null || time2 == null ) {
			return false;
		}
		return DateTime.consistent(time1,time2);
	}

	public static boolean sameLocation(String s1, String s2) {
		Location location1 = LocationParser.parse(s1);
		Location location2 = LocationParser.parse(s2);
		//System.out.println("comparing "+location1+" to "+location2);
		if ( location1 == null || location2 == null ) {
			return false;
		}
		return location1.equals(location2);
	}
}

