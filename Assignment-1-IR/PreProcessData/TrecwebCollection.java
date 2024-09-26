package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;
import java.io.IOException;

public class TrecwebCollection implements DocumentCollection {
	private BufferedReader reader;
	private String linePointer;
	// The content flag below was created as a way to save state and detect if the linePointer is in between the </DOCHDR> and </DOC> tag where the content of the document lies
	private Boolean inContentZoneFlag;

	public TrecwebCollection() throws IOException {
		reader = new BufferedReader(new FileReader(Path.DataWebDir));
		linePointer = reader.readLine();
	}

	// This function was created to remove the html tags when storing the web document contents
	private String removeHTMLTags(String content) {
	// AI-assisted: The exact regex expression was suggested by the AI tool: Perplexity, as I had multiple errors with mine
        return content.replaceAll("<[^>]+>", "").trim();
    }

	public Map<String, Object> nextDocument() throws IOException {
		try {
			// A simple check to see if linePointer had reached the end of the document
			if (linePointer == null){
				reader.close();
				return null;
			}

			// AI-assisted: The idea of using a string builder, initialy used string and took a long time store document content
			StringBuilder documentContentBuilder = new StringBuilder(); // accumalates and builds document content, preffered over concatinating document content over String
			String documentNumber = null; // string variable to store the document number
			inContentZoneFlag = false;


			while (linePointer != null) {
				if(linePointer.startsWith("<DOC>")) {
					// Resetting whenever I come across a new document
					documentNumber = null;
					documentContentBuilder.setLength(0);
				} else if (linePointer.startsWith("<DOCNO>")) {
					// the line below skips over the 7 charecters of the <DOCNO /> and then it begins extraction of the document number till it reaches the ending tag
					documentNumber = linePointer.substring(7, linePointer.indexOf("</DOCNO>")).trim(); // document number stored
				} else if (linePointer.startsWith("</DOCHDR>")) {
					inContentZoneFlag = true;
				} else if (inContentZoneFlag && !linePointer.startsWith("</DOC>")) {
					documentContentBuilder.append(linePointer).append("\n");
				} else if (linePointer.startsWith("</DOC>")) {
					if (documentNumber != null) {
										// Creates a new <Document Number, Documnet Content> structure and returns, if I have a document number
						Map<String, Object> document = new HashMap<>();
						// cleaning html tags from the document
						String cleanContent = removeHTMLTags(documentContentBuilder.toString());
						document.put(documentNumber, cleanContent.toCharArray());
						linePointer = reader.readLine(); // Move to the next document
						return document;
					}
				}

				linePointer = reader.readLine();
			}
			
		// Added Try and catch to read errors better
		} catch (IOException e) {
			System.err.println("Error reading Trecweb file: " + e.getMessage());
			return null;
		}

		return null;
	}

}
