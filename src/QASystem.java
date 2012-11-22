
public class QASystem {
	public static void main(String[] args) throws Exception {
		/*
		EmailRepository repository = new EmailRepository("data/split/lectures");
		EmailAnnotator annotator = new ManualEmailAnnotator("data/labels.txt");
		SeminarRetriever retriever = new SeminarDatabase("data/seminars.txt");
		AnswerExtractor extractor = new StatisticalAnswerExtractor(repository,annotator,"data/common_words.txt");
		*/
		QAController controller = new QAController(
			"ManualEmailAnnotator",
			"SeminarDatabase",
			"NLAnswerExtractor"
		);

		String question = args[0];
		int current = controller.getEmailRepository().getDate(args[1]);

		System.out.println("QAController loaded...");

		AnswerSet.Answer[] answers = controller.getAnswerExtractor().getAnswers(question,current,true).getAnswers();
		if ( answers != null ) {
			for ( int j=0; j<answers.length; j++ ) {
				System.out.println(answers[j]);
			}
		}
	}
}

