import java.util.List;

import Classes.*;
import IndexingLucene.*;
import Search.*;

/**
 * !!! YOU CANNOT CHANGE ANYTHING IN THIS CLASS !!!
 * 
 * This is for INFSCI 2140 in Fall 2023
 */
public class HW3Main {

	public static void main(String[] args) throws Exception {
		// Open index
		MyIndexReader ixreader = new MyIndexReader("trectext");

		// Initialize the MyRetrievalModel
		QueryRetrievalModel model = new QueryRetrievalModel(ixreader);
		// Extract the queries
		ExtractQuery queries = new ExtractQuery();

		long startTime = System.currentTimeMillis();
		while (queries.hasNext()) {
			Query aQuery = queries.next();
			System.out.println(aQuery.GetTopicId() + "\t" + aQuery.GetQueryContent());
			// conduct retrieval on the index for each topic, and return top 25 documents
			List<Document> results = model.retrieveQuery(aQuery, 20);
			if (results != null) {
				int rank = 1;
				for (Document result : results) {
					System.out.println(aQuery.GetTopicId() + " Q0 " + result.docno() + " " + rank + " " + result.score()
							+ " MYRUN");
					rank++;
				}
			}
		}
		long endTime = System.currentTimeMillis(); // end time of running code
		System.out.println("\n\nsearch time for 4 queries: " + (endTime - startTime) / 60000.0 + " min");
		ixreader.close();
	}

}
