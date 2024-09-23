package ie.atu.sw;

import static java.lang.System.out;

import java.util.Arrays;

public class WordSearcher {

	// ---------------------------------------------------------------------------------------------
	// 										FIELDS
	// ---------------------------------------------------------------------------------------------

	private int numberOfResults = 5; // No. of search results
	private String searchMethod = "dotprod"; // Similarity search method
	private boolean sortDesc = true; // Results sort order
	private FileManager instance; // Input/output file data
	private String[] wordArray;
	private double[][] vectorArray;
	
	public WordSearcher(FileManager fileManager) {
		this.instance = fileManager;
		this.vectorArray = instance.getVectorArray();
		this.wordArray = instance.getWordArray();
		
	}

	// ---------------------------------------------------------------------------------------------
	// 									MAIN LOGIC METHODS
	// ---------------------------------------------------------------------------------------------

	// Handler to execute high-level elements of similarity search process
	public void similaritySearch(String input) throws IllegalArgumentException {
	    String[] splitInput = null; //For storing user input split into valid queries
	    String query = null; // For storing current query

	    	try {
	        splitInput = queryValidator(input); // Validate and split input into queries
	    	}catch (IllegalArgumentException n) {
	    		throw n; //Rethrow so that user can be prompted for input again
	    	}catch (Exception e) {
	    		System.err.println("An unexpected error occurred while validating query: " 
	    	+ e.getMessage());
	    	}
	        int numberOfQueries = splitInput.length; // Record the number of queries
	        SearchRecord[] summary = new SearchRecord[numberOfQueries]; //Initialize record array
	        
	        //Iterate through queries
	        for (int i = 0; i < splitInput.length; i++) {
	            try {
	                query = splitInput[i];
	                // Print current query
	                out.println(ConsoleColour.YELLOW + "Executing Search for Query: \"" + query + "\"...");
	                
	                String[][] results = executeSearch(query); // Execute search for the current query
	                SearchRecord searchRecord = new SearchRecord(splitInput[i], results);
	                summary[i] = searchRecord; //Store result for current query
	            } catch (WordNotFoundException nf) {
	                // If word not found, inform user and continue searching other queries
	                out.println(ConsoleColour.RED);
	                System.err.println(nf.getMessage());
	                
	                continue; // Skip to the next query
	            } catch (Exception e) {
	                // For unexpected exceptions
	                System.err.println("An unexpected error occurred during search for query: "
	                        + query + ": " + e.getMessage());
	                e.printStackTrace();
	                break; // Stop in event of unexpected error
	            }
	        }
	        printResults(summary, numberOfResults); // Print results to console
	        // If output file path is set & verified, save to file
			if (instance.isOutputPathValidated() == true) {
				instance.saveResultsToFile(summary);
				out.println(ConsoleColour.BLACK_BRIGHT);
				out.println("[INFO] Results saved to file: " + instance.getFileOutPath() + ".");
			} else {
				out.println(ConsoleColour.BLACK_BRIGHT);
				out.println("[INFO] Results were not saved as no output file was specified.");
			}
	    
	}

	// Executes low-level elements of the search process & returns a 2D array of sorted results
	private String[][] executeSearch(String query) throws Exception {
	    int size = 100; // Size of progress meter

	    double[] queryVector = null; // Store corresponding vector for query word
	    double[] similarityScores = null; // Store similarity scores from search method
	    String[][] unsortedResults = null; // Store unsorted words & similarity scores
	    String[][] sortedResults = null;

	    for (int i = 0; i < size; i++) {
	        try {
	            // Sequence of processing steps
	            switch (i) {
	                case 0 -> {
	                    queryVector = vectorMatch(query); // Initialize queryVector
	                }
	                case 25 -> {
	                    if (queryVector != null) {
	                        // Compute similarity scores based on the selected search method
	                        similarityScores = switch (searchMethod) {
	                            case "dotprod" -> dotProduct(queryVector);
	                            case "euclidean" -> euclideanDist(queryVector);
	                            case "cosine" -> cosineDist(queryVector);
	                            default -> throw new IllegalArgumentException(
	                                    "Unknown search method: " + searchMethod);
	                        };
	                    } else {
	                        throw new WordNotFoundException("Word not found for query: " + query);
	                    }
	                }
	                case 50 -> unsortedResults = buildResultsArray(query, wordArray, similarityScores);
	                case 75 -> sortedResults = sortResultsArray(unsortedResults); // Sort array of words & similarity scores
	                default -> {} // Do nothing
	            }
	            ProgressBar.printProgress(i + 1, size); // Run progress meter
	            Thread.sleep(10); // Wait between updates so that the progress is visible
	        } catch (WordNotFoundException nf) {
	            // Rethrow to be caught in similaritySearch method
	            throw nf;
	        } catch (Exception e) {
	            throw new Exception("Error during search execution at iteration " + i + ": " + 
	        e.getMessage(), e);
	        }
	    }
	    return sortedResults; // Return the final sorted results
	}

	
	// ---------------------------------------------------------------------------------------------
    // 								QUERY METHODS
    // ---------------------------------------------------------------------------------------------

