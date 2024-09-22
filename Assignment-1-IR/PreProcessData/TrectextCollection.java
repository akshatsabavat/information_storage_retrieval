package PreProcessData;

import java.io.IOException;
import Classes.Path;
import java.util.Map;

/**
 * This is for INFSCI-2140 in 2024
 *
 * Please add comments along with your code.
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file existing in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!

	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more


		return null;
	}

}
