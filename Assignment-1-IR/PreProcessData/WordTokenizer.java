package PreProcessData;

public class WordTokenizer {
	private char[] texts;
	// declared a position variable to go charecter by charecter
	private int currentPosition;

	public WordTokenizer( char[] texts ) {
		this.texts = texts;
        this.currentPosition = 0;
	}

	public char[] nextWord() {
		StringBuilder wordToken = new StringBuilder(); // Using string builder over string for same reasons done when reading document files
		while(currentPosition < texts.length) {
			// initializing the charecter pointer
			char charecterPointer = texts[currentPosition];
			// a check to see if each input from the array we iterate is a valid charecter or a digit
			if(Character.isLetterOrDigit(charecterPointer)) {
				wordToken.append(charecterPointer);
				currentPosition++;
			} else if (wordToken.length() > 0) {
				return wordToken.toString().toCharArray();
			} else {
				currentPosition++;
			}
		}

		// AI-assisted: Handling the last word of the text, previously missed this step and it haulted the entire code returning garbage values
		if (wordToken.length() > 0){
			return wordToken.toString().toCharArray();
		} else {
			return null;
		}
	}

}
