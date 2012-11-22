
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Search {
	private EmailRepository repository = null;
	private Set stopwords = new HashSet();
	private ArrayList documents = new ArrayList();
	private ArrayList words = new ArrayList();

	private HashMap documentMap = new HashMap();
	private HashMap wordMap = new HashMap();

	private int totalDocumentLength = 0;

	public class QueryData {
		private HashMap scores = new HashMap();
		private HashMap docMap = new HashMap();
		private HashMap annotations = new HashMap();

		public boolean hasScore(String docID) {
			return scores.get(docID) != null;
		}

		public double getScore(String docID) {
			return ((Double)scores.get(docID)).doubleValue();
		}

		public void setScore(String docID, double value) {
			scores.put(docID, new Double(value));
		}

		public boolean hasDocMap(String docID) {
			return docMap.get(docID) != null;
		}

		public ArrayList getDocMap(String docID) {
			return (ArrayList)docMap.get(docID);
		}

		public void createDocMap(String docID, int size) {
			ArrayList array = new ArrayList();
			for ( int i=0; i<size; i++ ) {
				array.add(new Double(0));
			}
			docMap.put(docID, array);
		}

		public double getDocMap(String docID, int index) {
			return ((Double)getDocMap(docID).get(index)).doubleValue();
		}

		public void setDocMap(String docID, int index, double value) {
			getDocMap(docID).set(index, new Double(value));
		}

		public Iterator getIterator() {
			return scores.keySet().iterator();
		}

		public boolean hasAnnotations(String docID) {
			return annotations.get(docID) != null;
		}

		public ArrayList getAnnotations(String docID) {
			return (ArrayList)annotations.get(docID);
		}

		public void createAnnotations(String docID) {
			annotations.put(docID, new ArrayList());
		}

		public Annotation getAnnotation(String docID, int index) {
			return (Annotation)getAnnotations(docID).get(index);
		}

		public void addAnnotation(String docID, Annotation annotation) {
			getAnnotations(docID).add(annotation);
		}
	}

	public class QueryResult implements Comparable {
		public String documentID;
		public double score;
		public String bestSnippet;
		public StringBuffer relevanceMask;
		public Annotation[] annotations;

		public String toString() {
			String ans = "" + score + "\t" + documentID + "\t" + bestSnippet + "\n";
			for ( int i=0; i<annotations.length; i++ ) {
				ans += ("\t" + annotations[i] + "\n");
			}
			return ans;
		}

		public int compareTo(Object pother) {
			QueryResult other = (QueryResult)pother;
			if ( score == other.score ) {
				return documentID.compareTo(other.documentID);
			} else {
				return -((new Double(score)).compareTo(new Double(other.score)));
			}
		}
	}

	public class Document {
		public String id;
		public int wordLength;
		public int byteLength;
	}

	public class Position {
		public String section;
		public int byteOffset;
		public int byteLength;
		double weight;
	}

	public class Occurrence {
		public int count;
		public ArrayList positions = new ArrayList();
	}

	public class Word {
		public String text;
		public int df;
		public HashMap occurrences = new HashMap();
	}

	public class IndexDocumentState {
		public String id;
		public String section;
		public Stack weights = new Stack();
	}

	public interface ProcessEnglish {
		public abstract void processEnglish(String word, int byteOffset, int byteLength, Object data);
	}

	public interface ProcessScore {
		public abstract void processScore(
			String did,
			String section,
			String text,
			int byteDocumentLength,
			int byteOffset,
			int byteLength,
			double score,
			Object data);
	}

	public Search(EmailRepository repository) {
		this.repository = repository;
	}

	void loadStopwords(String filename) throws FileNotFoundException, IOException {
		stopwords = new HashSet();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = reader.readLine();
		while ( line != null ) {
			stopwords.add(line.toLowerCase());
			line = reader.readLine();
		}
	}

	String stem(String word) {
		return word;
	}

	boolean isStopword(String word) {
		return stopwords.contains(word.toLowerCase());
	}

	public void indexWord(String s, int byteOffset, int byteLength, IndexDocumentState state) {
		int wordIndex = 0;
		int documentIndex = 0;

		Integer wordPtr = (Integer)wordMap.get(s);
		if ( wordPtr == null ) {
			Word newWord = new Word();
			newWord.df = 0;
			newWord.text = s;
			wordIndex = words.size();
			wordMap.put(s, new Integer(wordIndex));
			words.add(newWord);
		} else {
			wordIndex = wordPtr.intValue();
		}
		Word word = (Word)words.get(wordIndex);

		Integer documentPtr = (Integer)documentMap.get(state.id);
		if ( documentPtr == null ) {
			Document newDocument = new Document();
			newDocument.id = state.id;
			newDocument.wordLength = 0;
			newDocument.byteLength = 0;
			documentIndex = documents.size();
			documentMap.put(state.id, new Integer(documentIndex));
			documents.add(newDocument);
		} else {
			documentIndex = documentPtr.intValue();
		}
		Document document = (Document)documents.get(documentIndex);

		Occurrence occurrence = (Occurrence)(word.occurrences.get(new Integer(documentIndex)));
		if ( occurrence == null ) {
			occurrence = new Occurrence();
			occurrence.count = 0;
			word.occurrences.put(new Integer(documentIndex), occurrence);
		}

		Position newPosition = new Position();
		newPosition.section = state.section;
		newPosition.byteOffset = byteOffset;
		newPosition.byteLength = byteLength;
		newPosition.weight = ((Double)state.weights.peek()).intValue();

		occurrence.count++;
		occurrence.positions.add(newPosition);
		word.df++;
		document.wordLength++;
		if ( byteOffset + byteLength > document.byteLength ) {
			document.byteLength = byteOffset + byteLength;
		}
		totalDocumentLength++;
	}

	public void parseText(String text, int byteOffset, int byteLength, ProcessEnglish processEnglish, Object data) {
		int start = 0;
		int end = 0;
		while ( start < text.length() ) {
			if ( Character.isLetterOrDigit(text.charAt(start)) ) {
				while ( end < text.length() && Character.isLetterOrDigit(text.charAt(end)) ) {
					end++;
				}
				if ( processEnglish != null ) {
					processEnglish.processEnglish(text.substring(start,end), byteOffset, end-start, data);
				}
				byteOffset += end-start;
				start = end;
			} else {
				start++;
				end++;
				byteOffset++;
			}
		}
	}

	void lookupWord(String s, ProcessScore processScore, Object data) {
		Integer wordPtr = (Integer)wordMap.get(s);
		if ( wordPtr != null ) {
			int wordID = ((Integer)wordPtr).intValue();
			Word word = (Word)words.get(wordID);
			Iterator docIt = word.occurrences.keySet().iterator();
			while ( docIt.hasNext() ) {
				Integer docIndexPtr = (Integer)docIt.next();
				Document document = (Document)documents.get(docIndexPtr.intValue());
				Occurrence occurrence = (Occurrence)word.occurrences.get(docIndexPtr);
				String did = document.id;
				int dl = document.wordLength;
				int bytedl = document.byteLength;
				double adl = (double)totalDocumentLength/documents.size();
				int df = word.df;
				double tf = 0.0;
				for ( int i=0; i<occurrence.positions.size(); i++ ) {
					Position position = (Position)occurrence.positions.get(i);
					tf = position.weight;
					double tfw = tf / ( tf + 1.0 + (dl/adl) );
					double idf = 1.0 / df;
					double score = ( tfw * idf );
					int byteOffset = position.byteOffset;
					int byteLength = position.byteLength;
					String section = position.section;
					processScore.processScore(did,section,s,bytedl,byteOffset,byteLength,score,data);
				}
			}
		}
	}

	public void indexEmail(String id, String text, Object data) {
		IndexDocumentState state = new IndexDocumentState();
		state.id = id;
		state.section = "body";
		state.weights.push(new Double(1));
		Matcher bodyMatcher = Pattern.compile("<body>").matcher(text);
		int bodyOffset = ( bodyMatcher.find() ? bodyMatcher.end() : 0 );
		ProcessEnglish indexEnglish = new ProcessEnglish() {
			public void processEnglish(String word, int byteOffset, int byteLength, Object pstate) {
				if ( !isStopword(word) ) {
					word = word.toLowerCase();
					word = stem(word);
					if ( word != null ) {
						indexWord( word, byteOffset, byteLength, (IndexDocumentState)pstate );
					}
				}
			}
		};
		parseText( text.substring(bodyOffset), bodyOffset, text.length()-bodyOffset, indexEnglish, state );
		state.weights.pop();
	}

	public String getEmailBody(String id) throws FileNotFoundException, IOException {
		String text = repository.getEmailSource(id);
		/*text = text.replaceAll("\\r", "");
		text = text.replaceAll("\\n", " ");
		text = text.replaceAll("^.*<body>", "");
		text = text.replaceAll("</body>.*", "");*/
		return text;
	}

	public void loadDocument(String id) throws FileNotFoundException, IOException {
		String text = getEmailBody(id);
		indexEmail(id,text,null);
	}

	public void loadDocuments() throws FileNotFoundException, IOException {
		String[] ids = repository.getEmailIDs();
		for (int i=0; i<ids.length; i++) {
			loadDocument(ids[i]);
		}
	}

	public String getSnippet(String docID, int start, int length, StringBuffer relevanceMask) throws FileNotFoundException, IOException {
		String text = getEmailBody(docID);
		return text.substring(start,start+length).replaceAll("\r","").replaceAll("\n"," ").replaceAll("  *"," ");
	}

	public void findBestSnippet( QueryResult result, int length, int context, QueryData query ) throws FileNotFoundException, IOException {
		String docID = result.documentID;
		int docLength = query.getDocMap(docID).size();

		result.relevanceMask = new StringBuffer();
		for ( int i=0; i<docLength; i++ ) {
			double relevance = query.getDocMap(docID,i);
			result.relevanceMask.append(relevance == 0.0 ? ' ' : '*');
		}

		if ( docLength < length ) {
			length = docLength;
		}
		if ( docLength == length ) {
			result.bestSnippet = getSnippet(docID, 0, length, result.relevanceMask);
			return;
		}

		double[] scoreMap = new double[docLength-length];
		double currentScore = 0;
		int currentIndex = 0;
		for ( int i=0; i<length; i++ ) {
			currentScore += query.getDocMap(docID,i);
		}
		scoreMap[currentIndex] = currentScore;

		for ( currentIndex=1; currentIndex<scoreMap.length; currentIndex++ ) {
			currentScore -= query.getDocMap(docID, currentIndex-1);
			currentScore += query.getDocMap(docID, currentIndex+(length-1));
			scoreMap[currentIndex] = currentScore;
		}

		int bestIndex = 0;
		double bestScore = scoreMap[0];
		for ( int i=1; i<scoreMap.length; i++ ) {
			if ( scoreMap[i] > bestScore ) {
				bestScore = scoreMap[i];
				bestIndex = i;
			}
		}

		while ( bestIndex + context >= scoreMap.length ) {
			context--;
		}
		if ( bestScore != 0 ) {
			if ( scoreMap[bestIndex+context] == bestScore ) {
				bestIndex += context;
			}
		}

		result.bestSnippet = getSnippet(docID, bestIndex, length, result.relevanceMask);
	}

	public QueryResult[] doQuery(String queryText) throws FileNotFoundException, IOException {
		return doQuery(queryText, 0, 0);
	}

	public QueryResult[] doQuery(String queryText, int snippetLength, int snippetContext) throws FileNotFoundException, IOException {
		ProcessEnglish queryEnglish = new ProcessEnglish() {
			public void processEnglish( String word, int byteOffset, int byteLength, Object pq ) {
				ProcessScore processScore = new ProcessScore() {
					public void processScore(String docID, String section, String text, int docLength, int byteOffset, int byteLength, double score, Object pq) {
						QueryData q = (QueryData)pq;
						if ( !q.hasScore(docID) ) {
							q.setScore(docID,score);
						} else {
							q.setScore(docID,score+q.getScore(docID));
						}

						if ( !q.hasDocMap(docID) ) {
							q.createDocMap(docID,docLength);
						}
						if ( section.equals("body") ) {
							for ( int i=byteOffset; i<byteOffset+byteLength; i++ ) {
								q.setDocMap(docID,i,q.getDocMap(docID,i)+1);
							}
						}

						if ( !q.hasAnnotations(docID) ) {
							q.createAnnotations(docID);
						}
						q.addAnnotation(docID, new Annotation("search",byteOffset,byteLength,text));
					}
				};
				if ( !isStopword(word) ) {
					word = word.toLowerCase();
					word = stem(word);
					if ( word != null ) {
						lookupWord(word, processScore, pq);
					}
				}
			}
		};
		QueryData q = new QueryData();
		parseText( queryText, 0, queryText.length(), queryEnglish, q );
		ArrayList ans = new ArrayList();
		Iterator it = q.getIterator();
		while ( it.hasNext() ) {
			String docID = (String)it.next();
			QueryResult result = new QueryResult();
			result.score = q.getScore(docID);
			result.documentID = docID;
			result.annotations = (Annotation[])q.getAnnotations(docID).toArray(new Annotation[0]);
			findBestSnippet( result, snippetLength, snippetContext, q );
			ans.add( result );
		}
			
		QueryResult[] res = (QueryResult[])ans.toArray(new QueryResult[0]);
		Arrays.sort(res);
		return res;
	}

	public static void main(String[] args) throws Exception {
		if ( args.length != 3 ) {
			System.err.println("usage: java -cp obj Search <string> <snippet-length> <snippet-context>");
			System.exit(-1);
		}
		
		System.out.println("indexing corpus...");
		Search search = new Search(new EmailRepository("data/split/lectures"));
		search.loadStopwords("data/common_words.txt");
		search.loadDocuments();

		String query = args[0];
		int snippetLength = (new Integer(args[1])).intValue();
		int snippetContext = (new Integer(args[2])).intValue();

		System.out.println("processing results...");
		QueryResult[] results = search.doQuery(query,snippetLength,snippetContext);
		for ( int i=0; i<results.length; i++ ) {
			System.out.println( results[i] );
		}
	}
}

