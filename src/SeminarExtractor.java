
import java.util.*;

public class SeminarExtractor implements SeminarRetriever {
	EmailAnnotator annotator = null;
	EmailRepository repository = null;

	public SeminarExtractor(EmailRepository repository, EmailAnnotator annotator) {
		this.annotator = annotator;
		this.repository = repository;
	}

	public Seminar[] getSeminars(String id) {
		try {
			Annotation[] annotations = annotator.getAnnotations(id,repository);
			return getSeminars(annotations);
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace(System.err);
			return null;
		}
	}

	public Seminar[] getSeminars(Annotation[] annotations) {
		Seminar.clearCache();
		Vector names = new Vector();
		Vector titles = new Vector();
		Vector dates = new Vector();
		Vector times = new Vector();
		Vector locations = new Vector();
		Vector affiliations = new Vector();
		Vector series = new Vector();
		for ( int i=0; i<annotations.length; i++ ) {
			if ( annotations[i].type.equals("name") ) {
				boolean found = false;
				for ( int j=0; !found && j<names.size(); j++ ) {
					if ( Seminar.sameName(annotations[i].text, ((Annotation)names.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					names.add(annotations[i]);
				}
			} else if ( annotations[i].type.equals("title") ) {
				boolean found = false;
				for ( int j=0; !found && j<titles.size(); j++ ) {
					if ( Seminar.sameTitle(annotations[i].text, ((Annotation)titles.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					titles.add(annotations[i]);
				}
			} else if ( annotations[i].type.equals("date") ) {
				boolean found = false;
				for ( int j=0; !found && j<dates.size(); j++ ) {
					if ( Seminar.sameDate(annotations[i].text, ((Annotation)dates.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					dates.add(annotations[i]);
				}
			} else if ( annotations[i].type.equals("time") ) {
				boolean found = false;
				for ( int j=0; !found && j<times.size(); j++ ) {
					if ( Seminar.sameTime(annotations[i].text, ((Annotation)times.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					times.add(annotations[i]);
				}
			} else if ( annotations[i].type.equals("location") ) {
				boolean found = false;
				for ( int j=0; !found && j<locations.size(); j++ ) {
					if ( Seminar.sameLocation(annotations[i].text, ((Annotation)locations.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					locations.add(annotations[i]);
				}
			} else if ( annotations[i].type.equals("affiliation") ) {
				boolean found = false;
				for ( int j=0; !found && j<affiliations.size(); j++ ) {
					if ( Seminar.sameAffiliation(annotations[i].text, ((Annotation)affiliations.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					affiliations.add(annotations[i]);
				}
			} else if ( annotations[i].type.equals("series") ) {
				boolean found = false;
				for ( int j=0; !found && j<series.size(); j++ ) {
					if ( Seminar.sameSeries(annotations[i].text, ((Annotation)series.get(j)).text) ) {
						found = true;
					}
				}
				if ( !found ) {
					series.add(annotations[i]);
				}
			}
		}
		int count = ( names.size() < titles.size() ? names.size() : titles.size() );
		if ( count == 0 && ( names.size() > 0 || titles.size() > 0 ) ) {
			count = names.size() + titles.size();
		}

		Seminar[] seminars = new Seminar[count];
		for ( int i=0; i<count; i++ ) {
			ArrayList seminarAnnotations = new ArrayList();

			if ( titles.size() != 0 ) {
				seminarAnnotations.add((Annotation)titles.get(i % titles.size()));
			}
			if ( names.size() != 0 ) {
				seminarAnnotations.add((Annotation)names.get(i % names.size()));
			}
			if ( locations.size() != 0 ) {
				seminarAnnotations.add((Annotation)locations.get(i % locations.size()));
			}
			if ( series.size() != 0 ) {
				seminarAnnotations.add((Annotation)series.get(i % series.size()));
			}
			if ( affiliations.size() != 0 ) {
				seminarAnnotations.add((Annotation)affiliations.get(i % affiliations.size()));
			}
			if ( times.size() != 0 ) {
				if ( times.size() <= count ) {
					seminarAnnotations.add((Annotation)times.get(i % times.size()));
				} else {
					seminarAnnotations.add((Annotation)times.get((times.size()-count)+i));
				}
			}
			if ( dates.size() != 0 ) {
				seminarAnnotations.add((Annotation)dates.get(i % dates.size()));
			}

			Annotation[] annotationArray = (Annotation[])seminarAnnotations.toArray(new Annotation[0]);
			seminars[i] = new Seminar(annotationArray);
		}

		return seminars;
	}

	public static void main(String[] args) throws Exception {
		EmailRepository repository = new EmailRepository("data/split/lectures");
		EmailAnnotator annotator = new ManualEmailAnnotator("data/labels.txt");
		SeminarDatabase database = new SeminarDatabase("data/seminars.txt");
		SeminarExtractor extractor = new SeminarExtractor(repository, annotator);
		SeminarEvaluator evaluator = new SeminarEvaluator();
		String[] ids = repository.getEmailIDs();

		System.out.println("<html><body>");
		for ( int i=0; i<ids.length; i++ ) {
			String id = ids[i];
			Annotation[] annotations = annotator.getAnnotations(id, repository);
			Seminar[] actual = database.getSeminars(id);
			Seminar[] predicted = extractor.getSeminars(annotations);
			System.out.println(id+"<br>");
			evaluator.evaluate(predicted,actual,true);
		}
		evaluator.printResults();
		System.out.println("</body></html>");
	}
}

