import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class LR {

	public static void main(String[] args) {
		
		String[] existingLabels = {"ca","de","el","es","fr","ga","hr","hu","nl","pl","pt","ru","sl","tr"};
		int totalLabels = existingLabels.length;
		
		try {
			
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
	        
	        String line = br.readLine();
	        
			while (line != null) {
				
				
				//read labels and words
				String[] labelsAndTokens = line.split("\\t",2);
				String[] labels = labelsAndTokens[0].split(",");
				String[] tokens = labelsAndTokens[1].split("\\s+");				
				
				//for each label
				for(String label : labels){
					
					
				}//end for label
				
				
				line = br.readLine();	
			}
			
			bw.flush();
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
