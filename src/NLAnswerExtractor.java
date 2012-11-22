
import java.util.HashSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class NLAnswerExtractor implements AnswerExtractor {
	private EmailRepository repository;
	private SeminarRetriever retriever;
	private QuestionAnalyzer analyzer;

	public NLAnswerExtractor(EmailRepository repository, SeminarRetriever retriever) {
		this.repository = repository;
		this.retriever = retriever;
		this.analyzer = new QuestionAnalyzer();
	}

	public NLAnswerExtractor(EmailRepository repository, SeminarRetriever retriever, QuestionAnalyzer analyzer) {
		this.repository = repository;
		this.retriever = retriever;
		this.analyzer = analyzer;
	}

	public AnswerSet getAnswers(Query q, int current, boolean verbose) {
		if ( verbose ) {
			System.out.println("searching using query "+q.toString());
		}
		String[] ids = repository.getEmailIDs();
		AnswerSet answers = new AnswerSet();
		boolean found = false;
		for ( int i=ids.length-1; i>=0; i-- ) {
			String id = ids[i];
			if ( repository.getDate(id) > current ) {
				continue;
			}
			Seminar[] seminars = retriever.getSeminars(id);
			for ( int j=0; j<seminars.length; j++ ) {
				Seminar seminar = seminars[j];
				if ( q.matches(seminar) ) {
					if ( verbose ) {
						System.out.println("matches "+id);
					}
					try {
						found = true;
						String[] a = q.getFields(seminar);
						for ( int k=0; k<a.length; k++ ) {
							answers.add(a[k],id);
						}
					}
					catch (NoSuchFieldException nsfe) {
						nsfe.printStackTrace(System.err);
					}
				}
			}
		}
		if ( !found && q.isExistQuery() ) {
			answers.add("no",null);
		}
		return answers;
	}

	public AnswerSet getAnswers(Query[] q, int current, boolean verbose) {
		AnswerSet ans = new AnswerSet();
		for ( int i=0; i<q.length; i++ ) {
			AnswerSet.Answer[] answers = getAnswers(q[i],current,verbose).getAnswers();
			for ( int j=0; j<answers.length; j++ ) {
				ans.add(answers[j]);
			}
		}
		return ans;
	}

	public AnswerSet getAnswers(String question, int current) {
		return getAnswers(question,current,false);
	}

	public AnswerSet getAnswers(String question, int current, boolean verbose) {
		if ( verbose ) {
			System.out.println("Analyzing question...");
		}
		Query[] queries = analyzer.analyze(question);
		if ( verbose ) {
			for ( int i=0; i<queries.length; i++ ) {
				System.out.println(queries[i]);
			}
			System.out.println("Searching for answers");
		}
		return getAnswers(queries,current,verbose);
	}

	public JPanel getDebugPanel(String question) {
		String parserOutput = analyzer.getParserOutput(question).replaceAll("\\n\\n","\n");
		String[] parses = Lisp.format(analyzer.getParses(question));
		Query[] queries = analyzer.analyze(question);
		JList parseList = new JList(parses);
		JList queryList = new JList(queries);
		parseList.setVisibleRowCount(parses.length);
		queryList.setVisibleRowCount(queries.length);
		JPanel queryPanel = new JPanel(new GridLayout(2,1));
		queryPanel.add(new JScrollPane(parseList));
		queryPanel.add(new JScrollPane(queryList));
		JPanel analysisPanel = new JPanel(new BorderLayout());
		analysisPanel.add(new JLabel("NL Analysis"), BorderLayout.NORTH);
		analysisPanel.add(new JScrollPane(new JTextArea(parserOutput)), BorderLayout.CENTER);
		analysisPanel.add(queryPanel, BorderLayout.SOUTH);
		return analysisPanel;
	}

}

