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
					+ "Usage:\n\tjava -jar %s.jar {obligatory CSV input:path\\filename} {optional output path [this directory used by default]}\n\n", "[progname]");
			System.exit(0);
		}
		
		BiasAnalyser analyser = new BiasAnalyser(csvInterface);
		analyser.runAllTests();
		
	}
}
