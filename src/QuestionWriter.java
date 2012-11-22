
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;

public class QuestionWriter extends JFrame implements ListSelectionListener, ActionListener {
	private EmailRepository repository = null;
	private EmailAnnotator annotator = null;
	private SeminarDatabase database = null;
	private QuestionManager questions = null;
	private JList idList = null;
	private JTextArea sourceText = null;
	private JList seminarList = null;
	private JList questionList = null;
	private JTextField questionText = null;
	private JTextField answerText = null;
	//private JTextField queryText = null;

	public QuestionWriter() {
		super("Question Writer");
	}

	public void error(String message, Exception e) {
		System.err.println(message);
		System.err.println(e);
	}

	public void showEmail(String id) throws FileNotFoundException, IOException {
		sourceText.setText(repository.getEmailSource(id).replaceAll("\\r",""));
		Annotation[] annotations = annotator.getAnnotations(id,repository);
		Seminar[] seminars = database.getSeminars(id);
		((DefaultListModel)(seminarList.getModel())).removeAllElements();
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
		for ( int i=0; i<seminars.length; i++ ) {
			Seminar seminar = seminars[i];
			((DefaultListModel)(seminarList.getModel())).addElement(seminar);
		}
		((DefaultListModel)(questionList.getModel())).removeAllElements();
		Question[] questionArray = questions.getQuestions(id);
		//if ( questionVector != null ) {
			for ( int i=0; i<questionArray.length; i++ ) {
				((DefaultListModel)questionList.getModel()).addElement(questionArray[i]);
			}
		//}
		sourceText.setCaretPosition(0);
		getContentPane().validate();
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( idList == e.getSource() ) {
			int index = idList.getSelectedIndex();
			if ( index != -1 ) {
				String id = idList.getModel().getElementAt(index).toString();
				try {
					showEmail(id);
				}
				catch (FileNotFoundException fnfe) {
					error("unable to open file "+id, fnfe);
				}
				catch (IOException ioe) {
					error("unable to open file "+id, ioe);
				}
			}
		} else if ( questionList == e.getSource() ) {
			int index = questionList.getSelectedIndex();
			if ( index != -1 ) {
				Question q = (Question)questionList.getModel().getElementAt(index);
				questionText.setText(q.getQuestion());
				answerText.setText(q.getAnswer());
				//queryText.setText(q.query);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals("Add" ) ) {
			if ( idList.getSelectedIndex() != -1 ) {
				String id = idList.getModel().getElementAt(idList.getSelectedIndex()).toString();
				Question q = new Question(questionText.getText(), answerText.getText(), id);
				((DefaultListModel)questionList.getModel()).addElement(q);
				questions.add(q);
				questionText.setText("");
				answerText.setText("");
				//queryText.setText("");
				questionList.validate();
			}
		} else if ( e.getActionCommand().equals("Modify" ) ) {
			int index = questionList.getSelectedIndex();
			if ( index != -1 ) {
				if ( idList.getSelectedIndex() != -1 ) {
					String id = idList.getModel().getElementAt(idList.getSelectedIndex()).toString();
					Question oldq = (Question)questionList.getModel().getElementAt(index);
					Question q = new Question(questionText.getText(), answerText.getText(), id);
					((DefaultListModel)questionList.getModel()).setElementAt(q, index);
					questions.replace(oldq,q);
				}
			}
			questionList.validate();
		} else if ( e.getActionCommand().equals("Remove" ) ) {
			int index = questionList.getSelectedIndex();
			if ( index != -1 ) {
				if ( idList.getSelectedIndex() != -1 ) {
					String id = idList.getModel().getElementAt(idList.getSelectedIndex()).toString();
					Question q = (Question)questionList.getModel().getElementAt(index);
					((DefaultListModel)questionList.getModel()).remove(index);
					questions.remove(q);
				}
			}
			questionList.validate();
		} else if ( e.getActionCommand().equals("Save") ) {
			try {
				questions.write();
				System.out.println("questions written to data/questions.txt");
			}
			catch (IOException ioe) {
				error("unable to save file", ioe);
			}
		} else if ( e.getActionCommand().equals("Next") ) {
			int index = idList.getSelectedIndex();
			if ( index != -1 ) {
				idList.setSelectedIndex(index+1);
			}
		}
	}

	public void initUI() {
		String[] ids = repository.getEmailIDs();
		idList = new JList(ids);
		idList.addListSelectionListener(this);

		sourceText = new JTextArea();
		seminarList = new JList(new DefaultListModel());
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Email", new JScrollPane(sourceText));
		tabs.add("Seminars", new JScrollPane(seminarList));

		JPanel labelPanel = new JPanel(new GridLayout(2,1));
		labelPanel.add(new Label("Question:  "));
		labelPanel.add(new Label("Answer:  "));
		//labelPanel.add(new Label("Query:  "));
		questionText = new JTextField();
		answerText = new JTextField();
		//queryText = new JTextField();
		JPanel textFieldPanel = new JPanel(new GridLayout(2,1));
		textFieldPanel.add(questionText);
		textFieldPanel.add(answerText);
		//textFieldPanel.add(queryText);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.add(labelPanel, BorderLayout.WEST);
		textPanel.add(textFieldPanel, BorderLayout.CENTER);

		JButton addButton = new JButton("Add");
		JButton modifyButton = new JButton("Modify");
		JButton removeButton = new JButton("Remove");
		addButton.addActionListener(this);
		modifyButton.addActionListener(this);
		removeButton.addActionListener(this);
		JPanel questionButtonPanel = new JPanel(new GridLayout(3,1));
		questionButtonPanel.add(addButton);
		questionButtonPanel.add(modifyButton);
		questionButtonPanel.add(removeButton);
		JPanel questionInputPanel = new JPanel(new BorderLayout());
		questionInputPanel.add(textPanel, BorderLayout.CENTER);
		questionInputPanel.add(questionButtonPanel, BorderLayout.EAST);
		JPanel questionPanel = new JPanel(new BorderLayout());
		questionList = new JList(new DefaultListModel());
		questionList.addListSelectionListener(this);
		questionPanel.add(questionList, BorderLayout.CENTER);
		questionPanel.add(questionInputPanel, BorderLayout.SOUTH);

		JPanel emailPanel = new JPanel(new BorderLayout());
		emailPanel.add(tabs, BorderLayout.CENTER);
		emailPanel.add(questionPanel, BorderLayout.SOUTH);

		JButton saveButton = new JButton("Save");
		JButton nextButton = new JButton("Next");
		saveButton.addActionListener(this);
		nextButton.addActionListener(this);
		JPanel controlPanel = new JPanel(new GridLayout(1,2));
		controlPanel.add(saveButton);
		controlPanel.add(nextButton);
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(new JPanel(), BorderLayout.CENTER);
		bottomPanel.add(controlPanel, BorderLayout.EAST);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(idList), BorderLayout.WEST);
		getContentPane().add(emailPanel, BorderLayout.CENTER);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		setBounds(100,100,600,600);
		setVisible(true);
		idList.setSelectedIndex(0);
	}

	public void init() throws FileNotFoundException, IOException {
		repository = new EmailRepository("data/split/lectures");
		annotator = new ManualEmailAnnotator("data/labels.txt");
		database = new SeminarDatabase("data/seminars.txt");
		questions = new QuestionManager("data/questions.txt");

		initUI();
	}

	public static void main(String[] args) throws Exception {
		QuestionWriter frame = new QuestionWriter();
		frame.init();
	}
}

