
import java.util.*;

class SeminarCombination implements Comparable {
	public int correct = 0;
	public int partial = 0;
	public int missed = 0;
	public int falseAlarm = 0;
	public LinkedList pairs = null;

	public SeminarCombination() {
		pairs = new LinkedList();
	}

	public void addLast(SeminarPair pair) {
		pairs.addLast(pair);
		correct += pair.correct;
		partial += pair.partial;
		missed += pair.missed;
		falseAlarm += pair.falseAlarm;
	}

	public void removeLast() {
		SeminarPair pair = (SeminarPair)pairs.removeLast();
		correct -= pair.correct;
		partial -= pair.partial;
		missed -= pair.missed;
		falseAlarm -= pair.falseAlarm;
	}

	public Object clone() {
		SeminarCombination other = new SeminarCombination();
		other.correct = correct;
		other.partial = partial;
		other.missed = missed;
		other.falseAlarm = falseAlarm;
		other.pairs = (LinkedList)pairs.clone();
		return other;
	}

	public int compareTo(Object obj) {
		SeminarCombination other = (SeminarCombination)obj;
		if ( correct != other.correct ) {
			return correct - other.correct;
		} else if ( partial != other.partial ) {
			return partial - other.partial;
		} else if ( missed != other.missed ) {
			return missed - other.missed;
		} else if ( falseAlarm != other.falseAlarm ) {
			return falseAlarm - other.falseAlarm;
		}
		// try other stuff?
		return 0;
	}

	public String toString() {
		String ans = "<TABLE BORDER=1>\n";
		ListIterator iterator = pairs.listIterator();
		while ( iterator.hasNext() ) {
			SeminarPair pair = (SeminarPair)iterator.next();
			ans += pair.toString();
		}
		ans += "</TABLE>\n";
		return ans;
	}
}

class SeminarPair {
	public int correct = 0;
	public int partial = 0;
	public int missed = 0;
	public int falseAlarm = 0;
	public Seminar predicted = null;
	public Seminar actual = null;

	private SeminarPair(Seminar predicted, Seminar actual) {
		/*
		if ( predicted != null ) {
			this.predicted = (Seminar)predicted.clone();
		}
		if ( actual != null ) {
			this.actual = (Seminar)actual.clone();
		}
		*/
		this.predicted = predicted;
		this.actual = actual;
	}

	public Object clone() {
		SeminarPair other = new SeminarPair(predicted,actual);
		other.correct = this.correct;
		other.partial = this.partial;
		other.missed = this.missed;
		other.falseAlarm = this.falseAlarm;
		return other;
	}

	public static SeminarPair compare(Seminar predicted, Seminar actual) {
		Seminar.clearCache();
		SeminarPair pair = new SeminarPair(predicted, actual);
		// do the comparison
		if ( predicted == null && actual == null ) {
			return null;
		} else if ( predicted == null ) {
			pair.missed = 1;
		} else if ( actual == null ) {
			pair.falseAlarm = 1;
		} else {
			String predictedTitle = predicted.getTitle();
			String predictedName = predicted.getName();
			String predictedDate = predicted.getDate();
			String predictedTime = predicted.getTime();
			String predictedLocation = predicted.getLocation();
			if ( !actual.hasTitle(predictedTitle) && !actual.hasName(predictedName) ) {
				return null;
			} else {
				if (
					actual.hasName(predictedName) &&
					actual.hasTitle(predictedTitle) &&
					actual.hasDate(predictedDate) &&
					actual.hasTime(predictedTime) &&
					actual.hasLocation(predictedLocation)
				) {
					pair.correct = 1;
				} else {
					pair.partial = 1;
				}
			}
		}
		return pair;
	}

	public String toString() {
		String actualString = "null";
		String predictedString = "null";
		String scoreString = "correct="+correct+"<br />\npartial="+partial+"<br />\nmissed="+missed+"<br />\nfalseAlarm="+falseAlarm;
		if ( actual != null ) {
			actualString = actual.toString();
		}
		if ( predicted != null ) {
			predictedString = predicted.toString();
		}
		predictedString = predictedString.replaceAll("<\\/*HTML>","");
		actualString = actualString.replaceAll("<\\/*HTML>","");
		return "<TR><TD>" + actualString + "</TD><TD>" + predictedString + "</TD><TD>" + scoreString + "</TD></TR>\n";
	}
}

public class SeminarEvaluator {
	public int correct = 0;
	public int partial = 0;
	public int missed = 0;
	public int falseAlarm = 0;
	//private LinkedList combinations = null;
	private SeminarCombination bestCombination = null;

	public SeminarEvaluator() {
		//combinations = new LinkedList();
	}

	public String toString() {
		String ans = "";
		ans = ans + "correct="+correct+"<br />\npartial="+partial+"<br />\nmissed="+missed+"<br />\nfalseAlarm="+falseAlarm+"<br />\n";
		//ListIterator iterator = combinations.listIterator();
		//while ( iterator.hasNext() ) {
		//	SeminarCombination combination = (SeminarCombination)iterator.next();
		//	ans += combination.toString();
		//}
		return ans;
	}

	public void printResults() {
		System.out.println(this);
	}

	public void evaluate(Seminar[] predicted, Seminar[] actual) {
		evaluate(predicted, actual, false);
	}

	public void evaluate(Seminar[] predicted, Seminar[] actual, boolean verbose) {
		bestCombination = null;
		generateCombinations(predicted, actual);
		if ( verbose ) {
			System.out.println(bestCombination);
		}
		//combinations.addLast(bestCombination);
		correct += bestCombination.correct;
		partial += bestCombination.partial;
		missed += bestCombination.missed;
		falseAlarm += bestCombination.falseAlarm;
	}

	private void generateCombinations(Seminar[] predicted, Seminar[] actual) {
		LinkedList a = new LinkedList();
		LinkedList b = new LinkedList();
		for ( int i=0; i<predicted.length; i++ ) {
			a.addLast(predicted[i]);
		}
		for ( int i=0; i<actual.length; i++ ) {
			b.addLast(actual[i]);
		}
		//System.out.println("beginning a combination of size " + predicted.length + " X " + actual.length );
		generateCombinations(new SeminarCombination(), a, b);
	}

	private void generateCombinations(SeminarCombination combination, LinkedList a, LinkedList b) {
		if ( a.size() == 0 ) {
			if ( b.size() == 0 ) {
				//System.out.println("trying a combination");
				if ( bestCombination == null ) {
					bestCombination = (SeminarCombination)combination.clone();
				} else {
					if ( combination.compareTo(bestCombination) > 0 ) {
						bestCombination = (SeminarCombination)combination.clone();
					}
				}
			} else {
				Seminar bItem = (Seminar)b.removeFirst();
				SeminarPair pair = SeminarPair.compare(null, bItem);
				if ( pair != null ) {
					combination.addLast(pair);
					generateCombinations(combination, a, b);
					combination.removeLast();
				}
				b.addFirst(bItem);
			}
		} else {
			Seminar aItem = (Seminar)a.removeFirst();
			for ( int j=0; j<b.size(); j++ ) {
				Seminar bItem = (Seminar)b.remove(j);
				SeminarPair pair = SeminarPair.compare(aItem, bItem);
				if ( pair != null ) {
					combination.addLast(pair);
					generateCombinations(combination, a, b);
					combination.removeLast();
				}
				b.add(j, bItem);
			}
			SeminarPair pair = SeminarPair.compare(aItem, null);
			if ( pair != null ) {
				combination.addLast(pair);
				generateCombinations(combination, a, b);
				combination.removeLast();
			}
			a.addFirst(aItem);
		}
	}
}

