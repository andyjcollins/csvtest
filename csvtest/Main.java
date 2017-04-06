package csvtest;

public class Main {
	
	public static final int MAXRANK = 15;
	
	public static void main(String[] args){
		String inputPath = "";
		String outputPath = "";
		CSVLoader csvInterface = null;
		
		if(args.length == 1){
			inputPath = args[0];
			csvInterface = new CSVLoader(inputPath);
		}
		else if(args.length == 2){
			inputPath = args[0];
			outputPath = args[1];
			csvInterface = new CSVLoader(inputPath, outputPath);
		}
		else{
			System.out.printf("Problem with program paramters\n"
					+ "Usage:\n\tjava -jar %s.jar {obligatory CSV input:path\\filename} {optional output path [this directory used by default]}\n\n", "csvtest");
			System.exit(0);
		}
		//System.out.printf("File path: %s\n", path);
		
		//test path
		//test file character type
		//test that file has correct extension
		//test file exists
		
		//CSVLoader csvInterface = new CSVLoader("C:\\Users\\Andrew\\Documents\\mdl_recysys_dataset\\rec.csv","c:\\Users\\andrew");
		BiasAnalyser analyser = new BiasAnalyser(csvInterface);
		analyser.runAllTests();
		
	}
}
