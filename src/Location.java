
import java.util.*;

public class Location {
	public String building = null;
	public String room = null;

	public boolean equals(Location other) {
		if ( other.building != null && building != null ) {
			if ( !other.building.equals(building) ) {
				return false;
			}
		}
		if ( other.room != null && room != null ) {
			if ( !other.room.equals(room) ) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		if ( building != null && room != null ) {
			return room + ", " + building;
		}
		if ( room != null ) {
			return room;
		}
		if ( building != null ) {
			return building;
		}
		return null;
	}
}

