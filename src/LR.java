import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;


public class LR {
	
	// tokenizes the document and attributes a numerical value to each token
	// that value is based on the token hashcode
	static HashMap<Integer, Integer> tokenizeDoc(String cur_doc, int N) {
        String[] words = cur_doc.split("\\s+");
        HashMap<Integer, Integer> tokens = new HashMap<Integer, Integer>();
        int wordHash;
        for (int i = 0; i < words.length; i++) {
        	words[i] = words[i].replaceAll("\\W", "");
        	if (words[i].length() > 0) {
        		wordHash = words[i].hashCode()%N;
        		if(wordHash<0){ 
        			wordHash += N;
        		}
        		
        		if(tokens.containsKey(wordHash)){
        			tokens.put(wordHash, tokens.get(wordHash)+1);
        		}
        		else{
        			tokens.put(wordHash, 1);
        		}
        	}
        }
        return tokens;
	}
	
	
	// ------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------
	// MAIN
	// ------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------
	
	
	public static void main(String[] args) {
		
		//Check if all parameters have been provided
		if(args.length < 6)
		{
			System.err.println("Not all arguments were provided");
			return;
		}

		//Read parameters
		int vocabSize = Integer.parseInt(args[0]);
		double initialLearningRate = Double.parseDouble(args[1]);
		double regCoeff = Double.parseDouble(args[2]);
		int maxIter = Integer.parseInt(args[3]);
		int trainingSize = Integer.parseInt(args[4]);
		String testFile = args[5];
		
		String[] existingLabels = {"nl","el","ru","sl","pl","ca","fr","tr","hu","de","hr","es","ga","pt"};
		int totalLabels = existingLabels.length;
		
		//link label to a number
		HashMap<String,Integer> labelPos = new HashMap<String,Integer>();
		for(int i=0; i<totalLabels; i++){
			labelPos.put(existingLabels[i], i);
		}
		
		//Weight vectors
		double[][] weight = new double[totalLabels][vocabSize];
		//Last update tracker
		int[][] lastUpdate = new int[totalLabels][vocabSize];
		
		
		//--------------------------------------------------------------------------------------------------
		// TRAINING
		//--------------------------------------------------------------------------------------------------
		
		try {
			//BufferedReader br = new BufferedReader(new FileReader(testFile));
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String line = br.readLine();
	        
	        //variables
	        int currentStep;
	        double currentLearningRate = initialLearningRate, p, y, betaDotX;
	        
	        //loops through the data. Each loop represents going once through the entire data  
	        for(int t=1; t<=maxIter; t++){
	        	
	        	//adjust learning rate
	        	currentLearningRate = initialLearningRate/(t*t);
	        	
	        	//loops through the points of the data set
	        	currentStep = 0;
				while (line != null && currentStep < trainingSize) {
					currentStep++;
					
					//read labels and words
					String[] labelsAndTokens = line.split("\\t",2);
					String[] labels = labelsAndTokens[0].split(",");
					HashSet<Integer> positiveLabelsPos = new HashSet<Integer>();
					for(String label : labels){
						positiveLabelsPos.add(labelPos.get(label));
					}
					HashMap<Integer, Integer> tokens = tokenizeDoc(labelsAndTokens[1],vocabSize);				
					
					//for each label
					for(int currentPos=0; currentPos<totalLabels; currentPos++){
						
						//compute p
						betaDotX = 0D;
						for(Entry<Integer, Integer> pair : tokens.entrySet()){
							betaDotX += pair.getValue()*weight[currentPos][pair.getKey()];
						}
						betaDotX = Math.exp(betaDotX);
						p = betaDotX / (1D + betaDotX);
						
						//compute y
						 y = positiveLabelsPos.contains(currentPos) ? 1D : 0D;
						
						
						//for each word, update weight
						for(Entry<Integer, Integer> pair : tokens.entrySet()){
							//regularization accummulated updates
							weight[currentPos][pair.getKey()] *= Math.pow(1D-2D*currentLearningRate*regCoeff, currentStep-lastUpdate[currentPos][pair.getKey()]);
							//gradient of loss update
							weight[currentPos][pair.getKey()] += currentLearningRate*(y-p)*pair.getValue();
							//update last update info
							lastUpdate[currentPos][pair.getKey()] = currentStep;
						}
						
					}//end of label loop
					
					line = br.readLine();	
					
				}//end of points loop (points in batch)
				
				
				//end of batch -> update all words from all labels
				for(int currentPos=0; currentPos<totalLabels; currentPos++){
					for(int currentToken=0; currentToken<vocabSize; currentToken++){
						weight[currentPos][currentToken] *= Math.pow(1D-2D*currentLearningRate*regCoeff, currentStep-lastUpdate[currentPos][currentToken]);
					}	
				}
				
				//reinitialize last update
				lastUpdate = new int[totalLabels][vocabSize];
				
	        }//end of t loop (batch loop)
			
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//debug
		/*
		for(int currentPos=0; currentPos<totalLabels; currentPos++){
			System.out.println(existingLabels[currentPos]);
			for(int i = 0; i<30; i++){
				System.out.print(String.valueOf(weight[currentPos][i])+" ");
			}
			System.out.println();
		}
		*/
		
		
		//--------------------------------------------------------------------------------------------------
		// TESTING
		//--------------------------------------------------------------------------------------------------
		
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(testFile));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			
			String line = br.readLine();
			
			while (line != null) {
				
				//read labels and words
				String[] labelsAndTokens = line.split("\\t",2);
				HashMap<Integer, Integer> tokens = tokenizeDoc(labelsAndTokens[1],vocabSize);
				
				//compute score for each label
				for(int currentPos=0; currentPos<totalLabels; currentPos++){
					
					//compute p
					double betaDotX = 0D, score;
					for(Entry<Integer, Integer> pair : tokens.entrySet()){
						betaDotX += pair.getValue()*weight[currentPos][pair.getKey()];
					}
					betaDotX = Math.exp(betaDotX);
					score = betaDotX / (1D + betaDotX);
					
					if(currentPos == 0){
						bw.append(existingLabels[currentPos] + "\t" + String.valueOf(score));
					}
					else{
						bw.append("," + existingLabels[currentPos] + "\t" + String.valueOf(score));
					}
				}
				bw.append("\n");
				
				line = br.readLine();
			}
			
			br.close();
			bw.flush();
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

}
