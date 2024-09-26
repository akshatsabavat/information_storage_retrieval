package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Classes.Path;

import java.util.HashMap;
import java.util.Map;

public class TrectextCollection implements DocumentCollection {
	private BufferedReader reader; // for reading documents line by line
	private String linePointer; // points towards reading the line of any incomiung document

	public TrectextCollection() throws IOException {
	reader = new BufferedReader(new FileReader(Path.DataTextDir));
	linePointer = reader.readLine(); // starts the reading of the file
	}

	public Map<String, Object> nextDocument() throws IOException {
		try {
			// A simple check to see if linePointer had reached the end of the document
			if(linePointer == null) {
			reader.close();
			return null;
			}

			// AI-assisted: The idea of using a string builder, initialy used string and took a long time store document content
			StringBuilder documentContentBuilder = new StringBuilder(); // accumalates and builds document content, preffered over concatinating document content over String
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
					linePointer = reader.readLine(); //Move to the next document in the collection
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
