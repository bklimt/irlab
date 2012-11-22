
import java.io.*;
import edu.cmu.minorthird.*;
import edu.cmu.minorthird.text.mixup.*;
import edu.cmu.minorthird.text.Span;
import edu.cmu.minorthird.text.MonotonicTextLabels;
import edu.cmu.minorthird.text.BasicTextLabels;
import edu.cmu.minorthird.text.BasicTextBase;

import edu.cmu.minorthird.text.Tokenizer;

public class MixupAnnotator implements EmailAnnotator {
	private MixupProgram program = null;
	private edu.cmu.minorthird.text.MixupAnnotator annotator = null;

	public MixupAnnotator(String filename) throws Mixup.ParseException, FileNotFoundException, IOException {
		program = new MixupProgram(new File(filename));
		annotator = new edu.cmu.minorthird.text.MixupAnnotator(program);
	}

	public void testTokenizer(String text) {
		Tokenizer tokenizer = new Tokenizer(Tokenizer.REGEX,"[\\t\\r ]*([0-9]+|[a-zA-Z]+|\\W)[\\t\\r ]*");
		String[] tokens = tokenizer.splitIntoTokens(text);
		for ( int i=0; i<tokens.length; i++ ) {
			System.out.print(tokens[i].replaceAll("\\n","\\\\n").replaceAll("\\t","\\\\t")+"|");
		}
		System.out.println("");
	}

	public Annotation[] getAnnotations(String text) {
		java.util.HashSet annotations = new java.util.HashSet();
		BasicTextBase textbase = new BasicTextBase();
		textbase.loadDocument("nullId",text,"[\\t\\r ]*([0-9]+|[a-zA-Z]+|\\W)[\\t\\r ]*");
		MonotonicTextLabels labels = new BasicTextLabels(textbase);
		annotator.annotate(labels);
		java.util.Set types = labels.getTypes();
		java.util.Iterator iterator = types.iterator();
		while ( iterator.hasNext() ) {
			String type = (String)iterator.next();
			if (!type.equals("date") &&
				!type.equals("time") &&
				!type.equals("name") &&
				!type.equals("title") &&
				!type.equals("location") &&
				!type.equals("affiliation") &&
				!type.equals("series") ) {
				continue;
			}

			Span.Looper looper = labels.instanceIterator(type);
			while ( looper.hasNext() ) {
				Span span = looper.nextSpan();
				String spanText = span.asString();
				int charOffset = 0;
				int offset = 0;
				for ( offset=0; offset<text.length(); offset++ ) {
					if ( text.charAt(offset) != ' ' && text.charAt(offset) != '_' && text.charAt(offset) != '\t' ) {
						if ( charOffset == span.getLoChar() ) {
							break;
						}
						charOffset++;
					}
				}

				offset = text.indexOf(spanText);
				while ( offset != -1 ) {
					Annotation annotation = new Annotation(type,offset,spanText.length(),spanText);
					java.util.Iterator otherAnnotations = annotations.iterator();
					boolean found = false;
					while ( otherAnnotations.hasNext() ) {
						Annotation other = (Annotation)otherAnnotations.next();
						if ( other.contains(annotation) ) {
							found = true;
							System.out.println(" not adding "+annotation.toString()+" contained by "+other.toString());
						} else {
							if ( annotation.contains(other) ) {
								System.out.println("   removing "+other.toString()+" contained by "+annotation.toString());
								annotations.remove(other);
								otherAnnotations = annotations.iterator();
							}
						}
					}
					if ( !found ) {
						annotations.add(annotation);
						System.out.println("     adding "+annotation.toString());
					}
					offset = text.indexOf(spanText, offset+1);
				}

				/*if ( type.equals("name") ) {
					break;
				}*/
			}
		}
		return (Annotation[])annotations.toArray(new Annotation[0]);
	}

	public Annotation[] getAnnotations(String id, EmailRepository repository) throws FileNotFoundException, IOException {
		return getAnnotations(repository.getEmailSource(id));
	}

	public static void main(String[] args) throws Exception {
		MixupAnnotator annotator = new MixupAnnotator("src/Annotator.mixup");
		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		String text = "";
		String line = reader.readLine();
		while ( line != null ) {
			text += line;
			text += '\n';
			line = reader.readLine();
		}
		text = text.replaceAll("\\r","");
		text = text.replaceAll("\\t"," ");
		//annotator.testTokenizer(text);
		Annotation[] annotations = annotator.getAnnotations(text);
		if ( annotations == null ) {
			System.out.println("null");
		} else {
			for ( int i=0; i<annotations.length; i++ ) {
				System.out.println(annotations[i]);
			}
		}
	}
}

