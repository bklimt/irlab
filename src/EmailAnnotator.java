
public interface EmailAnnotator {
	public Annotation[] getAnnotations(String id, EmailRepository repository) throws java.io.FileNotFoundException, java.io.IOException;
}

