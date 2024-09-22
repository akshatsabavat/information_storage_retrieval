import java.io.FileWriter;
import java.util.Map;

import Classes.Path;
import PreProcessData.*;

/**
 * !!! YOU CANNOT CHANGE ANYTHING IN THIS CLASS !!! This is for INFSCI 2140 in
 * Fall 2024
 *
 */
public class HW1Main {

	public static void main(String[] args) throws Exception {
		// main entrance
		long startTime=System.currentTimeMillis(); //star time of running code
		HW1Main hm1 = new HW1Main();
		hm1.PreProcess("trectext");//1.96min
		long endTime=System.currentTimeMillis(); //end time of running code
		System.out.println("Time to index text corpus: "+(endTime-startTime)/60000.0+" min");
		startTime=System.currentTimeMillis(); //star time of running code
		hm1.PreProcess("trecweb");//1.39min
		endTime=System.currentTimeMillis(); //end time of running code
		System.out.println("Time to index web corpus: "+(endTime-startTime)/60000.0+" min");
	}

	public void PreProcess(String typeCollection) throws Exception {
		// Loading the collection file and initiate the DocumentCollection class
		DocumentCollection corpus;
		if (typeCollection.equals("trectext"))
			corpus = new TrectextCollection();
		else
			corpus = new TrecwebCollection();

		// loading stopword list and initiate the StopWordRemover and WordNormalizer class
		StopWordRemover stopwordRemoverObj = new StopWordRemover();
		WordNormalizer normalizerObj = new WordNormalizer();

		// initiate the BufferedWriter to output result
		FileWriter wr = new FileWriter(Path.ResultHM1 + typeCollection);

		// initiate a doc object, which can hold document number and document content of a document
		Map<String, Object> doc = null;

		// process the corpus, document by document, iteractively
		int count=0;
		while ((doc = corpus.nextDocument()) != null) {
			// load document number of the document
			String docno = doc.keySet().iterator().next();

			// load document content
			char[] content = (char[]) doc.get(docno);

			// write docno into the result file
			wr.append(docno + "\n");

			// initiate the WordTokenizer class
			WordTokenizer tokenizer = new WordTokenizer(content);

			// initiate a word object, which can hold a word
			char[] word = null;

			// process the document word by word iteratively
			while ((word = tokenizer.nextWord()) != null) {
				// each word is transformed into lowercase
				word = normalizerObj.lowercase(word);

				// filter out stopword, and only non-stopword will be written
				// into result file
				if (!stopwordRemoverObj.isStopword(word))
					wr.append(normalizerObj.stem(word) + " ");
					//stemmed format of each word is written into result file
			}
			wr.append("\n");// finish processing one document
			count++;
			if(count%10000==0)
				System.out.println("Finish "+count+" docs");
		}
		System.out.println("Totaly document count:  "+count);
		wr.close();
	}
}
