
public class Annotation {
	public int offset;
	public int length;
	public String type;
	public String text;

	public Annotation( String type, int offset, int length ) {
		this.offset = offset;
		this.length = length;
		this.type = type;
	}

	public Annotation( String type, int offset, int length, String text ) {
		this.offset = offset;
		this.length = length;
		this.type = type;
		this.text = text;
	}

	public String toString() {
		if ( text == null ) {
			return type + "[" + offset + ":" + (offset+length) + "]";
		} else {
			return type + "[" + offset + ":" + (offset+length) + "]: \"" + text.replaceAll("\\r","\\\\r").replaceAll("\\n","\\\\n") + "\"";
		}
	}

	public boolean equals( Annotation other ) {
		return offset == other.offset && length == other.length && type.equals(other.type);
	}

	public boolean contains( Annotation other ) {
		return offset <= other.offset && (length+offset) >= (other.length+other.offset) && type.equals(other.type);
	}
}

