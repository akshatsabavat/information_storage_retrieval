package IndexingLucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * A class for reading your index.
 */
public class MyIndexReader {
	protected File dir;
	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	
	public MyIndexReader( String dataType ) throws IOException {
		if (dataType.equals("trectext")) {
			directory = FSDirectory.open(Paths.get(Classes.Path.IndexTextDir));  
		} else {
			directory = FSDirectory.open(Paths.get(Classes.Path.IndexWebDir)); 
		}
		ireader = DirectoryReader.open(directory);
		isearcher = new IndexSearcher(ireader);
	}
	
	/**
	 * Get the (non-negative) integer docid for the requested docno.
	 * If -1 returned, it indicates the requested docno does not exist in the index.
	 * 
	 * @param docno
	 * @return
	 * @throws IOException 
	 */
	public int getDocid( String docno ) throws IOException {
		// you should implement this method.
		Query query = new TermQuery(new Term("DOCNO", docno));
		TopDocs tops= isearcher.search(query,1);
		return tops.scoreDocs[0].doc;
	}
	
	/**
	 * Retrive the docno for the integer docid.
	 * 
	 * @param docid
	 * @return
	 * @throws IOException 
	 */
	public String getDocno( int docid ) throws IOException {
		// you should implement this method.
		Document doc = ireader.document(docid);
		return (doc==null)?null:doc.get("DOCNO");
	}

	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] getPostingList( String token ) throws IOException {
		// you should implement this method.
		Term tm = new Term("CONTENT", token);
		int df = ireader.docFreq(tm);
		if(df==0)
			return null;
		Query query = new TermQuery(tm);
		TopDocs tops= isearcher.search(query,df);		
		ScoreDoc[] scoreDoc = tops.scoreDocs;
		int[][] posting = new int[df][];
		int ix = 0;
		Terms vector;
		TermsEnum termsEnum;
		BytesRef text;
		for (ScoreDoc score : scoreDoc){
			int id = score.doc;
			int freq=0;
			vector = ireader.getTermVector(id, "CONTENT");
			termsEnum = vector.iterator();
			while ((text = termsEnum.next()) != null) {
			    if(text.utf8ToString().equals(token))
			    	freq+= (int) termsEnum.totalTermFreq();
			}
			posting[ix] = new int[] { id, freq };
			ix++;
		}
		return posting;
	}
	
	/**
	 * Return the number of documents that contains the token.
	 * 
	 * @param token
	 * @return
	 */
	public int DocFreq( String token ) throws IOException {
		Term tm = new Term("CONTENT", token);
		int df = ireader.docFreq( tm );
		return df;
	}
	
	/**
	 * Return the total number of times the token appears in the collection.
	 * 
	 * @param token
	 * @return
	 */
	public long CollectionFreq( String token ) throws IOException {
		// you should implement this method.
		Term tm = new Term("CONTENT", token);
		long ctf=ireader.totalTermFreq(tm);
		return ctf;
	}
	
	/**
	 * Get the length of the requested document. 
	 * 
	 * @param docid
	 * @return
	 * @throws IOException
	 */
	public int docLength( String docid ) throws IOException {
		int doc_length = 0;
		Terms vector = ireader.getTermVector( Integer.parseInt(docid), "CONTENT" );
		TermsEnum termsEnum = vector.iterator();
		BytesRef text;
		while ((text = termsEnum.next()) != null) {
			doc_length+= (int) termsEnum.totalTermFreq();
		}
		return doc_length;
	}

	// Added method for getting collection length
	public long getCollectionLength() throws IOException {
		// uses the lucene library to get the total collection length required for
		// P(w|C)
		return ireader.getSumTotalTermFreq("CONTENT");
	}

	// Added new method of getting the contents of a document with respect to the document ID
	/**
	 * Get document content with respect to doc id
	 *
	 * @param docid
	 * @return String doc content
	 * @throws IOException
	 */
	public String getDocContent(String docid) throws IOException {
		Document D = ireader.document(Integer.parseInt(docid));
		return D.get("CONTENT");
	}

	public Terms getTermVector(String docid, String field) throws IOException {
		return ireader.getTermVector(Integer.parseInt(docid), field);
	}

	// Added new method of getting the term collection frequency
	/**
	 * Get term collection frequency
	 * @param term
	 * @return long collection frequency
	 * @throws IOException
	 */
	public long getTermCollectionFrequency(String term) throws  IOException {
		Term t = new Term("CONTENT", term); // creating a term object to pass through function
		return ireader.totalTermFreq(t);
	}

	// Added new method of getting document term frequency
	/**
	 * Get term document frequency
	 * @param term
	 * @return String doc content
	 * @throws IOException
	 */
	public Integer getTermDocumentFrequency(String term) throws IOException {
		Term t = new Term("CONTENT", term);
		return ireader.docFreq(t);
	}

	// Method to return documents in collection the size
	public Integer getDocumentCollectionSize() throws IOException {
		return ireader.numDocs();
	}
	
	public void close() throws IOException {
		// you should implement this method when necessary
		ireader.close();
		directory.close();
	}
	
}
