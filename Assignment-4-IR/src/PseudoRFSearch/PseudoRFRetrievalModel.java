package PseudoRFSearch;

import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
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
		System.out.println("Initiating Creation of TokenRF Map..");
		HashMap<String, Double> TokenRFScore = GetTokenRFScore(aQuery, feedBackDocuments);

		// now getting the documents from the old model all together, using query not just topk
		Integer collectionSize = ixreader.getDocumentCollectionSize();
		System.out.println("Processing all documents from the previous model..");
		List<Document> originalDocumentList = previousModel.retrieveQuery(aQuery, collectionSize);


		// (3) & (4) Calculating the document scores with the RF scores of the query tokens and ranking them
		System.out.println("Intiating construction of psuedo RF scored documents map..");
		List<Document> pseudoRFScoredDocumentList = new ArrayList<Document>();
		for(Document document : originalDocumentList) {
			double QueryLikelihood = calculateRFScore(aQuery, document, TokenRFScore, alpha);
			document.setScore(QueryLikelihood); // set the score we get for that document query combination
			pseudoRFScoredDocumentList.add(document); // add it to the new list
		}

		// Sort documents by new scores in descending order
		pseudoRFScoredDocumentList.sort(new Comparator<Document>() {
            @Override
            public int compare(Document d1, Document d2) {
                return Double.compare(d2.score(), d1.score());
            }
        });

		// Now getting topK documents

		System.out.println(pseudoRFScoredDocumentList);

        return pseudoRFScoredDocumentList.subList(0, Math.min(TopN, pseudoRFScoredDocumentList.size()));
	}


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

		// first we fetch of the contents from each feedback document, but rather than content which may be too much we just require the terms that can be fetched from the vectors
		// then we use each token of the query and build a probability score of each token with respect to the content in the feedback document
		System.out.println("Processing feedback documents...");
		for(Document doc : feedbackDocuments) {
			Terms terms = ixreader.getTermVector(doc.docid(), "CONTENT");
			if(terms != null) {
				TermsEnum termsEnum = terms.iterator();
				BytesRef term;
				while ((term = termsEnum.next()) != null) {
					String token = term.utf8ToString();
					long freq = (int) termsEnum.totalTermFreq();
					TokenFeedBackRelation.put(token, (int) (TokenFeedBackRelation.getOrDefault(token, 0) + freq));
					totalTermCount += freq;
				}
			}
//			String docContent = ixreader.getDocContent(doc.docid());
//			String[] tokens = docContent.split("\\s+"); // tokens created
//
//			for(String token : tokens) {
//				// relation here token ---> [token count value in the entire feedback list content]
//				TokenFeedBackRelation.put(token, TokenFeedBackRelation.getOrDefault(token, 0) + 1);
//				totalTermCount++; // keep incrementing
//			}
		}

		// getting probability score for each token
		long collectionLength = ixreader.getCollectionLength();

		// calculating the score or probability for each token in query
		System.out.println("Calculating probability scores");
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

		System.out.println("Token RF Map built");
		System.out.println(TokenRFScore);

		return TokenRFScore;
	}

	/**
	 * Function to calculate doc term prob with Dirichlet Smoothing, using formula, for one document and not multiple
	 *
	 * @param doc Document
	 * @param token Query token
	 * @return Probability of term
	 */

	private double calculateDocTermProb(Document doc, String token) throws Exception {
		long collectionLength = ixreader.getCollectionLength();
		long tokenCollectionFrequency = ixreader.getTermCollectionFrequency(token);
		int documentLength = ixreader.docLength(doc.docid());

		Terms terms = ixreader.getTermVector(doc.docid(), "CONTENT");
		long tokenDocumentFrequency = 0;
		if (terms != null) {
			TermsEnum termsEnum = terms.iterator();
			BytesRef term;
			while ((term = termsEnum.next()) != null) {
				if (term.utf8ToString().equals(token)) {
					tokenDocumentFrequency = termsEnum.totalTermFreq();
					break;
				}
			}
		}

		return (tokenDocumentFrequency + mu * ((double) tokenCollectionFrequency / collectionLength)) / (documentLength + mu);
	}

	/**
	 * The final formula to calculate that relevance feed back score P(Q|M’D)=αP(Q|MD)+(1-α)P(Q|F)
	 *
	 * @param Q The original query
	 * @param doc Target document
	 * @param TokenRFScore Token probabilities from feedback documents
	 * @param alpha Interpolation parameter
	 * @return Query likelihood score
	 */

	private double calculateRFScore(Query Q, Document doc, HashMap<String, Double> TokenRFScore, double alpha) throws Exception {
		double QL = 1.0; // QL is query liklihood, initializing to one
		String[] queryTokens = Q.GetQueryContent().split("\\s+");

		for(String queryToken : queryTokens) {
			double docTermProb = calculateDocTermProb(doc, queryToken); // P(Q|MD)
			double feedBackTermProb = TokenRFScore.get(queryToken); // P(Q|F)
			// P(Q|M’D)= αP(Q|MD)+(1-α)P(Q|F)
			double tokenRelevanceScore = alpha * docTermProb + (1-alpha) * feedBackTermProb;

			// using naive bayes assumption
			// for Qt belongs to Q {t1, t2, t3 .... tn}
			// P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
			QL *= tokenRelevanceScore;
		}
		return QL;
	}

}
