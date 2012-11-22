
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class QAController implements ActionListener {
	private EmailRepository repository = null;
	private SeminarRetriever retriever = null;
	private EmailAnnotator annotator = null;
	private QuestionAnalyzer analyzer = null;
	private AnswerExtractor extractor = null;
	private QuestionManager manager = null;

	private JComboBox annotatorBox = null;
	private JComboBox retrieverBox = null;
	private JComboBox extractorBox = null;
	private JPanel panel = null;

	public QAController(String annotatorType, String retrieverType, String extractorType) throws ClassCastException, FileNotFoundException, IOException, edu.cmu.minorthird.text.mixup.Mixup.ParseException {
		createEmailRepository();
		createEmailAnnotator(annotatorType);
		createSeminarRetriever(retrieverType);
		createQuestionAnalyzer();
		createAnswerExtractor(extractorType);
		createQuestionManager();
	}

	public void actionPerformed(ActionEvent ae) {
		try {
			createEmailRepository();
			createEmailAnnotator((String)annotatorBox.getSelectedItem());
			createSeminarRetriever((String)retrieverBox.getSelectedItem());
			createQuestionAnalyzer();
			createAnswerExtractor((String)extractorBox.getSelectedItem());
			createQuestionManager();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	public JPanel getControlPanel() {
		if ( panel == null ) {
			panel = new JPanel(new GridLayout(1,3));
			annotatorBox = new JComboBox(getEmailAnnotatorTypes());
			retrieverBox = new JComboBox(getSeminarRetrieverTypes());
			extractorBox = new JComboBox(getAnswerExtractorTypes());
			annotatorBox.addActionListener(this);
			retrieverBox.addActionListener(this);
			extractorBox.addActionListener(this);
			panel.add(annotatorBox);
			panel.add(retrieverBox);
			panel.add(extractorBox);
		}
		return panel;
	}

	public EmailRepository getEmailRepository() {
		return repository;
	}

	private void createEmailRepository() throws FileNotFoundException, IOException {
		repository = new EmailRepository("data/split/lectures");
	}

	public SeminarRetriever getSeminarRetriever() {
		return retriever;
	}

	public String[] getSeminarRetrieverTypes() {
		String[] types = {"SeminarDatabase","SeminarExtractor"};
		return types;
	}

	private void createSeminarRetriever(String type) throws ClassCastException, FileNotFoundException, IOException {
		if ( type.equals("SeminarExtractor") ) {
			retriever = new SeminarExtractor(repository, annotator);
			return;
		}
		if ( type.equals("SeminarDatabase") ) {
			retriever = new SeminarDatabase("data/seminars.txt");
			return;
		}
		throw new ClassCastException("No seminar of type "+type);
	}

	public EmailAnnotator getEmailAnnotator() {
		return annotator;
	}

	public String[] getEmailAnnotatorTypes() {
		String[] types = {"ManualEmailAnnotator","MixupAnnotator"};
		return types;
	}

	private void createEmailAnnotator(String type) throws ClassCastException, FileNotFoundException, IOException, edu.cmu.minorthird.text.mixup.Mixup.ParseException {
		if ( type.equals("ManualEmailAnnotator") ) {
			annotator = new ManualEmailAnnotator("data/labels.txt");
			return;
		}
		if ( type.equals("MixupAnnotator") ) {
			annotator = new MixupAnnotator("src/Annotator.mixup");
			return;
		}
		throw new ClassCastException("No annotator of type "+type);
	}

	public QuestionAnalyzer getQuestionAnalyzer() {
		return analyzer;
	}

	private void createQuestionAnalyzer() {
		analyzer = new QuestionAnalyzer();
	}

	public AnswerExtractor getAnswerExtractor() {
		return extractor;
	}

	public String[] getAnswerExtractorTypes() {
		String[] types = {"NLAnswerExtractor","StatisticalAnswerExtractor","QuestionManager"};
		return types;
	}

	private void createAnswerExtractor(String type) throws ClassCastException, FileNotFoundException, IOException {
		if ( type.equals("NLAnswerExtractor") ) {
			extractor = new NLAnswerExtractor(repository,retriever);
			return;
		}
		if ( type.equals("StatisticalAnswerExtractor") ) {
			extractor = new StatisticalAnswerExtractor(repository,annotator,"data/common_words.txt");
			return;
		}
		if ( type.equals("QuestionManager") ) {
			extractor = new QuestionManager("data/questions.txt");
			return;
		}
		throw new ClassCastException("No answer extractor of type "+type);
	}

	public QuestionManager getQuestionManager() {
		return manager;
	}

	private void createQuestionManager() throws FileNotFoundException, IOException {
		manager = new QuestionManager("data/questions.txt");
	}
}

