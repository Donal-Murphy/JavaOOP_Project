package ie.atu.sw;

import static java.lang.System.out;
import java.util.Scanner;

public class Menu {


	//---------------------------------------------------------------------------------------------
    //                                    FIELDS
    //---------------------------------------------------------------------------------------------

	// Store references
	private Scanner s;
	private FileManager instance;
	private WordSearcher search;
	private boolean keepRunning = true;

	// Set references
	public Menu(Scanner scanner, FileManager fileManager, WordSearcher wordSearcher) {
		this.s = scanner;
		this.instance = fileManager;
		this.search = wordSearcher;

	}
	//TODO Check that invalid paths dont set validated = true

	//---------------------------------------------------------------------------------------------
    //                                 MAIN LOGIC METHODS
    //---------------------------------------------------------------------------------------------

	// Main menu options
	public void mainMenu() {
		while(keepRunning) {
		printMainMenu(); // Display main menu to user

		int choice = menuInputValidation(5); // Validate user input

		// Execute methods/sub-menus based on user input
		switch (choice) {
			case 1 -> configureFilePath(true); // Specify embedding file
			case 2 -> configureFilePath(false); // Specify an output file
			case 3 -> searchInput(); // Word search
			case 4 -> configMenu(); // Modify search settings
			case 5 -> { // Close Program
				out.println(ConsoleColour.BLACK_BRIGHT);
				out.println("[INFO] Exiting... Bye!");
				keepRunning = false;
			}
			default -> criticalError(); // Shouldn't occur; menuInputValidation prevents invalid
										// choices
		}
		}

	}

	// Configuration menu for modifying search options
	private void configMenu() {

		printConfigMenu(); // Show menu

		int choice = menuInputValidation(4); // Validate user input

		switch (choice) {
			case 1 -> searchMethodMenu(); // Change search method (dotprod/cosine/euclidean)
			case 2 -> resultsNumberMenu(); // Change number of search results
			case 3 -> sortOrderMenu(); // Change order in which results are sorted
			case 4 -> mainMenu(); // Return to previous menu
			default -> criticalError(); // Shouldn't occur; menuInputValidation prevents invalid
										// choices
		}
	}

	// Menu for changing search method
	private void searchMethodMenu() {

		printSearchMethodMenu(); // Show menu

		int choice = menuInputValidation(4); // Validate user input

		switch (choice) {
			case 1 -> { // Change to dot product search mode
				out.println(ConsoleColour.BLACK_BRIGHT);
				out.println("[INFO] Dot Product Method Selected");
				search.setSearchMethod("dotprod");
			}
			case 2 -> { // Change to euclidean search mode
				out.println(ConsoleColour.BLACK_BRIGHT);
				out.println("[INFO] Euclidean Distance Method Selected");
				search.setSearchMethod("euclidean");
			}
			case 3 -> { // Change to cosine search mode
				out.println(ConsoleColour.BLACK_BRIGHT);
				out.println("[INFO] Cosine Distance Method Selected");
				search.setSearchMethod("cosine");
			}
			case 4 -> configMenu(); // Return to previous menu
			default -> criticalError(); // Shouldn't occur; menuInputValidation prevents invalid
										// choices
		}
	}

	// Menu for changing number of search results
	private void resultsNumberMenu() {

		out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
		out.print("Enter Desired Number of Search Results>");

		int numberOfResuts = menuInputValidation(999); // Max number of results allowed
		search.setNumberOfResuts(numberOfResuts); // Set number of search results

		out.println(ConsoleColour.BLACK_BRIGHT);
		out.print("[INFO] Number of Search Results set to " + numberOfResuts);
		out.println();
	}

	// Menu for changing sort order (Asc/Desc)
	private void sortOrderMenu() {

		printSortOrderMenu(); // Show menu

		int choice = menuInputValidation(4); // Validate user input

		switch (choice) {
			case 1 -> search.setSortDesc(true); // Sort by best match to worst
			case 2 -> search.setSortDesc(false); // Sort by worst match to best
			case 3 -> configMenu(); // Return to previous menu
			default -> criticalError(); // Shouldn't occur; menuInputValidation prevents invalid
										// choices
		}
	}


	//---------------------------------------------------------------------------------------------
    //                                    DISPLAY METHODS
    //---------------------------------------------------------------------------------------------


	// Display main menu
	private void printMainMenu() {
		out.println(ConsoleColour.WHITE);
		out.println("************************************************************");
		out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		out.println("*                                                          *");
		out.println("*          Similarity Search with Word Embeddings          *");
		out.println("*                                                          *");
		out.println("************************************************************");
		out.println("(1) Specify Embedding File");
		out.println("(2) Specify an Output File");
		out.println("(3) Enter a Word or Text");
		out.println("(4) Configure Options");
		out.println("(5) Quit");
	}

