package PreProcessData;

import java.util.Map;
import java.io.IOException;


/**
 * This is for INFSCI-2140 in 2024
 *
 * DocumentCollection is an interface for reading individual document files from
 * a collection file.
 *
 * Please add comments along with your code.
 */
public interface DocumentCollection {

	/**
	 * Try to read and return the next document stored in the collection.
	 * Note : If it is the end of the collection file, return null.
	 * Each document should be stored as a Map, the key is the document number, while the value is document content
	 * so that you can get the document's number by calling doc.keySet().iterator().next()
	 * and document's content by map.get(document's number)
	 *
	 * @return The next document stored in the collection; or null if it is the end of the collection file.
	 */
	public abstract Map<String,Object> nextDocument() throws IOException;

}
