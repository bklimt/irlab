
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class StatisticalAnswerExtractor implements AnswerExtractor {
	Search searcher;
	EmailRepository repository;
	EmailAnnotator annotator;

	public StatisticalAnswerExtractor(EmailRepository repository, EmailAnnotator annotator, String stopwords) throws java.io.FileNotFoundException, java.io.IOException {
		this.repository = repository;
		this.annotator = annotator;
		searcher = new Search(repository);
		if ( stopwords != null ) {
			searcher.loadStopwords(stopwords);
		}
		searcher.loadDocuments();
	}

	int getDistance(Search.QueryResult result, int position) {
		int sortest = 0;
		int best = 100000000;
		for ( int i=0; i<result.annotations.length; i++ ) {
			if ( best>Math.abs(position-result.annotations[i].offset) ) {
				best = Math.abs(position-result.annotations[i].offset);
			}
		}
		return best;
	}

	String getClosestAnnotation(Search.QueryResult result, String type) throws java.io.FileNotFoundException, java.io.IOException {
		Annotation[] annotations = annotator.getAnnotations(result.documentID,repository);
		int best_length = -1;
		String best = "";
		for ( int i=0; i<annotations.length; i++ ) {
			if ( annotations[i].type.toUpperCase().equals(type) ) {
				if ( best_length==-1 || best_length>getDistance(result,annotations[i].offset) ) {
					best_length = getDistance(result,annotations[i].offset);
					best = annotations[i].text;
				}
			}
		}
		return best;
	}

	String getField(String question) {
		question = question.toUpperCase();
		if ( question.matches("^WHEN .*$") ) { return "DATETIME"; }
		if ( question.matches("^WHERE .*$") ) { return "LOCATION"; }
		if ( question.matches("^IS .*$") ) { return "EXIST"; }
		if ( question.matches("^ARE .*$") ) { return "EXIST"; }
		if ( question.matches("^WHO .*$") ) { return "NAME"; }
		if ( question.matches("^WHAT SERIES .*$") ) { return "SERIES"; }
		if ( question.matches("^WHAT TIME .*$") ) { return "TIME"; }
		if ( question.matches("^WHAT DAY .*$") ) { return "DATE"; }
		if ( question.matches("^WHAT DATE.*$") ) { return "DATE"; }
		if ( question.matches("^IN WHICH ROOM .*$") ) { return "LOCATION"; }
		if ( question.matches("^WHAT IS THE DATE .*$") ) { return "DATE"; }
		if ( question.matches("^WHAT IS THE TITLE .*$") ) { return "TITLE"; }
		if ( question.matches("^WHAT IS THE AFFILIATION .*$") ) { return "AFFILIATION"; }
		if ( question.matches("^WHAT IS THE TOPIC .*$") ) { return "TITLE"; }
		if ( question.matches("^WHAT IS .*'S AFFILIATION.*$") ) { return "AFFILIATION"; }
		return null;
	}

	public AnswerSet getAnswers(String question, int current) {
		return getAnswers(question, current, false);
	}

	public AnswerSet getAnswers(String question, int current, boolean verbose) {
		AnswerSet ans = new AnswerSet();
		String field = getField(question);
		if ( field == null ) {
			return ans;
		}
		try {
			Search.QueryResult[] results = searcher.doQuery(question);
			if ( field.equals("EXIST") ) {
				if ( results.length > 0) {
					ans.add("YES",null);
				} else {
					ans.add("NO",null);
				}
				return ans;
			}
			for ( int i=0; i<results.length && i<10; i++ ) {
				if ( repository.getDate(results[i].documentID) <= current ) {
					ans.add(getClosestAnnotation(results[i],field),results[i].documentID);
				}
			}
		}
		catch (java.io.FileNotFoundException fnfe) {
			fnfe.printStackTrace(System.err);
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.err);
		}
		return ans;
	}

	public JPanel getDebugPanel(String question) {
		String text = "";
		try {
			Search.QueryResult[] results = searcher.doQuery(question);
			for ( int i=0; i<results.length; i++ ) {
				text += results[i];
			}
		}
		catch (java.io.FileNotFoundException fnfe) {
			fnfe.printStackTrace(System.err);
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.err);
		}
		JTextArea textArea = new JTextArea(text);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		return panel;
	}
}

