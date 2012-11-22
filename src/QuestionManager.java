
import java.io.*;
import java.util.ArrayList;

public class QuestionManager implements AnswerExtractor {
	private String filename = null;
	private ArrayList questions = null;

	public QuestionManager(String filename) throws FileNotFoundException, IOException {
		this.filename = filename;
		read();
	}

	private void read() throws FileNotFoundException, IOException {
		questions = new ArrayList();
		BufferedReader reader = new BufferedReader(new FileReader("data/questions.txt"));
		String line = reader.readLine();
		while ( line != null ) {
			String[] parts = line.split("\t");
			String id = parts[0];
			String question = parts[1];
			String answer = parts[2];
			Question q = new Question(question,answer,id);
			questions.add(q);
			line = reader.readLine();
		}
	}

	public void write() throws FileNotFoundException, IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("data/questions.txt"));
		for (int i=0; i<questions.size(); i++) {
			Question q = (Question)questions.get(i);
			writer.write(q.getSource()+"\t"+q.getQuestion()+"\t"+q.getAnswer()+"\t\n");
		}
		writer.flush();
		writer.close();
	}

	public Question[] getQuestions() {
		return (Question[])questions.toArray(new Question[0]);
	}

	public Question[] getQuestions(String source) {
		ArrayList ans = new ArrayList();
		for ( int i=0; i<questions.size(); i++ ) {
			if ( ((Question)questions.get(i)).getSource().equals(source) ) {
				ans.add(questions.get(i));
			}
		}
		return (Question[])ans.toArray(new Question[0]);
	}

	public void add(Question q) {
		questions.add(q);
	}

	public void remove(Question q) {
		questions.remove(questions.indexOf(q));
	}

	public void replace(Question oldq, Question newq) {
		int index = questions.indexOf(oldq);
		if ( index != -1 ) {
			questions.set(index,newq);
		}
	}

	public javax.swing.JPanel getDebugPanel(String question) {
		return new javax.swing.JPanel();
	}

	public AnswerSet getAnswers(String question, int current) {
		return getAnswers(question,current,false);
	}

	public int getDate(String id) {
		String[] parts = id.split("\\.");
		return (new Integer(parts[0])).intValue();
	}

	public AnswerSet getAnswers(String question, int current, boolean verbose) {
		AnswerSet ans = new AnswerSet();
		for ( int i=0; i<questions.size(); i++ ) {
			Question q = (Question)questions.get(i);
			if ( q.getQuestion().equals(question) ) {
				if ( getDate(q.getSource()) == current ) {
					ans.add(q.getAnswer(),q.getSource());
				}
			}
		}
		return ans;
	}
}

