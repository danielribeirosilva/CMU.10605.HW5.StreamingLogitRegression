import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;


public class ResultEvaluatorNB {

	
	public static void main(String[] args) {
		
		String testScoresFile = "/Users/daniel/Documents/Codes/ML605/HW5StreamingLogitRegression/src/question4-tiny.txt";
		String testTrueLabelsFile = "/Users/daniel/Documents/Codes/ML605/HW5StreamingLogitRegression/data/abstract.tiny.test";
		
		String[] existingLabels = {"nl","el","ru","sl","pl","ca","fr","tr","hu","de","hr","es","ga","pt"};
		HashSet<String> allLabels = new HashSet<String>(Arrays.asList(existingLabels));
		
		long totalTruePositives = 0;
		long totalTrueNegatives = 0;
		long totalFalsePositives = 0;
		long totalFalseNegatives = 0;
		
		try {
			BufferedReader scoresBR = new BufferedReader(new FileReader(testScoresFile));
			BufferedReader labelsBR = new BufferedReader(new FileReader(testTrueLabelsFile));
			
			String lineScores = scoresBR.readLine();
			String lineLabels = labelsBR.readLine();
			
			while(lineLabels != null && lineScores != null){
				long currentTruePositives = 0;
				long currentTrueNegatives = 0;
				long currentFalsePositives = 0;
				long currentFalseNegatives = 0;
				
				String[] trueLabels = lineLabels.split("\\t")[0].split(",");
				HashSet<String> trueLabelsSet = new HashSet<String>(Arrays.asList(trueLabels));
				String[] predictions = lineScores.split("[\\t,]");
				HashSet<String> predictedLabels = new HashSet<String>();
				
				//add predictions to priority queue
				PriorityQueue<LabelScorePair> pq = new PriorityQueue<LabelScorePair>();
				for(int i=0; i<predictions.length; i+=2){
					String currentLabel = predictions[i];
					double currentScore = Double.parseDouble(predictions[i+1]);
					pq.add(new LabelScorePair(currentLabel,currentScore));
				}
				
				//make predictions (predict first 2)
				predictedLabels.add(pq.poll().label);
				//predictedLabels.add(pq.poll().label);
				
				for(String pred : predictedLabels){
					if(trueLabelsSet.contains(pred)){
						currentTruePositives++;
					}
					else{
						currentFalsePositives++;
					}
				}
				currentFalseNegatives = trueLabelsSet.size() - currentTruePositives;
				currentTrueNegatives = allLabels.size() - (currentTruePositives + currentFalsePositives + currentFalseNegatives);
				
				totalFalseNegatives += currentFalseNegatives;
				totalFalsePositives += currentFalsePositives;
				totalTrueNegatives += currentTrueNegatives;
				totalTruePositives += currentTruePositives;
				
				lineScores = scoresBR.readLine();
				lineLabels = labelsBR.readLine();
			}
			
			scoresBR.close();
			labelsBR.close();
			
			long corrects = totalTrueNegatives+totalTruePositives;
			long incorrects = totalFalseNegatives + totalFalsePositives;
			Double accuracy = (double)corrects / (double)(corrects + incorrects);
			
			/*
			System.out.println("TP: "+String.valueOf(totalTruePositives));
			System.out.println("TN: "+String.valueOf(totalTrueNegatives));
			System.out.println("FP: "+String.valueOf(totalFalsePositives));
			System.out.println("FN: "+String.valueOf(totalFalseNegatives));
			System.out.println("----------------------------------------");
			System.out.println("Accuracy: "+String.valueOf(accuracy));
			*/
			
			System.out.println(String.valueOf(totalTruePositives));
			System.out.println(String.valueOf(totalTrueNegatives));
			System.out.println(String.valueOf(totalFalsePositives));
			System.out.println(String.valueOf(totalFalseNegatives));
			System.out.println(String.valueOf(accuracy));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
