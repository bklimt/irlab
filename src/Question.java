
class Question {
	private String source;
	private String question;
	private String correctAnswer;

	public Question(String question, String correctAnswer, String source) {
		this.question = question;
		this.correctAnswer = correctAnswer;
		this.source = source;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return correctAnswer;
	}

	public String getSource() {
		return source;
	}

	public String toString() {
		return "<HTML><PRE>"+question+"\n"+correctAnswer+"\n"+source+"\n</PRE></HTML>";
	}

	public boolean equals(Object other) {
		Question q = (Question)other;
		if ( !question.equals(q.question) ) { return false; }
		if ( !correctAnswer.equals(q.correctAnswer) ) { return false; }
		if ( !source.equals(q.source) ) { return false; }
		return true;
	}
}

