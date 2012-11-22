
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

class EmailPanel extends JPanel {
	public EmailPanel(String id, QAController controller) throws FileNotFoundException, IOException {
		JTextArea sourceText = new JTextArea(controller.getEmailRepository().getEmailSource(id).replaceAll("\\r",""));
		JList seminarList = new JList(controller.getSeminarRetriever().getSeminars(id));
		Annotation[] annotations = controller.getEmailAnnotator().getAnnotations(id,controller.getEmailRepository());

		for ( int i=0; i<annotations.length; i++ ) {
			Annotation annotation = annotations[i];
			try {
				sourceText.getHighlighter().addHighlight(
					annotation.offset,
					annotation.offset+annotation.length,
					HighlightManager.getPainter(annotation.type)
				);
			}
			catch (BadLocationException ble) {
			}
		}

		setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Email "+id, new JScrollPane(sourceText));
		tabs.add("Seminars", new JScrollPane(seminarList));
		add(tabs, BorderLayout.CENTER);
	}
}

class AnswerPanel extends JPanel {
	QAController controller = null;
	JList idList = null;
	JPanel leftPanel = null;

	private void showEmail(String id) {
		removeAll();
		add(leftPanel, BorderLayout.WEST);
		try {
			add(new EmailPanel(id,controller), BorderLayout.CENTER);
		}
		catch (FileNotFoundException fnfe) {
			add(new JLabel("Unable to open email "+id), BorderLayout.CENTER);
		}
		catch (IOException ioe) {
			add(new JLabel("Unable to open email "+id), BorderLayout.CENTER);
		}
		validate();
	}

	public AnswerPanel(String question, int current, QAController controller) {
		this.controller = controller;

		AnswerSet.Answer[] answers = controller.getAnswerExtractor().getAnswers(question,current).getAnswers();
		//String[] ids = answers.getEmailIDs();
		String[] ids = new String[answers.length];
		for ( int i=0; i<answers.length; i++ ) {
			ids[i] = answers[i].getID();
		}

		JList answerList = new JList(answers);
		idList = new JList(ids);
		answerList.setVisibleRowCount(answers.length+1);
		idList.setVisibleRowCount(ids.length+1);
		JPanel answerListPanel = new JPanel(new GridLayout(2,1));
		answerListPanel.add(new JScrollPane(answerList));
		answerListPanel.add(new JScrollPane(idList));
		JPanel answerPanel = new JPanel(new BorderLayout());
		answerPanel.add(new JLabel("Answers:"), BorderLayout.NORTH);
		answerPanel.add(answerListPanel, BorderLayout.CENTER);

		leftPanel = new JPanel(new BorderLayout());//GridLayout(2,1));
		leftPanel.add(answerPanel, BorderLayout.NORTH);
		leftPanel.add(controller.getAnswerExtractor().getDebugPanel(question), BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(leftPanel, BorderLayout.CENTER);

		idList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = idList.locationToIndex(e.getPoint());
					String id = (String)idList.getModel().getElementAt(index);
					showEmail(id);
				}
			}
		});
	}
}

public class QADebugger {
	private JFrame frame = null;
	private QAController controller = null;
	private JList questionList = null;
	private JPanel questionPanel = null;
	private JTextField questionText = null;

	public void answerQuestion(String question, int current) {
		frame.getContentPane().removeAll();
		frame.getContentPane().add(questionPanel, BorderLayout.NORTH);
		frame.getContentPane().add(new AnswerPanel(question,current,controller), BorderLayout.CENTER);
		frame.getContentPane().add(controller.getControlPanel(), BorderLayout.SOUTH);
		frame.validate();
	}

	public void init() throws FileNotFoundException, IOException, ClassCastException, edu.cmu.minorthird.text.mixup.Mixup.ParseException {
		controller = new QAController("ManualEmailAnnotator","SeminarDatabase","NLAnswerExtractor");
		//ArrayList questions = new ArrayList();

		/*
		BufferedReader reader = new BufferedReader(new FileReader("data/questions.txt"));
		String line = reader.readLine();
		while ( line != null ) {
			String[] parts = line.split("\t");
			String id = parts[0];
			String question = parts[1];
			String answer = parts[2];
			String query = "";
			if ( parts.length == 4 ) {
				query = parts[3];
			}
			Question q = new Question(question,answer,id);
			questions.add(q);
			line = reader.readLine();
		}
		initUI((Question[])questions.toArray(new Question[0]));
		*/

		//Question[] questions = controller.getQuestionManager().getQuestions();
		initUI();
	}

	public void initUI() {
		frame = new JFrame("questions");

		questionList = new JList(controller.getQuestionManager().getQuestions());
		questionList.setVisibleRowCount(2);
		questionList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = questionList.locationToIndex(e.getPoint());
					Question q = (Question)questionList.getModel().getElementAt(index);
					answerQuestion(q.getQuestion(),controller.getEmailRepository().getDate(q.getSource()));
				}
			}
		});

		questionText = new JTextField("");
		JButton askButton = new JButton("Ask");
		JPanel questionInputPanel = new JPanel(new BorderLayout());
		questionInputPanel.add(new JLabel("Question: "), BorderLayout.WEST);
		questionInputPanel.add(askButton, BorderLayout.EAST);
		questionInputPanel.add(questionText, BorderLayout.CENTER);
		askButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				answerQuestion(questionText.getText(),controller.getEmailRepository().getCurrentDate());
			}
		});

		questionPanel = new JPanel(new BorderLayout());
		//questionPanel.add(new JScrollPane(questionList), BorderLayout.CENTER);
		questionPanel.add(questionInputPanel, BorderLayout.NORTH);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(questionPanel, BorderLayout.CENTER);
		frame.getContentPane().add(controller.getControlPanel(), BorderLayout.SOUTH);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		frame.setBounds(50,50,600,600);
		frame.show();
	}

	public static void main(String[] args) throws Exception {
		QADebugger app = new QADebugger();
		app.init();
	}
}

