package csvtest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class BiasAnalyser implements Serializable{

	private CSVLoader csvInterface;
	private List<Entry> clickedEntries;
	private int[] totalEntries;
	private Map<Integer, List<Entry>> rankToEntries;
	private Map<Integer, List<Entry>> setIDToEntries;
	private final int MAXRANK = Main.MAXRANK;

	public BiasAnalyser(CSVLoader csvInterface){

		this.csvInterface = csvInterface;
		this.clickedEntries = csvInterface.clickedEntries();
		this.totalEntries = csvInterface.totalEntriesByRank();

		// Create Maps of <each rank, <List of Entries>> and <each Set ID, <List of Entries>>
		this.rankToEntries = clickedEntries.stream().collect(Collectors.groupingBy(x -> x.getRank()));
		this.setIDToEntries = clickedEntries.stream().collect(Collectors.groupingBy(x -> x.getSet()));
	}

	public void runAllTests(){
//		this.calcCTRbyRank();
//		this.calcClicksBySetandRank();		
//		this.calcFirstClickbyRank();
//		this.calcFirstClickbySetAndRank();
		this.calcTimeStatsbyRank(10);
	}

	public void calcCTRbyRank(){
		System.out.println("\n\nCalculating Click Through Rate by Rank\n");
		ArrayList<String[]> data = new ArrayList<String[]>();
		data.add("Rank\tTimes Clicked\tTimes Delivered\tCTR\n".split("\t"));

		for(int rank : rankToEntries.keySet()){
			int rankSum = rankToEntries.get(rank).size();
			String outputStr = String.format("%d\t%d\t%s\t%.5f", rank, rankSum, Integer.toString(totalEntries[rank-1]), ((double)rankSum/(double)totalEntries[rank-1]));
			data.add(  outputStr.split("\t")  );
		}
		if(!csvInterface.writeCSV("CTRbyRank.csv", data, '\t')){
			System.out.println("Test unsuccessful\n");
		}
	}

	public void calcTimeStatsbyRank(int maxHoursIncluded){
		if(maxHoursIncluded < 1){
			System.out.println("\n\nTest failed - increase time examined\n");
			return;
		}
		System.out.println("\n\nCalculating Time Stats for Clicks by Rank\n");
		
		DescriptiveStatistics[] stats = new DescriptiveStatistics[MAXRANK];
		for(int x=0; x<MAXRANK; x+=1){
			stats[x] = new DescriptiveStatistics();
		}

		for(int rank : rankToEntries.keySet()){			
			for(Entry entry : rankToEntries.get(rank)){
				long time = Math.abs(ChronoUnit.MINUTES.between(entry.getTimeDelivered(), entry.getTimeClicked()));
				if(time > 60*maxHoursIncluded){
					continue;
				}
				stats[rank-1].addValue(time);
			}
		}

		ArrayList<String[]> data = new ArrayList<String[]>();
		data.add("Rank:\tAvg Time to Click (m)\tVariance\tStandard Deviation\tMedian\n".split("\t"));

		for(int x=0; x<MAXRANK; x+=1){
			//System.out.printf("Rank\t%d\tAvg Time to Click (m): %.4f\t\tVariance: %.4f\tStandard Deviation: %.4f\tMedian: %.4f\n", x+1, stats[x].getMean(), stats[x].getVariance(), stats[x].getStandardDeviation(), stats[x].getPercentile(50) );
			String outputStr = String.format("%d\t%.4f\t%.4f\t%.4f\t%.4f", x+1, stats[x].getMean(), stats[x].getVariance(), stats[x].getStandardDeviation(), stats[x].getPercentile(50) );
			data.add(  outputStr.split("\t")  );
		}

		if(!csvInterface.writeCSV("TimeStatsbyRank.csv", data, '\t')){
			System.out.println("Test unsuccessful\n");
		}

	}

	public void calcClicksBySetandRank(){
		System.out.println("\n\nCalculating Clicks Per Rank by Number of Clicks Per Set\n");

		int[][] clicksPerSetVersusRank = new int[MAXRANK][MAXRANK];

		for(int setID : setIDToEntries.keySet()){
			int clicksPerSetID = setIDToEntries.get(setID).size();
			
			if(clicksPerSetID > 0){
				for(Entry entry : setIDToEntries.get(setID)){
					clicksPerSetVersusRank[ clicksPerSetID-1 ][ entry.getRank()-1 ] += 1;
				}
			}	
		}

		//Gather and write data
		ArrayList<String[]> data = new ArrayList<String[]>();
		String s = "\t";
		for(int x=0; x<MAXRANK; x+=1){
			s += "Rank " + Integer.toString(x+1) + "\t";
		}
		data.add(s.split("\t"));
		for(int a=0; a<MAXRANK; a+=1){
			s = a+1 + " clicks per set\t";
			for(int b=0; b<MAXRANK; b+=1){
				s += clicksPerSetVersusRank[a][b] + "\t";
				//System.out.print(clicksPerSetVersusRank[a][b] + "\t");
			}
			data.add(s.split("\t"));
			//System.out.print("\n\n");
		}
		
		if(!csvInterface.writeCSV("ClicksbySetandRank.csv", data, '\t')){
			System.out.println("Test unsuccessful\n");
		}
	}

	public void calcFirstClickbyRank(){
		System.out.println("\n\nCalculating First Click by Rank\n\n");
		
		int[] firstRankClicked = new int[MAXRANK];

		for(int setID : setIDToEntries.keySet()){

			List<Entry> entries = setIDToEntries.get(setID);

			LocalDateTime setDelivered = entries.get(0).getTimeDelivered();
			Entry firstClicked = entries.get(0);
			long seconds = Math.abs(ChronoUnit.SECONDS.between(setDelivered, firstClicked.getTimeClicked()));

			for(Entry entry : entries){
				long timeDifference = Math.abs(ChronoUnit.SECONDS.between(setDelivered, entry.getTimeClicked()));
				if(timeDifference < seconds){
					firstClicked = entry;
					seconds = timeDifference;
				}
			}
			
			if(seconds > 5)
			firstRankClicked[firstClicked.getRank()-1] += 1;
		}

		//Gather and write data
		ArrayList<String[]> data = new ArrayList<String[]>();
		data.add("Rank\tTimes Clicked First\n".split("\t"));
		for(int i=0; i < firstRankClicked.length; i+=1){
			//System.out.printf("Rank: %d\t\tTimes Clicked First: %d\n\n", i+1, firstRankClicked[i]);
			data.add( String.format("%d\t%d", i+1, firstRankClicked[i]).split("\t")  );
		}
		if(!csvInterface.writeCSV("FirstClickbyRank.csv", data, '\t')){
			System.out.println("Test unsuccessful\n");
		}
		
	}
	
	public void calcFirstClickbySetAndRank(){
		System.out.println("\n\nCalculating First Click by Rank and Clicks per Set\n\n");
		
		int[][] firstClickBySetAndRank = new int[MAXRANK][MAXRANK];

		for(int setID : setIDToEntries.keySet()){
			int clicksPerSetID = setIDToEntries.get(setID).size();
			List<Entry> entries = setIDToEntries.get(setID);

			LocalDateTime setDelivered = entries.get(0).getTimeDelivered();
			Entry firstClicked = entries.get(0);
			long seconds = Math.abs(ChronoUnit.SECONDS.between(setDelivered, firstClicked.getTimeClicked()));

			for(Entry entry : entries){
				long timeDifference = Math.abs(ChronoUnit.SECONDS.between(setDelivered, entry.getTimeClicked()));
				if(timeDifference < seconds){
					firstClicked = entry;
					seconds = timeDifference;
				}
			}
			firstClickBySetAndRank[ clicksPerSetID-1 ][ firstClicked.getRank()-1 ] += 1;
		}

		//Gather and write data
		ArrayList<String[]> data = new ArrayList<String[]>();
		String s = "\t";
		for(int x=0; x<MAXRANK; x+=1){
			s += "Rank " + Integer.toString(x+1) + "\t";
		}
		data.add(s.split("\t"));
		for(int a=0; a<MAXRANK; a+=1){
			s = a+1 + " clicks per set\t";
			for(int b=0; b<MAXRANK; b+=1){
				s += firstClickBySetAndRank[a][b] + "\t";
			}
			data.add(s.split("\t"));
		}
		
		if(!csvInterface.writeCSV("FirstClickbySetandRank.csv", data, '\t')){
			System.out.println("Test unsuccessful\n");
		}
	}
	

	
}

