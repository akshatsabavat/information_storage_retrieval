package PreProcessData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import Classes.*;

public class StopWordRemover {
	private Set<String> stopwords;

	public StopWordRemover() throws IOException {
		stopwords = new HashSet<>(); // Decided to use a hashset so the look up time is O(1)
		fetchStopWords();
	}

	// Method for fetching stop words from document, and populate the Hashset
	private void fetchStopWords() throws IOException {
		// Declared reader to read the file line by line
		BufferedReader reader = new BufferedReader(new FileReader(Path.StopwordDir));
		String linePointer = reader.readLine(); // Creating a pointer to keep track of line
		try {
			while(linePointer != null) {
				stopwords.add(linePointer.trim().toLowerCase()); // Adds the lowercase stop words to the hashset
				linePointer = reader.readLine(); // iterates through the document reading line by line
			}
			reader.close();
		
		// Added try catch to improve error reading
		} catch (IOException e) {
			System.err.println("Error reading Stop Word file: " + e.getMessage());	
		}
	}

	public boolean isStopword( char[] word ) {
		String stopWordCandidate = new String(word).toLowerCase(); // converting charecters to a word string 
		return stopwords.contains(stopWordCandidate); // returns true or false the stopwords hashset contains the input word
	}
}
