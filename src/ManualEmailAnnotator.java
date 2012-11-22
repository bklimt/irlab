
import java.io.*;
import java.util.Vector;
import java.util.HashMap;

public class ManualEmailAnnotator implements EmailAnnotator {
	HashMap annotations = null;

	ManualEmailAnnotator(String filename) throws FileNotFoundException, IOException {
		annotations = new HashMap();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = reader.readLine();
		while ( line != null ) {
			if ( line.matches("^addToType .*") ) {
				String[] parts = line.split(" ");
				String id = parts[1];
				Integer offset = new Integer(parts[2]);
				Integer length = new Integer(parts[3]);
				String type = parts[4];
				Annotation annotation = new Annotation(type,offset.intValue(),length.intValue());
				if ( annotations.get(id) == null ) {
					annotations.put(id,new Vector());
				}
				((Vector)annotations.get(id)).add(annotation);
			}
			line = reader.readLine();
		}
	}

	public Annotation[] getAnnotations(String id, EmailRepository repository) throws FileNotFoundException, IOException {
		Annotation[] data = (Annotation[])(((Vector)(annotations.get(id))).toArray(new Annotation[0]));
		String source = repository.getEmailSource(id);
		source = source.replaceAll("\\r","");
		for ( int i=0; i<data.length; i++ ) {
			data[i].text = source.substring(data[i].offset,data[i].offset+data[i].length);
			data[i].text = data[i].text.replaceAll("\\n"," ");
			data[i].text = data[i].text.replaceAll("\\t"," ");
			while ( data[i].text.matches(".*  .*") ) {
				data[i].text = data[i].text.replaceAll("  "," ");
			}
		}
		return data;
	}
}

