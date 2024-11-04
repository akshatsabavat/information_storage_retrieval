package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {

	protected MyIndexReader indexReader;
	private final double u = 2000; // variable parameter for Dirichlet smoothing

	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
	}

	/**
	 * Search for the topic information.
	 * The returned results (retrieved documents) should be ranked by the score
	 * (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN   The maximum number of returned document
	 * @return
	 */

	public List<Document> retrieveQuery(Query aQuery, int TopN) throws IOException {
		String[] termsInQuery = aQuery.GetQueryContent().split("\\s+");
		// A hash map for document scores relative to the document IDs so we can sort
		// later
		Map<Integer, Double> documentScores = new HashMap<>(); // Map structure <document ID, document Score>, and for
																// accumalation of score
		long collectionLength = indexReader.getCollectionLength(); // cl
		// iterate through the terms in the query to calculate the document score,
		// delivered by -> tf, cf, |D|, P(w|C) = cf/cl
		for (String term : termsInQuery) {
			// first retrieve the relevant documents that actuall contain these terms
			long termCollectionFrequency = indexReader.CollectionFreq(term); // cf
			int[][] postingsList = indexReader.getPostingList(term);
			if (postingsList != null) {
				// store the document IDs with the associated term frequencies and document
				// lengths
				// each posting = document Id [0], term freq [1]
				for (int[] posting : postingsList) {
					int documentID = posting[0];
					int termFreq = posting[1]; // tf
					int docLength = indexReader.docLength(documentID); // |D|

					// for each term I try to calculate the relevance score of p(ti|D) which applied
					// with DM is given as:
					// log[(tf + u * P(t|C))/(|D| + u)], where P(t|C) = cf/cl

					double score_t = Math
							.log((termFreq + u * (termCollectionFrequency / collectionLength)) / (docLength + u));
					// score_t = log[p(qi|D)]

					// Now we apply the idea of the Query likelihood model
					// we got the score_t which is the same as P(qi|D) for qi belongs to Q
					// so the get the document score we accumalate all the qi's and then we get the
					// relevance measure
					// P(Q|D) = p(q1|D) * p(q2|D) * .... * p(qn|D)

					// every time I come across, the same doc ID, it multiplies
					// remapping function is a sum cause P(Q|D) = Log[p(q1|D) * p(q2|D) * .... *
					// p(qn|D)] = log[p(q1|D)] + log[p(q2|D)] + .... + log[p(qn|D)]
					documentScores.merge(documentID, score_t, Double::sum);
				}
			}
		}
		return null;
	}

}