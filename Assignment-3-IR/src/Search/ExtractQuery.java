package Search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import Classes.Query;
import Classes.Path;

public class ExtractQuery {
	private List<Query> queries; // A list containing all the queries derieved from the topic.txt file
	private int currentPostion; // Used for file traversal and keeping track of queires counted
	private boolean titleFlag; // Used to signal when query content is found

	// titleFlag helps prevent the unecessary of maybe using a while loop to keep
	// appending till we encounter a desc tag hence preventing an O(n^2) operation
	// and sticking to O(n)

	// private function handles creation of query content until desc tag is
	// encountered
	private void extractQueryContentFromTag(String linePointer, StringBuilder queryContent, boolean titleFlag) {
		if (titleFlag) {
			// extracts content immediatly after title tag, till we encounter <desc> tag
			queryContent.append(linePointer.substring("<title>".length()).trim()).append(" ");
		}
	}

	// creating a hash set for stop word removal so look up and removal is O(1)
	private Set<String> LoadStopWords() {
		Set<String> stopWords = new HashSet<>();
		try {
			// reader initalized to read stopword.txt
			BufferedReader reader = new BufferedReader(new FileReader(Path.StopwordDir));
			String linePointer; // Keeps track of file contents

			linePointer = reader.readLine();

			while (linePointer != null) {
				stopWords.add(linePointer.trim().toLowerCase());
			}
		} catch (IOException e) {
			System.err.println("Error while creating stop word hashset" + e.getMessage());
		}
		return stopWords;
	}

	private String preProcessQueryContent(String queryContent) {
		// Tokenization
		String[] tokens = queryContent.split("\\s+");
		// Rather than applying a loop, used streams api to create a map function that
		// performed casefolding on all terms in query

		// case folding
		tokens = Arrays.stream(tokens).map(String::toLowerCase).toArray(String[]::new);

		// loading stop words
		Set<String> stopWords = LoadStopWords();

		// removing all tokens that belong to the stopWords String hashset
		tokens = Arrays.stream(tokens).filter(token -> !stopWords.contains(token)).toArray(String[]::new);

		// post processing on individual words return query back as a single string
		return String.join(" ", tokens);
	}

	private void topicQueryExtraction(String filePath) throws IOException {
		// declared a buffer reader to read topics.txt file content
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String linePointer;
		String topicId = null; // When <num Tag> split Number: and store value// of ID into this variable
		StringBuilder queryContent = new StringBuilder(); // used to store topic title as query content

		linePointer = reader.readLine(); // start reading document
		// iterate till end of document
		while (linePointer != null) {
			if (linePointer.startsWith("<num>")) {
				// split all that comes after Number following num tag --> Number: x - {XYX}
				// where x is topic ID, \s* for 0 or more spaces
				topicId = linePointer.split("Number:\\s*")[1].trim();
				// below statements handle encounter of desc tag and build query accordingly
			} else if (linePointer.startsWith("<title>")) {
				titleFlag = true;
				extractQueryContentFromTag(linePointer, queryContent, titleFlag);
			} else if (linePointer.startsWith("<desc>")) {
				// prevent further appending to query content as we have encountered desc tag
				titleFlag = false;
			} else if (linePointer.startsWith("</top>")) {
				// when we encounter ending tag for each topic we ensure to add the collected
				// query data to the QueryList and keep continuing
				if (topicId != null && queryContent.length() > 0) {
					// instantiate a new query object to be added to the queries list
					Query query = new Query();
					query.SetTopicId(topicId);
					String preProcessedQuery = preProcessQueryContent(queryContent.toString());
					query.SetQueryContent(preProcessedQuery);
					// appending to queries list data structure
					queries.add(query);
				}
			}

		}

		reader.close();
	}

	public ExtractQuery() {
		currentPostion = 0;
	}

	public boolean hasNext() {
		return false;
	}

	public Query next() {
		return null;
	}
}
