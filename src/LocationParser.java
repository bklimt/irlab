
import java.util.*;
import java.util.regex.*;

public class LocationParser {
	private static HashMap aliases = null;

	public static void initAliases() {
		aliases = new HashMap();
		aliases.put("SCAIFE ?HALL", "SCAIFE HALL");
		aliases.put("BAKER ?HALL", "BAKER HALL");
		aliases.put("SIGMA ?BUILDING", "SIGMA BUILDING");
		aliases.put("UNIVERSITY ?CENTER", "UNIVERSITY CENTER");
		aliases.put("NEWELL-? ?SIMON HALL", "NEWELL-SIMON HALL");
		aliases.put("NSH", "NEWELL-SIMON HALL");
		aliases.put("WEAN ?(?:HALL)?", "WEAN NALL");
		aliases.put("WEH", "WEAN HALL");
	}

	public static String unAlias(String building) {
		if ( aliases == null ) {
			initAliases();
		}
		Set keys = aliases.keySet();
		Iterator iterator = keys.iterator();
		while ( iterator.hasNext() ) {
			String key = (String)iterator.next();
			if ( building.matches(key) ) {
				return (String)aliases.get(key);
			}
		}
		return building;
	}

	public static String getBuildingRegEx() {
		if ( aliases == null ) {
			initAliases();
		}
		String ans = "";
		Set keys = aliases.keySet();
		Iterator iterator = keys.iterator();
		while ( iterator.hasNext() ) {
			String name = (String)iterator.next();
			if ( ans.equals("") ) {
				ans += "(";
			} else {
				ans += "|";
			}
			ans += name;
		}
		ans += ")";
		return ans;
	}

	public static Location parse(String room) {
		room = room.toUpperCase();
		room = room.replaceAll(" \\([0-9]F\\)", "");
		room = room.replaceAll(", [0-9]\\/?F,", "");
		room = room.replaceAll(", [0-9]\\/?F$", "");
		room = room.replaceAll("ROOM #", "");
		String building = getBuildingRegEx();

		Matcher roomMatcher1 = Pattern.compile(building+",? ?([A-Z0-9 ]+)").matcher(room);
		if ( roomMatcher1.matches() ) {
			Location location = new Location();
			location.building = unAlias(roomMatcher1.group(1));
			location.room = roomMatcher1.group(2);
			return location;
		}

		Matcher roomMatcher2 = Pattern.compile("([A-Z0-9 ]+),? "+building).matcher(room);
		if ( roomMatcher2.matches() ) {
			Location location = new Location();
			location.building = unAlias(roomMatcher2.group(2));
			location.room = roomMatcher2.group(1);
			return location;
		}

		Matcher roomMatcher3 = Pattern.compile("(.*) ROOM$").matcher(room);
		if ( roomMatcher3.matches() ) {
			Location location = new Location();
			location.building = null;
			location.room = roomMatcher3.group(1);
			return location;
		}

		Location location = new Location();
		location.building = null;
		location.room = room;
		return location;
	}

	public static void main(String[] args) {
		for ( int i=0; i<args.length; i++ ) {
			System.out.println(parse(args[i]));
		}
	}

}