	// Display config menu
	private void printConfigMenu() {
		out.println(ConsoleColour.WHITE);
		out.println("(1) Specify Search Method");
		out.println("(2) Specify Number of Search Reults");
		out.println("(3) Specify Sort Order");
		out.println("(4) Return to Main Menu");
	}

	// Display search method menu
	private void printSearchMethodMenu() {
		out.println(ConsoleColour.WHITE);
		out.println("(1) Dot Product");
		out.println("(2) Euclidean Distance");
		out.println("(3) Cosine Distance");
		out.println("(4) Return to Config Menu");
	}

	// Display sort order menu
	private void printSortOrderMenu() {
		out.println(ConsoleColour.WHITE);
		out.println("(1) Descending (Most similar -> Least similar)");
		out.println("(2) Ascending (Least similar -> Most similar");
		out.println("(3) Return to Config Menu");
	}


	//---------------------------------------------------------------------------------------------
    //                                    HELPER METHODS
    //---------------------------------------------------------------------------------------------


	// Change input/output files
	private void configureFilePath(boolean isInput) {
		
		while (true) {
			
		if (isInput) { // For input file
			out.println("\nCurrent Embedding File: " + instance.getFileInPath());
			out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
			out.println("Please Enter the Full Path to the Desired Input File>\n");
		} else { // For Output file
			out.println("\nCurrent Output File: " + instance.getFileOutPath());
			out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
			out.println("Please Enter the Full Path to the Desired Output File>\n");
		}
		
		String path = s.nextLine().trim(); // User input
		if (path.isEmpty()) {  // Check if input is empty
            out.print(ConsoleColour.RED);
            System.out.println("[Error] Input was empty. Please enter a valid option.");
            continue;
		}else {

		try {
			if (isInput) { // For input file
				instance.setFileInPath(path);
				instance.setInputPathValidated(true);
				instance.initialiseInputFile();
			} else {// For Output file
				instance.setFileOutPath(path);
				instance.setOutputPathValidated(true);
			}
		} catch (Exception e) {
			out.println(e.getMessage() + "\nPlease try again");
			configureFilePath(isInput);
		}
		break;
		}
		}
	}

	// Validates user input for menu options
	private int menuInputValidation(int numOptions) {
	    boolean validInput = false;

	    while (!validInput) {
	        out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
	        out.println("Select Option [1-" + numOptions + "]>"); // present options

	        String input = s.nextLine().trim();  // Read and trim input

	        if (input.isEmpty()) {  // Check if input is empty
	            out.print(ConsoleColour.RED);
	            System.out.println("[Error] Input was empty. Please enter a valid option.");
	        } else if (input.matches("\\d+")) {  // Check if input is an integer
	            int choice = Integer.parseInt(input);
	            if (choice >= 1 && choice <= numOptions) {  // Check if the integer is within bounds
	                validInput = true;
	                return choice;
	            } else {  // If user has entered an out-of-bounds integer
	                out.print(ConsoleColour.RED);
	                out.println("[Error] Not a valid integer. Please enter a number between 1 and "
	                + numOptions + ".");
	            }
	        } else {  // If user has entered a non-integer
	            out.print(ConsoleColour.RED);
	            System.out.println("[Error] Input was not an integer. Please enter a valid option.");
	        }
	    }

	    return -1;
	}
	
	//Prompt user for search input & launches similarity search
	private void searchInput() {
		s.nextLine();// Consume empty line
		if (instance.isInputPathValidated()) { // If input path is valid, proceed to search
			while (true) { // Re-prompt user if input is empty
			out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
			out.println("Enter word(s) to search: ");
			String input = s.nextLine(); //Search query
			String emptyCheck =input.trim(); //Remove whitespace
			if(emptyCheck.isEmpty()) { // Check for empty input
				out.print(ConsoleColour.RED);
				out.println("[Error] Query Cannot be Empty");
				continue;
			}else {
				search.similaritySearch(input); // Execute search with user input
				break;
			}
			}
			
		} else {
			// If the path is not valid, prompt user to first enter an input file path
			out.print(ConsoleColour.RED);
			out.println("[Error] Please Enter a Valid Input Filepath First!");
		}
	}


	//---------------------------------------------------------------------------------------------
    //                                    UTILITY METHODS
    //---------------------------------------------------------------------------------------------


	// Displays an error if something unexpected happens during user input for menu options
	// Mostly for debug purposes as menuInputValidation should prevent this
	private void criticalError() {
		out.print(ConsoleColour.RED_BOLD);
		out.println("[CRITICAL ERROR] Something unexpected happened while processing user input."
				+ " Please check code.");
		System.exit(0); // Close program
	}

}
