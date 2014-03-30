import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;


public class ResultEvaluator {

	
	public static void main(String[] args) {
		
		String testScoresFile = "/Users/daniel/Documents/Codes/ML605/HW5StreamingLogitRegression/src/question3-10000.txt";
		String testTrueLabelsFile = "/Users/daniel/Documents/Codes/ML605/HW5StreamingLogitRegression/data/abstract.test";
		
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
				
				for(int i=0; i<predictions.length; i+=2){
					if(Double.parseDouble(predictions[i+1])>0.5D){
						predictedLabels.add(predictions[i]);
					}
				}
				
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
