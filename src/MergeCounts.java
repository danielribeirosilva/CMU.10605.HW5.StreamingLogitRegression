import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MergeCounts {

	
	public static void main(String[] args) {
		
        try {
        	
        	//String inputPath = args[0];
            //BufferedReader br = new BufferedReader(new FileReader(inputPath));
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
        	//for first line
        	String line = br.readLine();
        	String[] labelAndCount = line.split("\\t");
			String lastLabel = labelAndCount[0];
			int accumulatedCount = Integer.parseInt(labelAndCount[1]);
			
			//for remaining lines
			line = br.readLine();
			
			int currentCount;
			String currentLabel;
			
			while (line != null) {
				labelAndCount = line.split("\\t");
				currentLabel = labelAndCount[0];
				currentCount = Integer.parseInt(labelAndCount[1]);
				
				if(currentLabel.equals(lastLabel)){
					accumulatedCount += currentCount;
				}
				else{
					System.out.println(lastLabel+"\t"+accumulatedCount);
					accumulatedCount=currentCount;
				}
				
				lastLabel = currentLabel;
				
				line = br.readLine();
			}
			
			System.out.println(lastLabel+"\t"+accumulatedCount);
			br.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
