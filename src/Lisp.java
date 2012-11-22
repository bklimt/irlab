
import java.util.ArrayList;

class Lisp {
	public static final LispElement t = new LispSymbol("t");
	public static final LispElement nil = new LispSymbol("nil");

	abstract static class LispElement {
		public abstract boolean atom();
		public abstract boolean stringp();
		public abstract boolean symbolp();
		public abstract boolean listp();
		public abstract boolean nullp();
		public abstract boolean equals(LispElement other);
	}
	
	static class LispString extends LispElement {
		String value;
		public LispString(String value) { this.value = value; }
		public String toString() { return "\"" + value + "\""; }
		public boolean equals(LispElement y) { return y.stringp() && ((LispString)y).value.equals(value); }
		public String getValue() { return value; }
		public boolean atom() { return true; }
		public boolean stringp() { return true; }
		public boolean symbolp() { return false; }
		public boolean listp() { return false; }
		public boolean nullp() { return false; }
	}
	
	static class LispSymbol extends LispElement {
		String value;
		public LispSymbol(String value) { this.value = value.toUpperCase(); }
		public String toString() { return value; }
		public String symbolName() { return value; }
		public boolean equals(LispElement y) { return y.symbolp() && ((LispSymbol)y).value.equals(value); }
		public boolean atom() { return true; }
		public boolean stringp() { return false; }
		public boolean symbolp() { return true; }
		public boolean listp() { return false; }
		public boolean nullp() { return value.equals("NIL"); }
	}
	
	static class LispList extends LispElement {
		private LispElement car;
		private LispElement cdr;
	
		public LispList( LispElement car, LispElement cdr ) {
			this.car = car;
			this.cdr = cdr;
		}
	
		public LispElement car() { return car; }
		public LispElement cdr() { return cdr; }
	
		public String toString() {
			return "(" + toNakedString() + ")";
		}

		protected String toNakedString() {
			String ans = "";
			ans = car().toString();
			if ( cdr().atom() ) {
				if ( !cdr().nullp() ) {
					ans += (" . " + cdr().toString());
				}
			} else {
				ans += (" " + ((LispList)cdr()).toNakedString());
			}
			return ans;
		}

		public boolean equals(LispElement other) {
			return other.listp() && ((LispList)other).car.equals(car) && ((LispList)other).cdr.equals(cdr);
		}

		public boolean atom() { return false; }
		public boolean stringp() { return false; }
		public boolean symbolp() { return false; }
		public boolean listp() { return true; }
		public boolean nullp() { return false; }
	}

	public static LispElement cons(LispElement car, LispElement cdr) {
		return new LispList(car,cdr);
	}

	protected static LispElement assoc(LispElement key, LispElement dict) throws ClassCastException {
		if ( dict.nullp() ) {
			return nil;
		} else {
			if ( dict.listp() && ((LispList)dict).car().listp() ) {
				LispList tuple = (LispList)(((LispList)dict).car());
				if ( tuple.car().equals(key) ) {
					return tuple;
				} else {
					return assoc(key,((LispList)dict).cdr());
				}
			}
			throw new ClassCastException("dictionary is invalid");
		}
	}

	protected static int length(LispElement element) throws ClassCastException {
		if ( element.nullp() ) {
			return 0;
		}
		if ( element.stringp() ) {
			return ((LispString)element).getValue().length();
		}
		if ( element.listp() ) {
			if ( ((LispList)element).cdr().listp() ) {
				return 1 + length(((LispList)element).cdr());
			}
			if ( ((LispList)element).cdr().nullp() ) {
				return 1;
			}
		}
		throw new ClassCastException("length is only valid for lists, strings, and nil");
	}

	protected static LispElement nth(int n, LispElement list) throws ClassCastException {
		if ( n < 0 ) {
			throw new IndexOutOfBoundsException(""+n);
		}
		if ( list.nullp() ) {
			return nil;
		}
		if ( list.listp() ) {
			if ( n == 0 ) {
				return ((LispList)list).car();
			} else {
				return nth(n-1, ((LispList)list).cdr());
			}
		}
		throw new ClassCastException(list.toString() + " is not a list");
	}

	protected static LispElement list(LispElement[] elements, int start) {
		if ( start < elements.length-1 ) {
			return cons(elements[start],list(elements,start+1));
		} else {
			return cons(elements[start],nil);
		}
	}

	public static LispElement list(LispElement[] elements) {
		if ( elements.length == 0 ) {
			return nil;
		}
		return list(elements,0);
	}

	protected static int skipWhitespace(String code, int start) throws java.text.ParseException {
		int e = start;
		while ( e < code.length() && code.charAt(e) == ' ' ) {
			e++;
		}
		return e;
	}

	static class MutableInteger {
		int value;
		MutableInteger(int value) { this.value = value; }
		int get() { return value; }
		void set(int value) { this.value = value; }
	}

	protected static LispElement parse(String code, int start, MutableInteger end) throws java.text.ParseException {
		LispElement ans = null;
		if ( start < 0 || start >= code.length() ) {
			throw new java.text.ParseException("bad position", start);
		}
		if ( code.charAt(start) == '(' ) {
			ArrayList elements = new ArrayList();
			int e = start+1;
			do {
				e = skipWhitespace(code, e);
				if ( e < code.length() ) {
					MutableInteger temp = new MutableInteger(-1);
					LispElement element = parse(code, e, temp);
					elements.add(element);
					e = temp.get();
				}
				if ( e >= code.length() ) {
					throw new java.text.ParseException("unterminated list", e-1);
				}
			} while ( code.charAt(e) != ')' );
			end.set(e+1);
			ans = list((LispElement[])elements.toArray(new LispElement[0]));
		} else if ( code.charAt(start) == '"' ) {
			int e = start;
			do {
				if ( ++e >= code.length() ) {
					throw new java.text.ParseException("unterminated string", e-1);
				}
			} while ( code.charAt(e) != '"' );
			end.set(e+1);
			ans = new LispString(code.substring(start+1,e));
		} else {
			int e = start;
			while ( e<code.length() &&
				code.charAt(e)!=' ' &&
				code.charAt(e)!='"' &&
				code.charAt(e)!=')' &&
				code.charAt(e)!='(' ) {
				e++;
			}
			end.set(e);
			ans = new LispSymbol(code.substring(start,e));
		}
		return ans;
	}

	public static LispElement parse(String code) throws java.text.ParseException {
		MutableInteger end = new MutableInteger(-1);
		LispElement element = parse(code,0,end);
		if ( end.get() != code.length() ) {
			throw new java.text.ParseException("stray characters", end.get());
		}
		return element;
	}

	public static String format(String ans) {
		String ans2 = "";
		int count = 0;
		boolean inString = false;
		for (int i=0; i<ans.length(); i++) {
			if ( !inString && ans.charAt(i) == '(' ) {
				if ( count > 40 ) {
					ans2 += "\n  ";
					count = 2;
				}
				ans2 += "(";
				count++;
			} else {
				ans2 += ans.charAt(i);
				count++;
				if ( ans.charAt(i) == '"' ) {
					inString = !inString;
				}
			}
		}
		return "<HTML><PRE>"+ans2+"</PRE></HTML>";
	}

	public static String[] format(String[] ans) {
		String[] ans2 = new String[ans.length];
		for (int j=0; j<ans.length; j++ ) {
			ans2[j] = format(ans[j]);
		}
		return ans2;
	}
}


