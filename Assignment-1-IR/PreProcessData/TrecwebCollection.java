package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;
import java.io.IOException;

/**
 * This is for INFSCI-2224 in 2024
 *
 * Please add comments along with your code.
 */
public class TrecwebCollection implements DocumentCollection {
	private BufferedReader reader;
	private String linePointer;
	private Boolean inContentZoneFlag;

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		reader = new BufferedReader(new FileReader(Path.DataWebDir));
		linePointer = reader.readLine();
	}

	// This function was created to remove the html tags when storing the web document contents
	private String removeHTMLTags(String content) {
        return content.replaceAll("<[^>]+>", "").trim();
    }

	// private Strin removeHTMLTags()

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		try {
			// Reaching end of document collection check
			if(linePointer == null){
				reader.close();
				return null;
			}

			StringBuilder documentContentBuilder = new StringBuilder();
			String documentNumber = null;
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
						Map<String, Object> document = new HashMap<>();
						String cleanContent = removeHTMLTags(documentContentBuilder.toString());
						document.put(documentNumber, cleanContent.toCharArray());
						linePointer = reader.readLine(); // Move to the next document
						return document;
					}
				}

				linePointer = reader.readLine();
			}

			// if (documentNumber != null) {
            //     Map<String, Object> document = new HashMap<>(); 
            //     document.put(documentNumber, documentContentBuilder.toString().toCharArray());
            //     System.out.println("Processing final document: " + documentNumber);
            //     return document;
            // }
			
		} catch (IOException e) {
			System.err.println("Error reading Trecweb file: " + e.getMessage());
			return null;
		}

		return null;
	}

}
