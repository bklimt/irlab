
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NewSeminarFrame extends JFrame {
	private String id = null;
	private JCheckBox[] checkboxes = null;
	private Annotation[] annotations = null;
	private SeminarDatabase database = null;
	private JList list = null;

	public NewSeminarFrame(String nid, Annotation[] nannotations, SeminarDatabase ndatabase, JList nlist) {
		super("New Seminar for " + nid);

		id = nid;
		annotations = nannotations;
		database = ndatabase;
		list = nlist;

		java.util.Arrays.sort(annotations, new java.util.Comparator() {
			public int getSortValue(String type) {
				if (type.equals("title")) return 1;
				if (type.equals("name")) return 2;
				if (type.equals("time")) return 3;
				if (type.equals("date")) return 4;
				if (type.equals("location")) return 5;
				if (type.equals("affiliation")) return 6;
				if (type.equals("series")) return 7;
				return 0;
			}

			public int compare(Object obj1, Object obj2) {
				Annotation a1 = (Annotation)obj1;
				Annotation a2 = (Annotation)obj2;
				return getSortValue(a1.type) - getSortValue(a2.type);
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				java.util.Vector annotationVector = new java.util.Vector();
				for (int i=0; i<annotations.length; i++) {
					if ( checkboxes[i].isSelected() ) {
						annotationVector.add(annotations[i]);
					}
				}
				Annotation[] annotationArray = (Annotation[])annotationVector.toArray(new Annotation[0]);
				Seminar seminar = new Seminar(annotationArray);
				((DefaultListModel)(list.getModel())).addElement(seminar);
				database.addSeminar(id,seminar);
			}
		});

		getContentPane().setLayout(new GridLayout(annotations.length,1));
		checkboxes = new JCheckBox[annotations.length];
		for ( int i=0; i<annotations.length; i++ ) {
			checkboxes[i] = new JCheckBox(annotations[i].toString());
			getContentPane().add(checkboxes[i]);
		}

		setBounds(200,200,400,400);
		setVisible(true);
	}
}

