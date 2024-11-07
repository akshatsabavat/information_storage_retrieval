package Search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	// private void extractQueryContentFromTag(String linePointer, StringBuilder
	// queryContent, boolean titleFlag) {
	// if (titleFlag) {
	// // extracts content immediatly after title tag, till we encounter <desc> tag
	// if (linePointer.startsWith("<title>")) {
	// queryContent.append(linePointer.substring("<title>".length()).trim()).append("
	// ");
	// } else {
	// queryContent.append(linePointer.trim()).append(" ");
	// }
	// }
	// }

	// creating a hash set for stop word removal so look up and removal is O(1)
	private Set<String> LoadStopWords() {
		Set<String> stopWords = new HashSet<>();
		try {
			// surrounded reader with try resources to ensure automatic close of reader
			try (// reader initalized to read stopword.txt
					BufferedReader reader = new BufferedReader(new FileReader(Path.StopwordDir))) {
				String linePointer; // Keeps track of file contents

				while ((linePointer = reader.readLine()) != null) {
					stopWords.add(linePointer.trim().toLowerCase());
				}
			}
		} catch (IOException e) {
			System.err.println("Error while creating stop word hashset" + e.getMessage());
		}
		return stopWords;
	}

	private String preProcessQueryContent(String queryContent) {
		System.out.println("Pre-processing query: " + queryContent);
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

		// start reading document
		while ((linePointer = reader.readLine()) != null) {
			if (linePointer.startsWith("<num>")) {
				// split all that comes after Number following num tag --> Number: x - {XYX}
				// where x is topic ID, \s* for 0 or more spaces
				topicId = linePointer.split("Number:\\s*")[1].trim();
				System.out.println("Found topic ID: " + topicId);
				// below statements handle encounter of desc tag and build query accordingly
			} else if (linePointer.startsWith("<title>")) {
				titleFlag = true;
				System.out.println("Title flag set to true");
				queryContent.append(linePointer.substring("<title>".length()).trim()).append(" ");
			} else if (linePointer.startsWith("<desc>")) {
				// prevent further appending to query content as we have encountered desc tag
				titleFlag = false;
			} else if (linePointer.startsWith("</top>")) {
				System.out.println("End of topic reached");
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

				topicId = null;
				queryContent = new StringBuilder();
			} else if (titleFlag) {
				queryContent.append(linePointer.substring("<title>".length()).trim()).append(" ");
			}

		}

		reader.close();
	}

	public ExtractQuery() {
		// initializing a new list of queries to populate in memory
		queries = new ArrayList<>();
		currentPostion = 0;

		try {
			topicQueryExtraction(Path.TopicDir);
			System.out.println("\n=== All Extracted Queries ===");
			for (Query query : queries) {
				System.out.println("Topic ID: " + query.GetTopicId());
				System.out.println("Content: " + query.GetQueryContent());
				System.out.println("------------------------");
			}
			System.out.println("=== End of Queries ===\n");

		} catch (IOException e) {
			System.err.println("Error while extracting query from topics.txt" + e.getMessage());
		}
	}

	public boolean hasNext() {
		// validates and ensures there are still queries remaining in the Query List
		return currentPostion < queries.size();
	}

	public Query next() {
		if (hasNext()) {
			// move to the next position knowing u have queries left in the Array Query List
			return queries.get(currentPostion++);
		}
		return null;
	}
}
