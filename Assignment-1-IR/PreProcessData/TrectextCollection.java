package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Classes.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * This is for INFSCI-2140 in 2024
 *
 * Please add comments along with your code.
 */
public class TrectextCollection implements DocumentCollection {
	private BufferedReader reader; // for reading documents line by line
	private String linePointer; // points towards reading the line of any incomiung document

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
	reader = new BufferedReader(new FileReader(Path.DataTextDir));
	linePointer = reader.readLine(); // starts the reading of the file
	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		try {
			// Reaching end of document check
			if(linePointer == null) {
			reader.close();
			return null;
			}

			StringBuilder documentContentBuilder = new StringBuilder(); // accumalates and builds document content
			String documentNumber = null; // string variable to store the document number associated with the documnet content

		while(linePointer != null) {
			if (linePointer.startsWith("<DOC>")) {
				// Resetting whenever I come across a new document
				documentNumber = null;
				documentContentBuilder.setLength(0);

			} else if (linePointer.startsWith("<DOCNO>")){
				// the line below skips over the 7 charecters of the <DOCNO /> and then it begins extraction of the document number till it reaches the ending tag
				documentNumber = linePointer.substring(7, linePointer.indexOf("</DOCNO>")).trim(); // document number stored
			} else if (linePointer.startsWith("</DOC>")) {
				// Creates a new <Document Number, Documnet Content> structure and returns, if I have a document number
				if (documentNumber != null) {
					Map<String, Object> document = new HashMap<>(); 
					document.put(documentNumber, documentContentBuilder.toString().toCharArray());
					return document;
				}
			} else {
				if (documentNumber != null) {
					// Keeps building the document content, as the linePointer moves through the document
					documentContentBuilder.append(linePointer).append(" ");
				}
			}
			linePointer = reader.readLine();
		}
        // Added Try and catch to read errors better
		} catch (IOException e) {
			System.err.println("Error reading Trectext file: " + e.getMessage());
			return null;
		}
		return null;
	}

}