	// Returns the vector of the word queried by the user
	public double[] vectorMatch(String query) {
		int wordIndex = -1; // Index for matching query to its corresponding vector

		// Search word array for query
		for (int i = 0; i < wordArray.length; i++) {

			// If current word = query, set wordIndex and stop searching
			if (query.equals(wordArray[i])) {
				wordIndex = i;
				// out.println("Word found. Index: " + i);
				break;
			}
			// If end of file reached, above is false & therefore no match found, return null
			else if (i == wordArray.length - 1) {
				return null;
			}
			// If current word does not equal query, continue searching
			else {
				continue;
			}
		}
		return vectorArray[wordIndex]; // Return corresponding vector of user query
	}

	/*
	 * Returns an array of the calculated dot products of the query vector and all
	 * other vectors from the vector array
	 */
	public double[] dotProduct(double[] queryVector) {
		// Calculate the number of vectors minus the query vector
		double[] dotProdResult = new double[vectorArray.length - 1]; // Store calculated results
		int resultIndex = 0;

		// Iterate through array of all vectors & calculate dot product
		for (int i = 0; i < vectorArray.length; i++) {

			if (isQuery(queryVector, vectorArray[i])) {
				// Ignore query vector
				continue;
			} else {
				double sum = 0; // Store sum of products

				// Iterate through each element in array
				for (int j = 0; j < queryVector.length; j++) {
					sum += queryVector[j] * vectorArray[i][j];
				}
				dotProdResult[resultIndex] = sum;
				resultIndex++;
			}
		}
		return dotProdResult;
	}

	/*
	 * Returns an array of the calculated Euclidean distances of the query vector
	 * and all other vectors from the query array
	 */
	public double[] euclideanDist(double[] queryVector) {
		double[] EuclideanDistResult = new double[vectorArray.length - 1]; // Store calculated results
		int resultIndex = 0;

		// Iterate through array of all vectors & calculate Euclidean distance
		for (int i = 0; i < vectorArray.length; i++) {
			if (isQuery(queryVector, vectorArray[i])) {
				// Ignore query vector
				continue;
			} else {
				double sum = 0; // Store sum of each vector element squared

				// Iterate through each element in array
				for (int j = 0; j < queryVector.length; j++) {
					sum += Math.pow((queryVector[j] - vectorArray[i][j]), 2); // Difference squared
				}
				EuclideanDistResult[resultIndex] = Math.sqrt(sum); // Calculate square root of sum
				resultIndex++;
			}
		}
		return EuclideanDistResult;
	}

	/*
	 * Returns an array of the calculated cosine distances of the query vector and
	 * all other vectors from the query array
	 */
	public double[] cosineDist(double[] queryVector) {
		// Create array for storing results & calculate numerator (dot product)
		double[] cosineDistResult = new double[vectorArray.length - 1];
		int resultIndex = 0;

		// Iterate through array of all vectors & calculate cosine distance
		for (int i = 0; i < vectorArray.length; i++) {
			if (isQuery(queryVector, vectorArray[i])) {
				// Ignore query vector
				continue;
			} else {
				double querySum = 0; // Sum of query vector squared
				double vectorSum = 0; // Sum of current comparison vector squared

				// Iterate through each element in array
				for (int j = 0; j < queryVector.length; j++) {
					querySum += Math.pow(queryVector[j], 2); // Query vector element squared
					vectorSum += Math.pow(vectorArray[i][j], 2); // Comparison vector element squared
				}
				// Calculate cosine distance by dividing dot product by product of square root of sums
				cosineDistResult[resultIndex] = cosineDistResult[resultIndex] / 
						(Math.sqrt(querySum) * Math.sqrt(vectorSum));
				resultIndex++;
			}
		}
		return cosineDistResult;
	}
	
	// ---------------------------------------------------------------------------------------------
    // 								RESULT HANDLING METHODS
    // ---------------------------------------------------------------------------------------------
	
