package IndexingLucene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {
	private BufferedReader br;
	private FileInputStream instream_collection;
	private InputStreamReader is;

	public PreProcessedCorpusReader(String type) throws IOException {
		// Open the file in Path.ResultHM1 + type
		instream_collection = new FileInputStream(Path.ResultHM1 + type);
		is = new InputStreamReader(instream_collection);
		br = new BufferedReader(is);
	}

	public Map<String, Object> nextDocument() throws IOException {
		String docno = br.readLine();
		if (docno == null) {
			// Close the file streams when we reach the end of the file
			instream_collection.close();
			is.close();
			br.close();
			return null;
		}
		String content = br.readLine();
		Map<String, Object> doc = new HashMap<>();
		doc.put(docno, content.toCharArray());
		return doc;
	}
}