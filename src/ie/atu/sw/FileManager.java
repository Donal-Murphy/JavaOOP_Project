package ie.atu.sw;

import static java.lang.System.out;
import java.io.*;

public class FileManager {

	
	//---------------------------------------------------------------------------------------------
    //                                    FIELDS
    //---------------------------------------------------------------------------------------------

	private String fileInPath;
	private String fileOutPath;
	private int lineCount;
	private int vectorLength;
	private String[] wordArray;
	private double[][] vectorArray;
	private boolean inputPathValidated;
	private boolean outputPathValidated;

	// TODO Verify input file contents

	//---------------------------------------------------------------------------------------------
    //                                 MAIN LOGIC METHODS
    //---------------------------------------------------------------------------------------------


	// Checks for the existence of the default input file location
	public String findFile(String fileName) throws FileNotFoundException {

		// First, check in the current directory (for running in IDE)
		File file = new File(fileName);
		if (file.exists() && !file.isDirectory()) {
			return file.getPath();
		}

		// Then, check in the parent directory (For running in terminal from /bin)
		file = new File("../" + fileName);
		if (file.exists() && !file.isDirectory()) {
			return file.getPath();
		}

		// If the file is not found in either location, throw an exception
		throw new FileNotFoundException(
				"File not found in both current and parent directories: " + fileName);
	}
	
	// Initializes class variables from input file
		public void initialiseInputFile() {
			out.println(ConsoleColour.YELLOW);
			out.println("Initializing Input File...");
			int size = 100; // Size of progress meter
			for (int i = 0; i < size; i++) {

				// Sequence of processing steps
				switch (i) {
					case 0:
						setLineCount(); // Lines in file
						break;
					case 25:
						setVectorLength(); // Vector elements in lines
						break;
					case 50:
						setVectorArray(); // Store array of vectors
						break;
					case 75:
						setWordArray(); // Store array of words
						break;
					default:
						break;

				}

				ProgressBar.printProgress(i + 1, size); // Run progress meter
				try {
					Thread.sleep(10); // Wait between updates so that the progress is visible
				} catch (InterruptedException e) { // Catch any errors
					e.printStackTrace();
				}
			}
		}
		
		// Saves search results to file stored in fileOutPath
		public void saveResultsToFile(SearchRecord[] summary) {
		    try {
		        FileWriter out = new FileWriter(fileOutPath); // Initialize FileWriter

		        for (SearchRecord searchRecord : summary) {
		            if (searchRecord != null) {
		                out.write("\nSearch Results for \"" + searchRecord.query() + "\":\n");

		                String[][] results = searchRecord.results();

		                // Write all results in 2 columns, left-justified with 10 characters wide, and 4 decimal points
		                for (int resultsIndex = 0; resultsIndex < results.length; resultsIndex++) {
		                    out.write(String.format("%-10s\t\t\t%.4f\n", 
		                        results[resultsIndex][0], 
		                        Double.parseDouble(results[resultsIndex][1])));
		                }
		                out.write("\n"); // Add an extra newline after each query's results
		            }
		        }
		        out.close(); // Close stream
		    } catch (Exception e) { // Catch any exceptions
		        e.printStackTrace();
		    }
		}
		
	//---------------------------------------------------------------------------------------------
	//                                 HELPER METHODS
	//---------------------------------------------------------------------------------------------


		// Validates that the path exists, and that it is of the correct type
		private void pathValidation(String path, boolean isInput) throws Exception {
		    File f = new File(path);

		    if (isInput) {
		        // For input files:
		        // Check if the file exists and is not a directory
		        if (!f.exists() || f.isDirectory()) {
		            throw new FileNotFoundException("File not found: " + path);
		        }

		        // Check if the file is a .txt file
		        if (!path.endsWith(".txt")) {
		            throw new IllegalArgumentException(
		                    "Invalid file type: " + path + " is not a .txt file");
		        }

		        // Check if the file has read/write access
		        if (!f.canRead() || !f.canWrite()) {
		            throw new SecurityException(
		                    "Cannot access the file: " + path + ". Please check permissions");
		        }
		    } else {
		        // For output files:
		        // Check if the path is a valid directory
		        File directory = new File(path).getParentFile(); // Get the parent directory
		        if (directory != null && !directory.exists()) {
		            throw new FileNotFoundException("Directory not found: " + 
		        directory.getAbsolutePath());
		        }

		        // Check if the path ends with .txt
		        if (!path.endsWith(".txt")) {
		            throw new IllegalArgumentException(
		                    "Invalid file type: " + path + " is not a .txt file");
		        }
		    }
		}

