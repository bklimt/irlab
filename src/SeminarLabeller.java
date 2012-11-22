
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.Vector;
import java.util.HashMap;

public class SeminarLabeller implements ListSelectionListener, ActionListener {
	private EmailRepository repository = null;
	private EmailAnnotator annotator = null;
	private SeminarDatabase database = null;
	private JList idList = null;
	private JList annotationList = null;
	private JList seminarList = null;
	private JTextArea sourceText = null;
	private JFrame frame = null;

	private void error(String msg, Exception e) {
		System.err.println(msg);
		System.err.println(e);
	}

	private void showEmail(String id) throws java.io.FileNotFoundException, java.io.IOException {
		sourceText.setText(repository.getEmailSource(id).replaceAll("\\r",""));
		Annotation[] annotations = annotator.getAnnotations(id,repository);
		Seminar[] seminars = database.getSeminars(id);
		((DefaultListModel)(annotationList.getModel())).removeAllElements();
		((DefaultListModel)(seminarList.getModel())).removeAllElements();
		java.util.Arrays.sort(annotations,new java.util.Comparator() {
			public int compare(Object o1, Object o2) {
				Annotation a1 = (Annotation)o1;
				Annotation a2 = (Annotation)o2;
				return a1.offset - a2.offset;
			}
			public boolean equal(Object o1, Object o2) {
				return o1 == o2;
			}
		});
		for ( int i=0; i<annotations.length; i++ ) {
			Annotation annotation = annotations[i];
			try {
				sourceText.getHighlighter().addHighlight(
					annotation.offset,
					annotation.offset+annotation.length,
					HighlightManager.getPainter(annotation.type)
				);
				((DefaultListModel)(annotationList.getModel())).addElement(annotation);
			}
			catch (BadLocationException ble) {
			}
		}
		for ( int i=0; i<seminars.length; i++ ) {
			Seminar seminar = seminars[i];
			((DefaultListModel)(seminarList.getModel())).addElement(seminar);
		}
		sourceText.setCaretPosition(0);
		frame.getContentPane().validate();
	}

	public void valueChanged(ListSelectionEvent e) {
		if ( idList == e.getSource() ) {
			int index = idList.getSelectedIndex();
			if ( index != -1 ) {
				String id = idList.getModel().getElementAt(index).toString();
				try {
					showEmail(id);
				}
				catch (java.io.FileNotFoundException fnfe) {
					error("unable to open file "+id, fnfe);
				}
				catch (java.io.IOException ioe) {
					error("unable to open file "+id, ioe);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals("Add") ) {
			int index = idList.getSelectedIndex();
			if ( index != -1 ) {
				String id = idList.getModel().getElementAt(index).toString();
				DefaultListModel model = (DefaultListModel)(annotationList.getModel());
				Object[] objects = model.toArray();
				Annotation[] annotations = new Annotation[objects.length];
				for ( int i=0; i<objects.length; i++ ) {
					annotations[i] = (Annotation)objects[i];
				}
				new NewSeminarFrame(id,annotations,database,seminarList);
			}
		} else if ( e.getActionCommand().equals("Remove") ) {
			int idIndex = idList.getSelectedIndex();
			if ( idIndex != -1 ) {
				String id = idList.getModel().getElementAt(idIndex).toString();
				int index = seminarList.getSelectedIndex();
				if ( index != -1 ) {
					Seminar seminar = (Seminar)(seminarList.getModel().getElementAt(index));
					((DefaultListModel)(seminarList.getModel())).removeElementAt(index);
					database.removeSeminar(id, seminar);
				}
			}
		}
	}

	private void init() throws Exception {
		repository = new EmailRepository("data/split/lectures");
		annotator = new ManualEmailAnnotator("data/labels.txt");
		database = new SeminarDatabase("data/seminars.txt");
		initUI();
	}

	private void initUI() throws Exception {
		// setup idList
		String[] idArray = repository.getEmailIDs();
		Vector idVector = new Vector();
		for ( int i=0; i<idArray.length; i++ ) {
			idVector.add(idArray[i]);
		}
		idList = new JList(idVector);
		JPanel idPanel = new JPanel(new BorderLayout());
		idPanel.add(new Label("Emails:"), BorderLayout.NORTH);
		idPanel.add(new JScrollPane(idList), BorderLayout.CENTER);
		idList.addListSelectionListener(this);

		// setup source text
		sourceText = new JTextArea();

		// setup annotation list
		annotationList = new JList(new DefaultListModel());

		// setup seminar panel
		seminarList = new JList(new DefaultListModel());
		JPanel seminarPanel = new JPanel(new BorderLayout());
		JPanel seminarButtonPanel = new JPanel(new GridLayout(1,2));
		JButton addSeminarButton = new JButton("Add");
		JButton removeSeminarButton = new JButton("Remove");
		addSeminarButton.addActionListener(this);
		removeSeminarButton.addActionListener(this);
		seminarButtonPanel.add(addSeminarButton);
		seminarButtonPanel.add(removeSeminarButton);
		seminarPanel.add(seminarButtonPanel, BorderLayout.SOUTH);
		seminarPanel.add(new JScrollPane(seminarList), BorderLayout.CENTER);

		// setup side Panel
		JPanel sidePanel = new JPanel(new BorderLayout());
		sidePanel.add(annotationList, BorderLayout.NORTH);
		sidePanel.add(seminarPanel, BorderLayout.CENTER);

		// setup frame
		frame = new JFrame("Seminar Labeller");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(idPanel, BorderLayout.WEST);
		frame.getContentPane().add(new JScrollPane(sourceText), BorderLayout.CENTER);
		frame.getContentPane().add(sidePanel, BorderLayout.EAST);
		frame.setBounds(50,50,1000,800);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					database.write();
					System.exit(0);
				}
				catch (java.io.IOException e) {
					System.out.println("Unable to write seminar data.");
				}
			}
		});

		frame.setVisible(true);
		idList.setSelectedIndex(0);
	}

	public static void main(String[] args) throws Exception {
		SeminarLabeller instance = new SeminarLabeller();
		instance.init();
	}
}

