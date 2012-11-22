
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class QuestionAnalyzer {
	protected QuestionAnalyzer() {
	}

	public static String getParserOutput(String question) {
		String output = "";
		BufferedReader reader = getParserOutputReader(question);
		try {
			String line = reader.readLine();
			while ( line != null ) {
				output += line;
				output += "\n";
				line = reader.readLine();
			}
			return output;
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.err);
			return "";
		}
	}

	public static BufferedReader getParserOutputReader(String question) {
		String tempFile = "/tmp/lisp";
		if ( question.charAt(question.length()-1) != '?' ) {
			question += '?';
		}
		question = question.replaceAll("\\?"," ?");
		question = question.replaceAll(":"," COLON ");
		question = question.replaceAll("' ","'s ");
		question = question.replaceAll("'"," ");
		question = question.replaceAll("\""," ");
		question = question.replaceAll(","," ");

		question = question.replaceAll("1st", "1 st");
		question = question.replaceAll("2nd", "2 nd");
		question = question.replaceAll("2rd", "3 rd");
		question = question.replaceAll("1th", "1 th");
		question = question.replaceAll("2th", "2 th");
		question = question.replaceAll("3th", "3 th");
		question = question.replaceAll("4th", "4 th");
		question = question.replaceAll("5th", "5 th");
		question = question.replaceAll("6th", "6 th");
		question = question.replaceAll("7th", "7 th");
		question = question.replaceAll("8th", "8 th");
		question = question.replaceAll("9th", "9 th");
		question = question.replaceAll("0th", "0 th");

		question = question.toUpperCase();

		Runtime runtime = Runtime.getRuntime();
		String[] command = new String[3];
		command[0] = "bin/ParseQuestion";
		command[1] = question;
		command[2] = tempFile;
		try {
			Process process = runtime.exec(command);
			process.waitFor();

			ArrayList queries = new ArrayList();
			if ( System.getProperty("os.name").equals("Windows XP") ) {
				tempFile = "c:/cygwin" + tempFile;
			}

			return new BufferedReader(new FileReader(tempFile));
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.err);
			return null;
		}
		catch (InterruptedException ie) {
			ie.printStackTrace(System.err);
			return null;
		}
	}

	public static String[] getParses(String question) {
		BufferedReader reader = getParserOutputReader(question);
		if ( reader == null ) {
			return new String[0];
		}
		try {
			ArrayList parses = new ArrayList();
			String line = reader.readLine();
			while ( line != null ) {
				if ( line.matches("^;\\*\\*\\*\\* ambiguity [0-9][0-9]* \\*\\*\\*$") ) {
					String buffer = "";
					line = reader.readLine();
					while ( line != null && !line.matches("^;\\*\\*\\*\\* ambiguity [0-9][0-9]* \\*\\*\\*$") && !line.matches("^>$") ) {
						buffer += line;
						line = reader.readLine();
					}
					buffer = buffer.replaceAll("  *", " ");
					//System.out.println(buffer);
					//Query q = createQuery(buffer);
					//if ( q != null ) {
					//	queries.add(q);
					//}
					parses.add(buffer);
				} else {
					line = reader.readLine();
				}
			}
			return (String[])parses.toArray(new String[0]);
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.err);
			return new String[0];
		}
	}

	public static Query[] analyze(String question) {
		ArrayList queries = new ArrayList();
		String[] parses = getParses(question);
		for (int i=0; i<parses.length; i++ ) {
			Query q = createQuery(parses[i]);
			if ( q != null ) {
				queries.add(q);
			}
		}
		return (Query[])queries.toArray(new Query[0]);
	}

	protected static Query.Filter createFilter(Lisp.LispList code) throws NoSuchMethodException {
		//System.out.println(code);
		Lisp.LispElement filterTypeNode = code.car();
		if ( code.car().listp() ) {
			// implicit AND
			ArrayList filters = new ArrayList();
			filters.add(createFilter((Lisp.LispList)code.car()));
			Lisp.LispElement subnode = code.cdr();
			while (!subnode.nullp()) {
				if (!subnode.listp()) {
					throw new ClassCastException("*AND* can only take arguments of type list");
				}
				filters.add(createFilter((Lisp.LispList)((Lisp.LispList)subnode).car()));
				subnode = ((Lisp.LispList)subnode).cdr();
			}
			return new Query.ANDFilter((Query.Filter[])filters.toArray(new Query.Filter[0]));
		}
		if ( code.car().equals(new Lisp.LispSymbol("*OR*")) ) {
			ArrayList filters = new ArrayList();
			Lisp.LispElement subnode = code.cdr();
			while (!subnode.nullp()) {
				if (!subnode.listp()) {
					throw new ClassCastException("*OR* can only take arguments of type list");
				}
				filters.add(createFilter((Lisp.LispList)((Lisp.LispList)subnode).car()));
				subnode = ((Lisp.LispList)subnode).cdr();
			}
			return new Query.ORFilter((Query.Filter[])filters.toArray(new Query.Filter[0]));
		}
		if ( code.car().equals(new Lisp.LispSymbol("NAME")) ) {
			return new Query.NAMEFilter(((Lisp.LispString)((Lisp.LispList)code.cdr()).car()).getValue());
		}
		if ( code.car().equals(new Lisp.LispSymbol("SERIES")) ) {
			return new Query.SERIESFilter(((Lisp.LispString)((Lisp.LispList)code.cdr()).car()).getValue());
		}
		if ( code.car().equals(new Lisp.LispSymbol("TITLE")) ) {
			return new Query.TITLEFilter(((Lisp.LispString)((Lisp.LispList)code.cdr()).car()).getValue());
		}
		if ( code.car().equals(new Lisp.LispSymbol("LOCATION")) ) {
			return new Query.LOCATIONFilter(((Lisp.LispString)((Lisp.LispList)code.cdr()).car()).getValue());
		}
		if ( code.car().equals(new Lisp.LispSymbol("AFFILIATION")) ) {
			return new Query.AFFILIATIONFilter(((Lisp.LispString)((Lisp.LispList)code.cdr()).car()).getValue());
		}
		if ( code.car().equals(new Lisp.LispSymbol("DATE")) ) {
			DateTime datetime = new DateTime();
			if ( !code.cdr().listp() || !((Lisp.LispList)code.cdr()).car().listp() ) {
				throw new ClassCastException("DATE must take an argument of type list");
			}
			Lisp.LispElement subnode = ((Lisp.LispList)code.cdr()).car();
			while ( !subnode.nullp() ) {
				if ( !subnode.listp() ) {
					throw new ClassCastException("DATE/ can only take arguments of type list");
				}
				if ( !((Lisp.LispList)subnode).car().listp() ) {
					throw new ClassCastException("DATE/ can only take arguments of type list");
				}
				if ( !((Lisp.LispList)((Lisp.LispList)subnode).car()).car().symbolp() ) {
					throw new NoSuchMethodException("No filter for DATE/" + ((Lisp.LispList)subnode).car().toString());
				}
				Lisp.LispSymbol type = (Lisp.LispSymbol)((Lisp.LispList)((Lisp.LispList)subnode).car()).car();
				if ( !((Lisp.LispList)((Lisp.LispList)subnode).car()).cdr().listp() ) {
					throw new ClassCastException("DATE/"+type.symbolName() + " cannot take an argument of the form " + ((Lisp.LispList)subnode).toString());
				}
				Lisp.LispElement value = ((Lisp.LispList)((Lisp.LispList)((Lisp.LispList)subnode).car()).cdr()).car();
				if ( type.symbolName().equals("MONTH") ) {
					datetime.month = DateTimeParser.resolveMonth(value.toString());
				} else if ( type.symbolName().equals("WEEKDAY") ) {
					datetime.weekday = DateTimeParser.resolveWeekday(value.toString());
				} else if ( type.symbolName().equals("DAY") ) {
					datetime.day = new Integer(value.toString());
				} else if ( type.symbolName().equals("YEAR") ) {
					datetime.year = new Integer(value.toString());
				} else {
					throw new NoSuchMethodException("No filter for DATE/" + subnode.toString());
				}
				subnode = ((Lisp.LispList)subnode).cdr();
			}
			return new Query.DATEFilter(datetime);
		}
		if ( code.car().equals(new Lisp.LispSymbol("TIME")) ) {
			DateTime datetime = new DateTime();
			if ( !code.cdr().listp() ) { //|| !((Lisp.LispList)code.cdr()).car().listp() ) {
				throw new ClassCastException("TIME must take an argument of type list");
			}
			Lisp.LispElement subnode = ((Lisp.LispList)code.cdr()).car();
			while ( !subnode.nullp() ) {
				if ( !subnode.listp() ) {
					throw new ClassCastException("TIME/ can only take arguments of type list");
				}
				if ( !((Lisp.LispList)subnode).car().listp() ) {
					throw new ClassCastException("TIME/ can only take arguments of type list");
				}
				if ( !((Lisp.LispList)((Lisp.LispList)subnode).car()).car().symbolp() ) {
					System.out.println(subnode);
					throw new NoSuchMethodException("No filter for TIME/" + ((Lisp.LispList)subnode).car().toString());
				}
				Lisp.LispSymbol type = (Lisp.LispSymbol)((Lisp.LispList)((Lisp.LispList)subnode).car()).car();
				if ( !((Lisp.LispList)((Lisp.LispList)subnode).car()).cdr().listp() ) {
					throw new ClassCastException("TIME/"+type.symbolName() + " cannot take an argument of the form " + ((Lisp.LispList)subnode).toString());
				}
				Lisp.LispElement value = ((Lisp.LispList)((Lisp.LispList)((Lisp.LispList)subnode).car()).cdr()).car();
				if ( type.symbolName().equals("HOUR") ) {
					datetime.hour = new Integer(value.toString());
				} else if ( type.symbolName().equals("MINUTE") ) {
					datetime.minute = new Integer(value.toString());
				} else if ( type.symbolName().equals("MERIDIAN") ) {
					datetime.pm = new Boolean(value.toString().equals("PM"));
				} else {
					throw new NoSuchMethodException("No filter for TIME/" + subnode.toString());
				}
				subnode = ((Lisp.LispList)subnode).cdr();
			}
			return new Query.TIMEFilter(datetime);
		}
		throw new NoSuchMethodException("No filter for " + code.toString());
	}

	protected static Query createQuery(String lispCode) {
		//System.out.println(lispCode);
		try {
			Lisp.LispElement queryCode = Lisp.parse(lispCode);
			if ( Lisp.length(queryCode) != 2 ) {
				throw new ClassCastException("bad query object");
			}
			Lisp.LispElement fieldTuple = Lisp.assoc(new Lisp.LispSymbol("FIELD"), queryCode);
			Lisp.LispElement filterTuple = Lisp.assoc(new Lisp.LispSymbol("FILTER"), queryCode);
			if ( fieldTuple.nullp() || filterTuple.nullp() ) {
				throw new NoSuchFieldException("field or filter");
			}
			int fieldCount = Lisp.length(fieldTuple)-1;
			String[] fieldNames = new String[fieldCount];
			for (int i=0; i<fieldCount; i++ ) {
				Lisp.LispElement fieldElement = Lisp.nth(i+1, fieldTuple);
				if ( !fieldElement.symbolp() ) {
					throw new ClassCastException("field name "+fieldElement.toString()+" is not a symbol");
				}
				fieldNames[i] = ((Lisp.LispSymbol)fieldElement).symbolName();
			}
			if ( !((Lisp.LispList)filterTuple).cdr().listp() ) {
				throw new ClassCastException("filter argument is not a list");
			}
			Query.Filter filter = createFilter((Lisp.LispList)((Lisp.LispList)((Lisp.LispList)filterTuple).cdr()).car());
			return new Query(fieldNames,filter);
		}
		catch (java.text.ParseException pe) {
			System.err.println(pe);
			pe.printStackTrace(System.err);
			return null;
		}
		catch (ClassCastException cce) {
			System.err.println(cce);
			cce.printStackTrace(System.err);
			return null;
		}
		catch (NoSuchFieldException nsfe) {
			System.err.println(nsfe);
			nsfe.printStackTrace(System.err);
			return null;
		}
		catch (NoSuchMethodException nsme) {
			System.err.println(nsme);
			nsme.printStackTrace(System.err);
			return null;
		}
	}

	public static void main(String[] args) {
		String question = args[0];
		System.out.println(question);
		Query[] q = analyze(question);
		for (int i=0; i<q.length; i++) {
			System.out.println(q[i]);
		}
	}
}

