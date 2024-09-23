package ie.atu.sw;

public class ProgressBar {
	// Used for printing a progress bar to the screen
	public static void printProgress(int index, int total) {
		if (index > total) return;	//Exit when out of range
        int size = 50; 				//Must be less than console width
	    char done = '█';			//Completed
	    char todo = '░';			//Not yet completed
	    
        int complete = (100 * index) / total; //Calc percentage complete
        int completeLen = size * complete / 100; //Calc completed length size
        
        // Build & update progress meter
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
        	sb.append((i < completeLen) ? done : todo);
        }
        
        System.out.print("\r" + sb + "] " + complete + "%"); //Print meter
        if (done == total) System.out.println("\n"); //New line when complete
    }
}
