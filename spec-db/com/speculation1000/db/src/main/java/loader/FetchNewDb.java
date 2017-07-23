package loader;

public class FetchNewDb {
	
	private long lastUpdateDate;

	public FetchNewDb(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public void fetchNewRecords(){
		long fetchDate = lastUpdateDate + 24 * 60 * 60;
		
		//if [{"date":0,"high":0,"low":0,"open":0,"close":0,"volume":0,"quoteVolume":0,"weightedAverage":0}]
		//TODO WHEN GET BACK
		
		//if it doesnt equal above response
		
		//insert em alllll
		
		//no insert.....
	}

}