	// Returns the number of lines in the input file specified in "fileInPath"
	private int lineCounter() {
		int lineCount = 0; // Store no. of lines

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(fileInPath))); // Start file stream
			LineNumberReader lineNumRead = new LineNumberReader(br); // Start line count
			lineNumRead.skip(Long.MAX_VALUE); // Move to end of file
			lineCount = (int) lineNumRead.getLineNumber(); // Record line number
			br.close(); // Close stream

		} catch (Exception e) { // Catch any exceptions
			e.printStackTrace();
		}

		// out.println("Lines in file: " + lineCount);
		return lineCount;
	}

	// Returns the number of vectors for each word
	private int vectorCounter() {
		int vectorCount = 0; // Store no. of vectors

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileInPath)); // Start file stream
			String firstLine = br.readLine(); // Read first line
			String[] splitLine = firstLine.split(","); // Split fist line
			vectorCount = splitLine.length - 1; // No. vectors = total no. elements in line - word
			br.close(); // Close stream

		} catch (Exception e) { // Catch any exceptions
			e.printStackTrace();
		}

		return vectorCount;
	}

	// Returns string array of words from the file specified in filePath of length = lineCount
	private String[] extractWordsFromFile() {
		String[] wordArray = new String[lineCount]; // Store array of all words

		try {
			int index = 0;
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(fileInPath))); // Start file stream
			String line = null;

			while ((line = br.readLine()) != null) { // Read line by line
				String[] splitLine = line.split(","); // Split line
				wordArray[index] = splitLine[0]; // Store word in wordArray
				index++;
			}
			br.close(); // Close stream

		} catch (Exception e) { // Catch any exceptions
			e.printStackTrace();
		}
		return wordArray;
	}

	// Returns a 2D double array of vectors from the file specified in "filePath"
	private double[][] extractVectorsFromFile() {
		double[][] vectorArray = new double[lineCount][vectorLength]; // Store array of all vectors

		try {
			int rows = 0;
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(fileInPath))); // Start file stream
			String line = null;

			while ((line = br.readLine()) != null) { // Read line by line
				String[] splitLine = line.split(","); // Split line

				// Store each vector element in separate column & each vector in a separate row
				for (int cols = 1; cols < splitLine.length; cols++) {
					vectorArray[rows][cols - 1] = Double.parseDouble(splitLine[cols]);
				}
				rows++;
			}
			br.close(); // Close stream

		} catch (Exception e) { // Catch any exceptions
			e.printStackTrace();
		}
		return vectorArray;
	}

	

    //---------------------------------------------------------------------------------------------
    //                                 GETTERS & SETTERS
    //---------------------------------------------------------------------------------------------

	public boolean isOutputPathValidated() {
		return outputPathValidated;
	}

	public void setOutputPathValidated(boolean pathValidated) {
		this.outputPathValidated = pathValidated;
	}

	public boolean isInputPathValidated() {
		return inputPathValidated;
	}

	public void setInputPathValidated(boolean pathValidated) {
		this.inputPathValidated = pathValidated;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount() {
		this.lineCount = lineCounter();
	}

	public int getVectorLength() {
		return vectorLength;
	}

	public void setVectorLength() {
		this.vectorLength = vectorCounter();
	}

	public String[] getWordArray() {
		return wordArray;
	}

	public void setWordArray() {
		this.wordArray = extractWordsFromFile();
	}

	public double[][] getVectorArray() {
		return vectorArray;
	}

	public void setVectorArray() {
		this.vectorArray = extractVectorsFromFile();
	}

	public String getFileInPath() {
		return fileInPath;
	}

	public void setFileInPath(String fileInPath) throws Exception {

		pathValidation(fileInPath, true); // Validate the input file
		out.println("Input file selected: " + fileInPath);
		this.fileInPath = fileInPath; // If all checks pass, set the file path
	}

	public String getFileOutPath() {
		return fileOutPath;
	}

	public void setFileOutPath(String fileOutPath) throws Exception {
		pathValidation(fileOutPath, false); // Validate the input file
		out.println("Output path selected: " + fileOutPath);
		this.fileOutPath = fileOutPath;// If all checks pass, set the file path
	}

}
