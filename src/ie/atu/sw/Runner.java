package ie.atu.sw;

import java.util.Scanner;

public class Runner {

	public static void main(String[] args) {

		// Initialize necessary objects
		Scanner s = new Scanner(System.in);
		FileManager instance = new FileManager();
		

		// Search for default embed file
		try {
			String defaultEmbedPath = instance.findFile("word-embeddings.txt"); // Default
			instance.setFileInPath(defaultEmbedPath); // Attempt to find file in parent directory
			instance.setInputPathValidated(true); // Set validate status to true if found
			System.out.println("[INFO] Default Embed File Found");
			instance.initialiseInputFile(); // Initialize variables from FileManager
		}
		// Alert user that they will be required to enter path manually if default not
		// found
		catch (Exception e) {
			System.out.println("Error finding default embed file. Please enter manually");
		}
		WordSearcher search = new WordSearcher(instance);
		Menu m = new Menu(s, instance, search);

		// Launch the program
		m.mainMenu();
	}
}