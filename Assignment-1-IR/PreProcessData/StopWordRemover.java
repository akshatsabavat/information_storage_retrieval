package PreProcessData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import Classes.*;

/**
 * This is for INFSCI-2140 in 2024
 *
 * Please add comments along with your code.
 */

public class StopWordRemover {
	private Set<String> stopwords;

	public StopWordRemover() throws IOException {
		stopwords = new HashSet<>();
		fetchStopWords();
	}

	private void fetchStopWords() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(Path.StopwordDir));
		String linePointer = reader.readLine();
		try {
			while(linePointer != null) {
				stopwords.add(linePointer.trim().toLowerCase());
				linePointer = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading Stop Word file: " + e.getMessage());	
		}
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		String wordToCheck = new String(word).toLowerCase();
		return stopwords.contains(wordToCheck);
	}
}
