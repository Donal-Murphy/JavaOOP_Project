Similarity Search
@author Donal Murphy
@version JavaSE-21

Description
This is a console-based application that allows the user to enter one or multiple words and returns a list of similar words using GloVe embeddings. The GloVe embeddings file is an external txt file whose path can be defined by user (by default it is located in the project directory. The user can also specify the location of an output file in order to save the results, The results consist of the query, followed by a list of similar results and their similarity score according to the search method used.

To Run
From terminal at project directory: 
-> javac -d ../bin src/ie/atu/sw/*.java
-> cd bin
-> java ie.atu.sw.Runner

If the application indicates that the default embed file was found, you may proceed with the search functionality, otherwise you will have to enter it manually. The options can be chosen by entering a number and hitting “Enter”. If you select “3) Enter Word or Text” without an input(embed) file specified, you will be instructed to do so.
Features
Main Menu
Specify Embed File
Input path of file
Must be .txt
Must already exist
Above is verified by application
Specify an Output File
Input path of file
Must be .txt
Directory must already exist
Above is verified by application
Enter a Word or Text
Input a word or series of words (can be separated by spaces or commas)
Other special characters are automatically removed
Configure Options
Specify Search method
	- Choose between dot product, euclidean distance, or cosine distance
	- Value is stored for later use
Specify Number of Search Results
	- Choose a value between 1-999
Specify Sort Order
	- Can choose to sort most -> least / least -> most similar
	 



 