	// Method to build the initial unsorted results array
	private String[][] buildResultsArray(String query, String[] wordArray, double[] similarityScores) {
	    int resultSize = wordArray.length - 1; // Query is excluded
	    String[][] sortedSimilarityArray = new String[resultSize][2]; // Store all unsorted results
	    int resultIndex = 0;
	    
	    // Iterate through arrays and set values
	    for (int i = 0; i < wordArray.length; i++) {
	    	if (wordArray[i].equals(query)){
	    		// Ignore query
	    		continue;
	    	}else {
		        sortedSimilarityArray[resultIndex][0] = wordArray[i]; // Set word
		        sortedSimilarityArray[resultIndex][1] = Double.toString(similarityScores[resultIndex]); // Set word similarity score
		        resultIndex++;
	    	}
	    }

	    return sortedSimilarityArray;
	}
	
	private String[][] sortResultsArray(String[][] unsortedResults) {
		String[][] sortedResults_All = unsortedResults;
		String[][] sortedResults_Top = new String[numberOfResults][2];
		if (sortDesc) {
			// If sortDesc is true
			if (searchMethod.equals("dotprod") || searchMethod.equals("cosine")) {
				// Sort "dotprod" or "cosine" in descending order
				Arrays.sort(sortedResults_All, (b, a) -> Double.compare(Double.parseDouble(a[1]),
						Double.parseDouble(b[1])));
			} else if (searchMethod.equals("euclidean")) {
				// Sort "euclidean" in ascending order
				Arrays.sort(sortedResults_All, (a, b) -> Double.compare(Double.parseDouble(a[1]),
						Double.parseDouble(b[1])));
			}
		} else {
			// If sortDesc is false
			if (searchMethod.equals("dotprod") || searchMethod.equals("cosine")) {
				// Sort "dotprod" or "cosine" in ascending order
				Arrays.sort(sortedResults_All, (a, b) -> Double.compare(Double.parseDouble(a[1]),
						Double.parseDouble(b[1])));
			} else if (searchMethod.equals("euclidean")) {
				// Sort "euclidean" in descending order
				Arrays.sort(sortedResults_All, (b, a) -> Double.compare(Double.parseDouble(a[1]),
						Double.parseDouble(b[1])));
			}
		}
		for (int i = 0; i < numberOfResults; i++) {
            sortedResults_Top[i][0] = sortedResults_All[i][0];
            sortedResults_Top[i][1] = sortedResults_All[i][1];
        }
		return sortedResults_Top;
	}
	
	// Prints results to console & saves to file, if output file path is set
		private void printResults(SearchRecord[] summary, int numberOfResuts) {
			
			for (SearchRecord searchRecord : summary) {
				if(searchRecord != null) {
		        out.println(ConsoleColour.BLACK_BOLD_BRIGHT);
		        out.println("\nSearch Results for \"" + searchRecord.query() + "\":");

		        String[][] results = searchRecord.results();

		        // Print all results in 2 columns, 10 characters wide, left justified & 4 decimal points
		        for (int resultsIndex = 0; resultsIndex < results.length; resultsIndex++) {
		            out.println(results[resultsIndex][0] + "\t\t\t"
		                    + String.format("%,.4f", Double.parseDouble(results[resultsIndex][1])));
		        }
				}
		    }
			

		}
	
	// ---------------------------------------------------------------------------------------------
	// 								HELPER METHODS
	// ---------------------------------------------------------------------------------------------

		
	// Ensures that the query is in the correct format
	private String[] queryValidator(String input) throws Exception {

		String filteredInput = null; // For storing input minus special characters
		String[] splitInput = null; // For storing input split into individual queries

		try {
			filteredInput = input.replaceAll("[^a-zA-Z ,]", "");
			if (filteredInput.contains(",")) {
				filteredInput.trim(); // Remove whitespace
				splitInput = filteredInput.split(","); // Split by comma
			} else {
				splitInput = filteredInput.split(" "); // Split by space
			}

		} catch (Exception e) { // Catch any unexpected errors
			throw new Exception("[Error] An issue occured while validating input" + e.getMessage());
		}
		return splitInput;
	}
	
	//Helper method to exclude the query from appearing in the search results
	private boolean isQuery(double[] queryVector, double[] comparisonVector) {
		for (int i=0; i<queryVector.length; i++) {
			if (queryVector[i] != comparisonVector[i]) {
				return false; //If any element is different, not the same vector
			}
		}
		return true;
	}

	// ---------------------------------------------------------------------------------------------
	// GETTERS & SETTERS
	// ---------------------------------------------------------------------------------------------

	

	public int getNumberOfResuts() {
		return numberOfResults;
	}

	public void setNumberOfResuts(int numberOfResuts) {
		this.numberOfResults = numberOfResuts;
	}

	public String getSearchMethod() {
		return searchMethod;
	}

	public void setSearchMethod(String searchMethod) {
		this.searchMethod = searchMethod;
	}

	public boolean isSortDesc() {
		return sortDesc;
	}

	public void setSortDesc(boolean sortDesc) {
		this.sortDesc = sortDesc;
	}

}
