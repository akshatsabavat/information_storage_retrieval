package PreProcessData;

/**
 * This is for INFSCI-2140 in 2024
 *
 * TextTokenizer can split a sequence of text into individual word tokens.
 *
 * Please add comments along with your code.
 */
public class WordTokenizer {
	private char[] texts;
	private int currentPosition;

	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		this.texts = texts;
        this.currentPosition = 0;
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		StringBuilder wordToken = new StringBuilder();
		while(currentPosition < texts.length) {
			char charecterPointer = texts[currentPosition];
			if(Character.isLetterOrDigit(charecterPointer)) {
				wordToken.append(charecterPointer);
				currentPosition++;
			} else if (wordToken.length() > 0) {
				return wordToken.toString().toCharArray();
			} else {
				currentPosition++;
			}
		}
		
		if (wordToken.length() > 0){
			return wordToken.toString().toCharArray();
		} else {
			return null;
		}
	}

}
