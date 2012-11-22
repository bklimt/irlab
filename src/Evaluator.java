
import java.util.HashMap;

public class Evaluator {
	private QAController controller = null;
	private QAController actual = null;

	public Evaluator(QAController controller, QAController actual) {
		this.controller = controller;
		this.actual = actual;
	}

	public void evaluateAnnotator(boolean verbose) throws Exception {
		String[] ids = actual.getEmailRepository().getEmailIDs();
		HashMap falsePositives = new HashMap();
		HashMap falseNegatives = new HashMap();
		HashMap correct = new HashMap();

		for ( int k=0; k<ids.length; k++ ) {
			if ( verbose ) {
				System.out.println("\n"+ids[k]);
			}
			String id = ids[k];
			Annotation[] predictedAnnotations =
				controller.getEmailAnnotator().getAnnotations(id, controller.getEmailRepository());
			Annotation[] actualAnnotations =
				actual.getEmailAnnotator().getAnnotations(id, actual.getEmailRepository());

			for ( int i=0; i<predictedAnnotations.length; i++ ) {
				String type = predictedAnnotations[i].type;
				if ( correct.get(type) == null ) {
					correct.put(type,new Integer(0));
					falsePositives.put(type,new Integer(0));
					falseNegatives.put(type,new Integer(0));
				}
				boolean found = false;
				for ( int j=0; j<actualAnnotations.length && !found; j++ ) {
					if ( actualAnnotations[j].equals(predictedAnnotations[i]) ) {
						found = true;
						// correct
						if ( verbose ) {
							System.out.println("correct: "+actualAnnotations[j].toString());
						}
						correct.put(type,new Integer(((Integer)correct.get(type)).intValue()+1));
					}
				}
				if ( !found ) {
					// false positive
					if ( verbose ) {
						System.out.println("false +: "+predictedAnnotations[i].toString());
					}
					falsePositives.put(type,new Integer(((Integer)falsePositives.get(type)).intValue()+1));
				}
			}
			for ( int j=0; j<actualAnnotations.length; j++ ) {
				String type = actualAnnotations[j].type;
				if ( correct.get(type) == null ) {
					correct.put(type,new Integer(0));
					falsePositives.put(type,new Integer(0));
					falseNegatives.put(type,new Integer(0));
				}
				boolean found = false;
				for ( int i=0; i<predictedAnnotations.length && !found; i++ ) {
					if ( actualAnnotations[j].equals(predictedAnnotations[i]) ) {
						found = true;
					}
				}
				if ( !found ) {
					// false negative
					if ( verbose ) {
						System.out.println("false -: "+actualAnnotations[j].toString()+"\t");
					}
					falseNegatives.put(type,new Integer(((Integer)falseNegatives.get(type)).intValue()+1));
				}
			}
		}
		java.util.Iterator types = correct.keySet().iterator();
		while ( types.hasNext() ) {
			String type = (String)types.next();
			System.out.println("\n"+type);
			System.out.println("correct: " + correct.get(type).toString());
			System.out.println("false +: " + falsePositives.get(type).toString());
			System.out.println("false -: " + falseNegatives.get(type).toString());
		}
	}

	public void evaluateAnswers(QuestionManager manager, boolean verbose) {
		EmailRepository repository = actual.getEmailRepository();
		Question[] questions = manager.getQuestions();
		int credit = 0;
		for ( int i=0; i<questions.length; i++ ) {
			String question = questions[i].getQuestion();
			String expectedID = questions[i].getSource();
			int current = repository.getDate(expectedID);
			AnswerSet.Answer expected = actual.getAnswerExtractor().getAnswers(question,current,verbose).getBestAnswer();
			AnswerSet.Answer given = controller.getAnswerExtractor().getAnswers(question,current,verbose).getBestAnswer();
			if ( given == null ) {
				System.out.println(" QUESTION: "+question);
				System.out.println(" EXPECTED: "+expected.toString());
				System.out.println("    GIVEN: null");
				System.out.println("   CREDIT: 0");
				System.out.println("");
			} else {
				if ( given.getAnswer().equals(expected.getAnswer()) ) {
					System.out.println(" QUESTION: "+question);
					System.out.println(" EXPECTED: "+expected.toString());
					System.out.println("    GIVEN: "+given.toString());
					System.out.println("   CREDIT: 1");
					System.out.println("");
					credit++;
				} else {
					System.out.println(" QUESTION: "+question);
					System.out.println(" EXPECTED: "+expected.toString());
					System.out.println("    GIVEN: "+given.toString());
					System.out.println("   CREDIT: 0");
					System.out.println("");
				}
			}
		}
		System.out.println("SCORE = "+((double)credit/questions.length)+" ("+credit+"/"+questions.length+")");
	}

	public static void main(String[] args) throws Exception {
		QAController controller = new QAController(
			"ManualEmailAnnotator",
			"SeminarDatabase",
			//"NLAnswerExtractor"
			"StatisticalAnswerExtractor"
		);
		QAController actual = new QAController(
			"ManualEmailAnnotator",
			"SeminarDatabase",
			"QuestionManager"
		);
		Evaluator eval = new Evaluator(controller,actual);
		//eval.evaluateAnnotator(true);
		QuestionManager manager = (QuestionManager)actual.getAnswerExtractor();
		eval.evaluateAnswers(manager,false);
	}
}

