import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.Map.Entry;


public class NBTest {
	
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

	public static void main(String[] args) {
		
		
		if(args.length < 2){
			System.err.println("not all arguments were provided");
			System.exit(0);
		}
		
		int VOCABULARY_SIZE = Integer.parseInt(args[0]);
		String testPath = args[1];
		
		
		/*
		|----------------------------------------------------------------------
		|  First go through the testing data - Needed vocabulary
		|----------------------------------------------------------------------
		|  Go through the testing set and get all the needed
		|  tokens for the NB estimation
		|----------------------------------------------------------------------
		*/
		HashSet<Integer> neededWords = new HashSet<Integer>();
		
		try {
	        BufferedReader br = new BufferedReader(new FileReader(testPath));
	        String line = br.readLine(); 
			while (line != null) {
			
				//read labels and words
				String[] labelsAndTokens = line.split("\\t",2);
				HashMap<Integer, Integer> tokensAndCount = tokenizeDoc(labelsAndTokens[1], VOCABULARY_SIZE);
				
				for(int token : tokensAndCount.keySet()){
					neededWords.add(token);
				}
					
				//read next document
				line = br.readLine();
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		|----------------------------------------------------------------------
		|  Go through the training data
		|----------------------------------------------------------------------
		|  Go through the training set and get only the counts 
		|  for the needed counts 
		|----------------------------------------------------------------------
		*/
		
		//vector of dictionaries: one map per class
		Vector<HashMap<String,Integer>> vectorOfDics= new Vector<HashMap<String,Integer>>(); 
		//map that links ClassName to array number of vectorOfDics
		HashMap<String,Integer> classPosition = new HashMap<String,Integer>();
		//map with label count
		HashMap<String,Integer> classCount = new HashMap<String,Integer>();
		//total doc count #(Y=*)
		int totalDocCount = 0;
		//doc count per label
		HashMap<String, Integer> labelDocCount = new HashMap<String, Integer>(); 
		//vocabulary
		HashSet<String> vocabulary = new HashSet<String>(); 
		
		
		//training result is piped here
		try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        
	        String line = br.readLine(); 
			while (line != null) {
				
				String[] featureAndCount = line.split("\\t");
				int count = Integer.parseInt(featureAndCount[1]);
				
				//parse feature
				String[] yAndW = featureAndCount[0].split("[=,]");
				String label = yAndW[1];
				//Y=*
				if(label.equals("*")){ 
					totalDocCount = count;
				}
				//Y=y
				else if(yAndW.length==2){
					labelDocCount.put(label, count);
				}
				//Y=y,W=?
				else{
					String token = yAndW[3];
					vocabulary.add(token);
					if(!classPosition.containsKey(label)){
						int assignedPos = vectorOfDics.size();
						classPosition.put(label, assignedPos);
						vectorOfDics.add(new HashMap<String,Integer>());
					}
					//Y=y,W=*
					if(token.equals("*")){
						classCount.put(label, count);
					}
					//Y=y,W=w
					else if (neededWords.contains(token)){
						vectorOfDics.elementAt(classPosition.get(label)).put(token, count);
					}	
				}				
				line = br.readLine();	
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/*
		|----------------------------------------------------------------------
		|  Naive Bayes Classification
		|----------------------------------------------------------------------
		|  Go through the testing set again, but now 
		|  for the classification part
		|----------------------------------------------------------------------
		*/

		//feedback parameters
		//int totalPredictions = 0;
		//int totalCorrectPredictions = 0;
		
		//Smoothing parameters  Theta_i = (x_i + a) / (N + ad)
		double d = 1.3*vocabulary.size();
		double a = 1;
		
		try {
	        BufferedReader br = new BufferedReader(new FileReader(testPath));
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
	        String line = br.readLine(); 
			while (line != null) {
			
				//read labels and words
				String[] labelsAndTokens = line.split("\\t",2);
				//HashSet<String> trueLabels = new HashSet<String>(Arrays.asList(labelsAndTokens[0].split(",")));
				HashMap<Integer, Integer> tokensAndCounts = tokenizeDoc(labelsAndTokens[1], VOCABULARY_SIZE);
				
				
				//compute likelihood for each label
				boolean firstLabel = true;
				for (Entry<String, Integer> entry : classPosition.entrySet()) {
					
					String currentLabel = entry.getKey();
					int currentPos = entry.getValue();
					
					int classTotalWords = classCount.get(currentLabel);
					int classTotalDocs = labelDocCount.get(currentLabel);
					HashMap<String,Integer> currentDic = vectorOfDics.get(currentPos);
					
					//initialize with logP(Y=y)  or actually log( #(Y=y) )
					//OBS: no need to divide by #(Y=*) since it is constant for all labels
					//the correction will be done later, to optimize processing time
					double logLikelihood = - Math.log(classTotalDocs);
					
					for(int token : tokensAndCounts.keySet()){
						double tokenCountInTestDoc = (double)tokensAndCounts.get(token);
						int tokenCount = currentDic.containsKey(token) ? currentDic.get(token) : 0;
						logLikelihood += tokenCountInTestDoc * ( Math.log((double)tokenCount + a) - Math.log((double)classTotalWords + a*d) );
					}
					
					//adjust probability:  make  log( #(Y=y,W=*) ) become logP(Y=y,W=*)
					logLikelihood += Math.log(totalDocCount);
					
					//print result for label
					if(firstLabel){
						firstLabel = false;
						bw.append(currentLabel+"\t"+String.valueOf(logLikelihood));
					}
					else{
						bw.append(","+currentLabel+"\t"+String.valueOf(logLikelihood));
					}
					
				}
				
				//next line
				bw.append("\n");
				
				//read next document
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
