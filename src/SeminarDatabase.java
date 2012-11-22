
import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class SeminarDatabase implements SeminarRetriever {
	HashMap seminars = null;
	String filename = null;

	public SeminarDatabase(String filename) throws FileNotFoundException, IOException {
		this.filename = filename;
		seminars = new HashMap();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = reader.readLine();
		while ( line != null ) {
			String[] parts = line.split("\t");
			String id = parts[0];
			int count = (new Integer(parts[1])).intValue();
			Vector annotationVector = new Vector();
			for ( int i=0; i<count; i++ ) {
				String line2 = reader.readLine();
				String[] parts2 = line2.split("\t");
				String type = parts2[0];
				int offset = (new Integer(parts2[1])).intValue();
				int length = (new Integer(parts2[2])).intValue();
				Annotation annotation = new Annotation(type,offset,length);
				annotation.text = parts2[3];
				annotationVector.add(annotation);
			}
			Annotation[] annotations = (Annotation[])annotationVector.toArray(new Annotation[0]);
			Seminar seminar = new Seminar(annotations);
			if ( seminars.get(id) == null ) {
				seminars.put(id,new Vector());
			}
			((Vector)seminars.get(id)).add(seminar);
			line = reader.readLine();
		}
	}

	protected void write() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		java.util.Set ids = seminars.keySet();
		java.util.Iterator iterator = ids.iterator();
		while ( iterator.hasNext() ) {
			String id = (String)iterator.next();
			Vector vector = (Vector)seminars.get(id);
			for ( int i=0; i<vector.size(); i++ ) {
				Seminar seminar = (Seminar)vector.get(i);
				writer.write(id + "\t" + seminar.getAnnotations().length + "\n");
				for ( int j=0; j<seminar.getAnnotations().length; j++ ) {
					Annotation annotation = seminar.getAnnotations()[j];
					writer.write(annotation.type + "\t");
					writer.write(annotation.offset + "\t");
					writer.write(annotation.length + "\t");
					writer.write(annotation.text + "\n" );
				}
			}
		}
		writer.flush();
		writer.close();
	}

	public Seminar[] getSeminars(String id) {
		if ( seminars.get(id) == null ) {
			return new Seminar[0];
		} else {
			return (Seminar[])(((Vector)(seminars.get(id))).toArray(new Seminar[0]));
		}
	}

	public void addSeminar(String id, Seminar seminar) {
		if ( seminars.get(id) == null ) {
			seminars.put(id,new Vector());
		}
		((Vector)seminars.get(id)).add(seminar);
	}

	public void removeSeminar(String id, Seminar seminar) {
		if ( seminars.get(id) != null ) {
			((Vector)seminars.get(id)).remove(seminar);
		}
	}
}

