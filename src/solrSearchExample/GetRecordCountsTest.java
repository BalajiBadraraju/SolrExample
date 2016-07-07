package solrSearchExample;

import java.util.ArrayList;

public class GetRecordCountsTest {

	public static void main(String[] args) {

		ArrayList<String> datesToBeProcessed = new ArrayList<String>();
		datesToBeProcessed.add("10/01/2014");
//		datesToBeProcessed.add("03/02/2014");
//		datesToBeProcessed.add("03/03/2014");
//		datesToBeProcessed.add("03/04/2014");
//		datesToBeProcessed.add("03/05/2014");

		GetRecordsSolrCollectionCount.getRecordsCountInCollection(datesToBeProcessed);

	}

}
