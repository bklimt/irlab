
import java.util.Vector;

public class AnswerSet {
	public class Answer {
		String answer;
		String id;

		public Answer(String answer, String id) {
			this.answer = answer;
			this.id = id;
		}

		public String getAnswer() {
			return answer;
		}

		public String getID() {
			return id;
		}

		public String toString() {
			return answer + " (" + id + ")";
		}
	}

	private Vector answers;

	public AnswerSet() {
		answers = new Vector();
	}

	public void add(String answer, String id) {
		answer = answer.toUpperCase();
		answers.add(new Answer(answer, id));
	}

	public void add(Answer answer) {
		answers.add(answer);
	}

	public Answer[] getAnswers() {
		return (Answer[])answers.toArray(new Answer[0]);
	}

	public Answer getBestAnswer() {
		if ( answers.size() > 0 ) {
			return (Answer)answers.get(0);
		} else {
			return null;
		}
	}
}

