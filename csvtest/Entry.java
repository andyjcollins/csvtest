package csvtest;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Entry implements Serializable{

	private Integer set;
	private Integer rank;
	private Integer numberOfRecommendations;
	private LocalDateTime timeClicked;
	private LocalDateTime timeDelivered;
	
	public Entry(int set, int rank, LocalDateTime timeDelivered, LocalDateTime timeClicked){
		this.set = set;
		this.rank = rank;
		this.timeClicked = timeClicked;
		this.timeDelivered = timeDelivered;
		//assert(timeClicked.isAfter(timeDelivered) || timeClicked.isEqual(timeDelivered)) : 
			//  "Entry constructor - Set ID " + set+ ": timeClicked " + timeClicked.toString() + " should be after timeDelivered " + timeDelivered.toString() + "  - check parameters and CSV";
	}
	
	public int getSet(){
		return set;
	}
	
	public int getRank(){
		return rank;
	}
	
	public int getClicked(){
		return 1;
	}
	
	public LocalDateTime getTimeClicked(){
		return timeClicked;
	}
	
	public LocalDateTime getTimeDelivered(){
		return timeDelivered;
	}
	
	public void printEntry(){
		System.out.println("SetID:" + this.set + "  Rank:" + this.rank + "  Time clicked:" + this.timeClicked.toString() + "  Time Delivered:" + this.timeDelivered.toString() + "\n\n");
	}
	
}
