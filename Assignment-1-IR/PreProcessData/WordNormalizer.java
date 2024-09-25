package PreProcessData;
import Classes.Stemmer;

/**
 * This is for INFSCI-2140 in 2024
 *
 * Please add comments along with your code.
 */
public class WordNormalizer {;
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] lowercase( char[] chars ) {
		// A simple for loop to update all the individual charecters in the array to lowercase
		for(int i = 0; i < chars.length; i++){
			chars[i] = Character.toLowerCase(chars[i]);
		}
		return chars;
	}

	public String stem(char[] chars)
	{
		Stemmer stemmer = new Stemmer();
		stemmer.add(chars, chars.length);
		stemmer.stem();
		return stemmer.toString();
	}

}
