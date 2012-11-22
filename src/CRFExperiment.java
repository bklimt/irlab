
import edu.cmu.minorthird.text.*;
import edu.cmu.minorthird.text.learn.*;
import edu.cmu.minorthird.text.learn.experiments.*;
import edu.cmu.minorthird.classify.*;
import edu.cmu.minorthird.classify.experiments.*;
import edu.cmu.minorthird.classify.sequential.*;
import edu.cmu.minorthird.ui.*;
import edu.cmu.minorthird.util.gui.*;

public class CRFExperiment {
	public static void main(String[] args) throws Exception {
		Evaluation e = Evaluation.load(new java.io.File(args[0]));
		ViewerFrame f = new ViewerFrame("Evaluation",e.toGUI());

		/*
		BasicTextBase base = new BasicTextBase();

		EmailRepository repository = new EmailRepository("data/split/lectures");
		ManualEmailAnnotator annotator = new ManualEmailAnnotator("data/labels.txt");
		String[] ids = repository.getEmailIDs();
		for ( int i=0; i<ids.length; i++ ) {
			String id = ids[i];
			String text = repository.getEmailSource(id);
			base.loadDocument(id,text);
			Annotation[] annotations = annotator.getAnnotations(id,repository);
			
		}

		TextLabels labels = new BasicTextLabels(base);
		Splitter splitter = new CrossValSplitter(20);
		SequenceClassifierLearner learner = new CRFLearner();
		String inputLabel = args[0];
		SequenceAnnotatorExpt experiment = new SequenceAnnotatorExpt(labels, splitter, learner, inputLabel);
		Evaluation evaluation = experiment.evaluation();
		System.out.println(evaluation);
		*/
	}
}

