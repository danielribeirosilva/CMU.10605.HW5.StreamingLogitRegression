
public class LabelScorePair implements Comparable<LabelScorePair> {
	
	public String label;
	public double score;
	
	public LabelScorePair(String label, double score){
		this.label = label;
		this.score = score;
	}

	@Override
	public int compareTo(LabelScorePair other) {
		return Double.compare(this.score,other.score);
	}

}
