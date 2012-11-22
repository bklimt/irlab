
public class AnnotationDumper {
	public static void main(String[] args) throws Exception {
		boolean printID = false;
		for ( int i=0; i<args.length; i++ ) {
			if ( args[i].equals("-id") ) {
				printID=true;
			}
		}

		EmailRepository repository = new EmailRepository("data/split/lectures");
		EmailAnnotator annotator = new ManualEmailAnnotator("data/labels.txt");
		String[] ids = repository.getEmailIDs();
		for ( int i=0; i<ids.length; i++ ) {
			String id = ids[i];
			if ( printID ) {
				System.out.println(id);
			}
			Annotation[] annotations = annotator.getAnnotations(id, repository);
			for ( int j=0; j<annotations.length; j++ ) {
				for ( int k=0; k<args.length; k++ ) {
					if ( args[k].equals(annotations[j].type) ) {
						System.out.println(annotations[j].text);
					}
				}
			}
		}
	}
}

