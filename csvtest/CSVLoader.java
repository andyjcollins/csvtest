package csvtest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CSVLoader {

	DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s", Locale.ENGLISH);
	List<Entry> clickedEntries = new ArrayList<Entry>();
	private final int MAXRANK = Main.MAXRANK;
	private final int MAXCSVLINES = 1000000;
	String outputPath = ".\\";
	int[] totalEntriesByRank = new int[MAXRANK];

	public CSVLoader(String filename){
		parseCSV(openCSV(filename));
	}
	
	public CSVLoader(String filename, String outputPath){
		this.outputPath = outputPath;
		parseCSV(openCSV(filename));
	}
	
	private FileReader openCSV(String filename){
		FileReader file = null;
		try{
			file = new FileReader(filename); 
		}
		catch(FileNotFoundException e){
			System.out.printf("\n%s can not be read.\nPlease check that the path and filename are correct.\n"
					+ "Usage:\n\tjava -jar %s.jar {obligatory CSV input:path\\filename} {optional output path [this directory used by default]}\n\n", filename, "csvtest");
					
					
					
					/*, new java.io.File(Main.class.getProtectionDomain()
							  .getCodeSource()
							  .getLocation()
							  .getPath())
							  .getName());*/
			System.exit(0);
		}
		return file;
	}

	private boolean parseCSV(FileReader file){
		System.out.println("Parsing CSV\n");
		CSVReader reader = new CSVReader(file, '\t');
		
		String [] nextLine;
		try {
			//Get rid of header line in CSV
			//Modify this to establish the schema and validate data accordingly
			nextLine = reader.readNext();
			
			int count = 0;
			int c = 0;
			Field f = null;
			
			while ((nextLine = reader.readNext()) != null && count < MAXCSVLINES){
				
				boolean entryNotClicked = nextLine[f.TIME_CLICKED.ordinal()].equalsIgnoreCase("N");
				int rank = Integer.parseInt( nextLine[f.RANK_IN_RECOMMENDATION_SET.ordinal()] );
				assert(rank > 0 && rank <= MAXRANK): "Rank out of range";
				
				this.totalEntriesByRank[rank-1] += 1;
				count += 1;

				if(entryNotClicked){
					continue;
				}

				int set = Integer.parseInt( nextLine[f.RECOMMENDATION_SET_ID.ordinal()] );
				LocalDateTime timeDelivered = LocalDateTime.parse( nextLine[f.TIME_RESPONSE_DELIVERED.ordinal()], this.dateformat);
				LocalDateTime timeClicked = LocalDateTime.parse( nextLine[f.TIME_CLICKED.ordinal()], this.dateformat);	
				//Validate above parameters

				clickedEntries.add(new Entry(set, rank, timeDelivered, timeClicked));
			}
			System.out.println("Clicked entries: " + clickedEntries.size());
			reader.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean writeCSV(String filename, ArrayList<String[]> data, char separator){
		FileWriter file = null;
		try{
			file = new FileWriter(this.outputPath + "\\" + filename);
		}
		catch(IOException e){
			System.out.printf("Can not write to " + this.outputPath + "\\%s\nPlease check that the file is not open, space exists, and that you have permissions on that path etc\n",filename);
			return false;
		}
		
		System.out.printf("\tWriting to file: %s\n",filename);
		CSVWriter writer = new CSVWriter(file, ',', CSVWriter.NO_QUOTE_CHARACTER);
		writer.writeAll(data, false);
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.printf("\tFinished writing to file: %s\n",filename);
		
		return true;
	}
	
	
	public List<Entry> clickedEntries(){
		return this.clickedEntries;
	}

	public int[] totalEntriesByRank(){
		return this.totalEntriesByRank;
	}

	
}
