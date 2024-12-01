package PseudoRFSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.*;
//import Search.*;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private static final double mu = 2000.0; // mu value for Dirichlet smoothing parameter

	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}

	/**
	 * Search for the topic with pseudo relevance feedback in 2023 Fall assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {
		// (1) Using the original retrieval model to get TopK documents, which will be regarded as feedback documents
		QueryRetrievalModel previousModel = new QueryRetrievalModel(ixreader); // getting my old model
		List<Document> feedBackDocuments = previousModel.retrieveQuery(aQuery, TopK); // fetching the documents from the old model to be used as feedback

		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents

		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')


		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore = GetTokenRFScore(aQuery,feedBackDocuments);


		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();

		return results;
	}

//	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
//	{
//		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
//		// use Dirichlet smoothing
//		// save <token, score> in HashMap TokenRFScore, and return it
//		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
//
//		return TokenRFScore;
//	}

	/**
	 * The below function calculates probabilities of tokens of a query in a feedback document (GetTokenRFScore)
	 *
	 * @param Q The original query user is sending
	 * @param feedbackDocuments The old doc list as a feedback list
	 * @return Returns the query score as double
	 */

	private HashMap<String, Double> GetTokenRFScore(Query Q, List<Document> feedbackDocuments) throws Exception {
		// the map below contains the tokens in the query and their probability scores
		// token --> [P(w|D)]
		HashMap<String, Double> TokenRFScore = new HashMap<>();
		// Token Feed Back relation below highlights the frequency of the terms in the feedback documents all combined
		HashMap<String, Integer> TokenFeedBackRelation = new HashMap<>();
		long totalTermCount = 0; // for formula

		// first we fetch of the contents from each feedback document
		// then we use each token of the query and build a probability score of each token with respect to the content in the feedback document
		for(Document doc : feedbackDocuments) {
			String docContent = ixreader.getDocContent(doc.docid());
			String[] tokens = docContent.split("\\s+"); // tokens created

			for(String token : tokens) {
				// relation here token ---> [token count value in the entire feedback list content]
				TokenFeedBackRelation.put(token, TokenFeedBackRelation.getOrDefault(token, 0) + 1);
				totalTermCount++; // keep incrementing
			}
		}

		// getting probability score for each token
		long collectionLength = ixreader.getCollectionLength();

		// calculating the score or probability for each token in query
		String[] queryTokens = Q.GetQueryContent().split("\\s+");
		for(String queryToken: queryTokens) {
			// now we can get the token freq of the query token cause we constructed the entire TokenFeedBack map or term dictionary before
			Integer tokenFreqFeedback = TokenFeedBackRelation.getOrDefault(queryToken, 0); // default it to 0 in the event we don't find the token
			long tokenFreqCollection = ixreader.getTermCollectionFrequency(queryToken);

			// P(w|D) = [totalFreqFeedback + mu x (termFreqCollection/collectionLength)] / (totalTermCount + mu)
			double tokenProbability = (tokenFreqFeedback + mu * ((double) tokenFreqCollection /collectionLength)) / (totalTermCount + mu);

			// add to tokenRFScore
			TokenRFScore.put(queryToken, tokenProbability);
		}

		return TokenRFScore;
	}

	/**
	 * Function to calculate doc term prob with Dirichlet Smoothing, using formula, for one document and not multiple
	 *
	 * @param doc Document
	 * @param token Query token
	 * @return Probability of term
	 */

	private double calculateDocTermProb(Document doc, String token, long collectionLength) throws Exception {
		Integer tokenDocumentFrequency =  ixreader.getTermDocumentFrequency(token); // number of token counts in the document content
		long tokenCollectionFrequency = ixreader.getTermCollectionFrequency(token); // number of token counts in the collection content
		int documentLength = ixreader.docLength(doc.docid()); // total number of terms in document
        return (tokenDocumentFrequency + mu * ((double) tokenCollectionFrequency/collectionLength)) / (documentLength + mu);
	}

}
