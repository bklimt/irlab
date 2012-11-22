
import javax.swing.*;

public abstract interface AnswerExtractor {
	public abstract AnswerSet getAnswers(String question, int current);
	public abstract AnswerSet getAnswers(String question, int current, boolean verbose);
	public abstract JPanel getDebugPanel(String question);
}

