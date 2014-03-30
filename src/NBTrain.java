import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map.Entry;


public class NBTrain {
	
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
		
		if(args.length < 1){
			System.err.println("vocabulary size not provided");
			System.exit(0);
		}
		
		int VOCABULARY_SIZE = Integer.parseInt(args[0]); 
		
		String[] existingLabels = {"nl","el","ru","sl","pl","ca","fr","tr","hu","de","hr","es","ga","pt"};
		
		//hashmap that links ClassName to array number of vectorOfDics
		HashMap<String,Integer> classPosition = new HashMap<String,Integer>();
		
		//total documents
		int totalDocCount = 0;
		int[] totalLabelDocs = new int[14];
		
		//total words
		int[] totalLabelWords = new int[14];
		
		//populate structures
		int assignedPos=0;
		for(String l : existingLabels){
			classPosition.put(l, assignedPos);
			assignedPos++;
		}
		
		// +--------------------------------------------------------
		// |     READING AND COUNTING (TRAINING)
		// |--------------------------------------------------------
		// |  To increase performance, we won't keep track
		// |  of Y=*  or any Y=y|W=* 
		// | we will only count them once data is consolidated
		// +--------------------------------------------------------
		
		//long startTime = System.currentTimeMillis(); 
		try {
			
			//String inputPath = args[0];
	        //BufferedReader br = new BufferedReader(new FileReader(inputPath));
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
	        
	        String line = br.readLine();
	        
			while (line != null) {
				
				//read labels and words
				String[] labelsAndTokens = line.split("\\t",2);
				String[] labels = labelsAndTokens[0].split(",");
				HashMap<Integer, Integer> tokensWithCounts = tokenizeDoc(labelsAndTokens[1], VOCABULARY_SIZE);				
				
				//for each label
				for(String label : labels){
					
					int labelPos = classPosition.get(label);
					
					//count of docs per label
					totalLabelDocs[labelPos]++;
					//count of words per label
					totalLabelWords[labelPos]++;
					
					for(int token : tokensWithCounts.keySet()){
						bw.append("Y="+label+",W="+String.valueOf(token)+"\t"+String.valueOf(tokensWithCounts.get(token))+"\n");
					}
					
				}//end for label
				
				line = br.readLine();	
			}
			
			bw.flush();
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//print label counters
		for(Entry<String,Integer> e : classPosition.entrySet()){
			String label = e.getKey();
			int labelPos = e.getValue();
			
			//Y=y, W=*
			int totalWordsInLabel = totalLabelWords[labelPos];
			System.out.println("Y="+label+",W=*\t"+totalWordsInLabel);
			//Y=y
			int totalDocsInLabel = totalLabelDocs[labelPos];
			System.out.println("Y="+label+"\t"+totalDocsInLabel);
			totalDocCount += totalDocsInLabel;
		}

		//total doc counter Y=*
		System.out.print("Y=*\t"+totalDocCount);
		
		//long estimatedTime = System.currentTimeMillis() - startTime;
		//System.out.println("Training Time: "+estimatedTime);
		
	}



}
