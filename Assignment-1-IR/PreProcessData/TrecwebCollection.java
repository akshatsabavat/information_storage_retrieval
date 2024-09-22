package PreProcessData;

import java.util.Map;
import Classes.Path;
import java.io.IOException;

/**
 * This is for INFSCI-2224 in 2024
 *
 * Please add comments along with your code.
 */
public class TrecwebCollection implements DocumentCollection {
	//you can add essential private methods or variables

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		// This constructor should open the file existing in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NT: the returned content of the document should be cleaned, all html tags should be removed.
		// NTT: remember to close the file that you opened, when you do not use it any more

		return null;
	}

}
