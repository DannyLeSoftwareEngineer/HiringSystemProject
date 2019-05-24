import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/*
* Class: MovieMaster
* Description: The class contains the Menu interface of the program,
* 				allowing the user to utilise the program however they want to.
* 				It is also contains functions that are needed to create various objects,
* 				build a string containing the information contained in the every class, read and write data. 
* Author: [Danny le] - [s3722067]
*/
public class MovieMaster {
	Item[] items = new Item[100];
	int nullIndexValue;
	private final int ID_LENGTH = 3;
	private final int  GAME_RENTAL_PERIOD = 7;
	private final int NEW_RELEASE_RENTAL_PERIOD = 2;
	private final int WEEKLY_RENTAL_PERIOD = 7;
	NumberFormat formatter = new DecimalFormat("#0.00");
	Scanner scanner = new Scanner(System.in);
	
	/*
	*  readData() ALGORITHM
	* BEGIN
	* GET Data file
	* IF file does not exist
	*    	SWITCH to backup file
	* IF backup file does not exist 
	* 		SWITCH to not load a  file
	* COMPUTE if there is a next line
	* IF there is a next line 
	* 		COMPUTE the line and spilt the data in an array
	* 	IF the item id is new
	* 		ADD the item id into the item array	
	*ELSE
	*		ADD the hiring record into the hiring record array	
	*END
	*/

	public void readData() {
		/*
		 * Prevents an exception FileNotFoundException by changing the file path
		 * depending on what exists in the src folder
		 */
		File Data = new File("src/Data.txt");
		if (!Data.exists()) {
			System.out.println("Switching to back up file");
			Data = new File("src/Data_backup.txt");
		}
		if (!Data.exists()) {
			System.out.println("No item Data was loaded");
			return;
		}

		File filename = Data;
		Scanner scan = null;
		try {
			String currentId = null;
			int itemIndexValue = -1;
			scan = new Scanner(filename);
			boolean isNewReleaseValue = false;
			/*
			 * If the file still has lines the scanner will continue to loop
			 * If it finds an id different that the one it just read before it, will read it in 
			 * as a new item, and based of the id prefix, it will use the appropriate class method
			 * to add the item.  It will also assign the current id to the id just read in and add one to the index value. 
			 */
			while (scan.hasNext()) {
				String dataLine = scan.nextLine();
				String[] splitData = dataLine.split(":");
				String token = scan.nextLine();
				if (!(splitData[0].substring(2, 5).equals(currentId))) {
					
					if (splitData[0].substring(0, 1).equals("M")) {
						itemIndexValue += 1;
						if (splitData[5].equals("WK")) {
							isNewReleaseValue = false;
						} else if (splitData[5].equals("NR")) {
							isNewReleaseValue = true;
						}
						Movie movieData = null;
						try {
							movieData = new Movie(splitData[0].substring(2, 5), splitData[1], splitData[3],
									splitData[2], isNewReleaseValue);
						} catch (IdException e) {
							System.out.println(e.getMessage());
						}
						items[itemIndexValue] = movieData;
						currentId = splitData[0].substring(2, 5);
					}

					else if (splitData[0].substring(0, 1).equals("G")) {
						itemIndexValue += 1;
						String[] platfromsData = splitData[5].split(",");
						Game gameData = null;
						try {
							gameData = new Game(splitData[0].substring(2, 5), splitData[1], splitData[3], splitData[2],
									platfromsData);
						} catch (IdException e) {
							System.out.println(e.getMessage());
						}
						items[itemIndexValue] = gameData;
						if (splitData[6].equals("E")) {
							Game GameItem = (Game) items[itemIndexValue];
							GameItem.setExtened(true);
						} else {
							Game GameItem = (Game) items[itemIndexValue];
							GameItem.setExtened(false);
						}
						currentId = splitData[0].substring(2, 5);
					}
				/*
				*If the id is the same as the one read before it, it will treat it as 
				* a hiring record line and read in the date to fill the hiring record of the item
				* based on whether the third element in the line is the word "none"
				*/ 
				} else {
					if (splitData[2].equals("none")) {
						//Reading in date on an item that is on hire but never returned
						String[] idSpiltData = splitData[0].split("_");
						DateTime Date = new DateTime(Integer.parseInt(splitData[1].substring(0, 2)),
								Integer.parseInt(splitData[1].substring(2, 4)),
								Integer.parseInt(splitData[1].substring(4, 8)));

						try {
							items[itemIndexValue].borrow(idSpiltData[2], Date);
						} catch (BorrowException | IdException e) {
							e.printStackTrace();
						}

					} else {
						//Reading in date on an item that is on hire and never returned
						String[] idSpiltData = splitData[0].split("_");
						DateTime Date = new DateTime(Integer.parseInt(splitData[1].substring(0, 2)),
								Integer.parseInt(splitData[1].substring(2, 4)),
								Integer.parseInt(splitData[1].substring(4, 8)));
						DateTime Date2 = new DateTime(Integer.parseInt(splitData[2].substring(0, 2)),
								Integer.parseInt(splitData[2].substring(2, 4)),
								Integer.parseInt(splitData[2].substring(4, 8)));
						try {
							items[itemIndexValue].borrow(idSpiltData[2], Date);
							items[itemIndexValue].returnItem(Date2, Double.parseDouble(splitData[4]));
						} catch (BorrowException | IdException e) {
							System.out.println(e.getMessage());
						}
					}
				}

			}
			Menu();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	
	
	/*
	* AddItem() ALGORITHM
	* BEGIN
	* PRINT each line of the menu
	* PRINT a prompt for the user to enter their selection
	* COMPUTE their input and run the correct method
	* END
	*TEST
	*			input A: Call the additem method
	*			input a: Call the additem method
	*			input z:  Call the menu method
	*			input x:	Call the writeData method and end the program
	*/
	public void Menu() {
		//NOTE: THE MENU MAYBE MISALIGNED DUE TO ECLIPSE'S
		//MAC FONT. THIS IS PROPERLY ALIGNED ON WINDOWS.
		//The code used to format this menu is shown is the 
		//design specification as the way to format strings (page 4)
		System.out.println("*** Movie Master System Menu ***");
		System.out.println(String.format("%-25s %s", "Add Item:", "A"));
		System.out.println(String.format("%-25s %s", "Borrow Item:", "B"));
		System.out.println(String.format("%-25s %s", "Return Item:", "C"));
		System.out.println(String.format("%-25s %s", "Display Details", "D"));
		System.out.println(String.format("%-25s %s", "Seed Data", "E"));
		System.out.println(String.format("%-25s %s", "Exit Program:", "X"));
		System.out.print("Enter selection: ");
		String input = scanner.nextLine().toLowerCase();

		switch (input) {
		case "a":
			addItem();
			break;
		case "b":
			borrowItem();
			break;
		case "c":
			returnItem();
			break;
		case "d":
			getDetails();
			break;
		case "e":
			seedData();
			break;
		case "x":
			writeData();
			break;
		default:
			Menu();
			break;
		}
		if (input.equals("x")) {
			System.exit(0);
		} else {
			Menu();
		}
	}

	/*
	* AddItem() ALGORITHM
	* BEGIN
	* PRINT message to prompt user to enter an ID
	* IF length of id was 3 
	* 		IF id is in the system
	* 			PRINT error message
	* 		ELSE
	* 			PRINT message to prompt user to enter an title, genre, description and Movie or game and other details depending on whether
	* 			it is a movie or game.
	* 			CALL the constructor of the appropriate class
	* 			DISPLAY message to confirm adding an item
	* ELSE CATCH Exception and print the error message			
	* END
	*TEST
	*			Item is in the array, an error message is printed
	*			Id enter had more than 3 characters, an error message is printed
	*			A length 3 id and is not in the array is inputed, the user is prompted to input
	*			more information
	*/
	public void addItem() {
		String id;
		System.out.print("Enter id:");
		id = scanner.nextLine().toUpperCase();
		nullIndexValue = nullIdexValue();
		boolean existItem = false;
		boolean isNewReleaseValue = false;

		// Searches through the array of movies and tries to match the Id to an item
		//in the items array. If found it will print an error message.
		double indexValue = idMatch(id, nullIndexValue);
		if (!Double.isNaN(indexValue)) {
			existItem = true;
			System.out.println("Error - Id for " + items[(int) indexValue].getId() + " aleady exists in the system!");
		}

		// If the id  does not exist in the array, prompt the user to enter extra details
		// such as the title, genre and description 
		if (existItem == false) {
			System.out.print("Enter title:");
			String title = scanner.nextLine();
			System.out.print("Enter genre:");
			String genre = scanner.nextLine();
			System.out.print("Enter description:");
			String description = scanner.nextLine();

			System.out.print("Enter Movie or Game (M/G)? : ");
			String itemType = scanner.nextLine().toUpperCase();
			while (!itemType.equals("M") && !itemType.equals("G")) {
				System.out.println("Error: You must enter:" + "\'" + "M" + "\'" + "or" + "\'" + "G" + "\'");
				System.out.print("Enter Movie or Game (M/G): ?");
				itemType = scanner.nextLine().toUpperCase();
			}
			
			// Separate code for movie and game item types
			//If the item type is a movie it will ask relevant details specifically tailored for the movie item
			//the section also converts the users inputs to actual relevant boolean values 
			//and printing a confirmation message with the user is successful in adding a movie
			//The same is done with the games object, however with different questions.
			if (itemType.equals("M")) {
				System.out.print("Enter new release  (Y/N):");
				String isNewReleaseInput = scanner.nextLine().toUpperCase();
				while (!isNewReleaseInput.equals("Y") && !isNewReleaseInput.equals("N")
						&& !isNewReleaseInput.equals("")) {
					System.out.println("Error: You must enter:" + "\'" + "Y" + "\'" + "or" + "\'" + "N" + "\'");
					System.out.print("Is new release?  (Y/N):");
					isNewReleaseInput = scanner.nextLine();
				}

				if (isNewReleaseInput.equals("")) {
					System.out.println("Exiting to main Menu");
				}
				if (isNewReleaseInput.equals("Y")) {
					isNewReleaseValue = true;
				} else if (isNewReleaseInput.equals("N")) {
					isNewReleaseValue = false;
				}

				try {
					Movie Movie1 = new Movie(id, title, genre, description, isNewReleaseValue);
					items[nullIndexValue] = Movie1;
					System.out.println("New movie added succesfully for movie id:" + items[nullIndexValue].getId());
				} catch (IdException e) {
					System.out.println(e.getMessage());
				}
			}

			if (itemType.equals("G")) {
				System.out.print("Enter Game Platform: ");
				String platformsInput = scanner.nextLine();
				String[] platforms = platformsInput.split(",");

				try {
					Game Game1 = new Game(id, title, genre, description, platforms);
					items[nullIndexValue] = Game1;
					System.out.println("New movie added succesfully for game id:" + items[nullIndexValue].getId());
				} catch (IdException e) {
					System.out.println(e.getMessage());

				}

			}

		}
	}
	
	/*
	* borrowItem() ALGORITHM
	* BEGIN
	* PRINT message to prompt user to enter an ID
	* IF length of id was not length 3 
	*		PRINT an error message that the id is not length 3
	*COMPUTE if the item exists in the array
	*IF the item exists in the item array
	*		PRINT message to prompt user to enter an memberId and advance borrow days
	*		IF the item is an instance of the Movie Class 
	*			COMPUTE the borrow days depending on the NewRelease attribute
	*			COMPUTE the rental fee by using the borrow method in the Movie class
	*			IF an idException or borrowException was thrown and caught, print the message
	*			ELSE Print an confirmation that the item was borrowed
	*		IF the item is an instance of the Games Class 	
	*			COMPUTE the borrow days
	*			COMPUTE the rental fee by using the borrow method in the Game class
	*		 	IF an idException or borrowException was thrown and caught print the message
	*			ELSE Print an confirmation that the item was borrowed
	*ELSE 
	*		PRINT an error message that the item was not found in the items array
	*END
	*/
	public void borrowItem() {
		String id;
		int borrowDays = 0;
		boolean existItem = false;
		int itemIndex;
		double rentalFee = 0;
		nullIndexValue = nullIdexValue();

		//Prompts user to enter an id but if the id is not of length 3 then an error message
		//will be print onto the console
		System.out.print("Enter id:");
		id = scanner.nextLine().toUpperCase();
		if (id.length() != ID_LENGTH ) {
			System.out.println("Error - The Id \"" + id + "\" is invalid. Please enter a 3 digit id.");
			return;
		}

		// Searches through the array of movies and tries to match the Id
		//If the id matches, existMovie will be change to true and the index of the 
		//item will be assigned to itemIndex. If the item does not exist an error message will be printed onto the console
		double indexValue = idMatch(id, nullIndexValue);
		if (!Double.isNaN(indexValue)) {
			existItem = true;
		}

		itemIndex = (int) indexValue;
		// If the item exists, prompt the user to enter their user id
		// and the number of days they will borrow in advance
		if (existItem == true) {
			System.out.print("Enter Member id:");
			String memberid = scanner.nextLine().toUpperCase();
			System.out.print("Advance borrow (days):");
			int daysinadvance = Integer.parseInt(scanner.nextLine());

			// Separate code for movie and game item types
			//If the item type is a movie it will calculate the number of days the item
			//can be borrowed for without it being late and calls the borrow method of the movie class
			//giving the borrow days details and the member id. It prints a confirmation message with the user is successful
			// in borrowing a movie. The same is done with the games object, however with an extra extended hire question.
			//If any exceptions are caught the program will print out the error message
			if (items[itemIndex] instanceof Movie) {
				Movie MovieItem = (Movie) items[itemIndex];

				if (MovieItem.getisNewRelease()) {
					borrowDays = NEW_RELEASE_RENTAL_PERIOD;
				} else {
					borrowDays = WEEKLY_RENTAL_PERIOD;
				}

				try {
					rentalFee = MovieItem.borrow(memberid, daysinadvance);
					DateTime dueDate = new DateTime(new DateTime(daysinadvance), borrowDays);
					System.out.println("The item " + items[itemIndex].getIdNoPrefix() + " costs $" + formatter.format(rentalFee)
							+ " and is due on " + dueDate.getFormattedDate());
				} catch (BorrowException | IdException e) {
					System.out.println(e.getMessage());
				}
			}

			else if (items[itemIndex] instanceof Game) {
				System.out.print("Extended hire?:");
				String inputExtended = scanner.nextLine().toUpperCase();
				while (!inputExtended.equals("Y") && !inputExtended.equals("N") && !inputExtended.equals("")) {
					System.out.println("Error: You must enter:" + "\'" + "Y" + "\'" + "or" + "\'" + "N" + "\'");
					System.out.print("Is Extended hire?  (Y/N):");
					inputExtended = scanner.nextLine();
				}
				if (inputExtended.equals("")) {
					System.out.println("Exiting to main Menu");

				}

				Game GameItem = (Game) items[itemIndex];
				borrowDays =  GAME_RENTAL_PERIOD;
				try {
					rentalFee = GameItem.borrow(memberid, inputExtended, daysinadvance);
					DateTime dueDate = new DateTime(new DateTime(daysinadvance), borrowDays);
					System.out.println("The item " + items[itemIndex].getIdNoPrefix() + " costs $" + formatter.format(rentalFee)
							+ " and is due on: " + dueDate.getFormattedDate());
				} catch (BorrowException | IdException e) {
					System.out.println(e.getMessage());
				}

			}

		} else if (existItem != true) {
			System.out.println("Error - The item with id number: " + id + " , not found");
		}

	}
	/*
	* ReturnItem() ALGORITHM
	* BEGIN
	* PRINT message to prompt user to enter an ID
	* IF length of id was not length 3 
	*		PRINT an error message that the id is not length 3
	*COMPUTE if the item exists in the array
	*IF item exist in the array
	*		COMPUTE if the item is on loan
	*IF the item is not returnable
	*		PRINT an error message that the item can be returned as the item as not borrowed
	*ELSEIF the item was not found in the items array
	*		PRINT an error message that the item was not found in the items array
	*ELSE
	*	PRINT message to prompt user to enter the number of days on loan
	*	COMPUTE the return date
	*	COMPUTE the total fee by calling the appropriate returnItem method from the specific class, feeding in the return date
	*	PRINT an confirmation that the item was returned
	*END
	*/
	public void returnItem() {
		String id;
		int itemIndex;
		boolean existMovie = false;
		boolean returnable = true;
		double totalFee = 0;
		nullIndexValue = nullIdexValue();
		System.out.print("Enter id:");
		
		id = scanner.nextLine().toUpperCase();
		if (id.length() != ID_LENGTH ) {
			System.out.println("Error - The Id \"" + id + "\" is invalid. Please enter a 3 digit id.");
			return;
		}

		// Searches through the array of movies and tries to match the Id
		//If the id matches, existMovie will be change to true and the index of the 
		//item will be assigned to itemIndex. And then it  will be checked if it is on loan, to assign the "returnable" value
		//If the item is not returnable because it's on loan or does not exist an error message will be printed onto the console
		double indexValue = idMatch(id, nullIndexValue);
		itemIndex = (int) indexValue;
		if (!Double.isNaN(indexValue)) {
			existMovie = true;
			if (items[itemIndex].getCurrentlyBorrowed() == null) {
				returnable = false;
			}
		}

		if (returnable == false) {
			System.out.println("Error: The item the id :" + items[itemIndex].getId() + " is NOT currently on loan");
		} else if (existMovie == false) {
			System.out.println("Error - The item with id: " + id + " , not found");
		} else {
			//If the item is returnable and the movie exists the user will be prompt with the number of days one loan
			//the value will then be added to the borrow date and the return date will be given to the
			//appropriate return method 
			//If the movie is successfully returned a confirmation message will be printed to the console
			System.out.print("Enter number of days on loan: ");
			int daysOnLoan = Integer.parseInt(scanner.nextLine());

			DateTime returnDate = new DateTime(items[itemIndex].getCurrentlyBorrowed().getborrowDate(), daysOnLoan);

			if (items[itemIndex] instanceof Movie) {
				Movie MovieItem = (Movie) items[itemIndex];
				totalFee = MovieItem.returnItem(returnDate);
			}

			else if (items[itemIndex] instanceof Game) {
				Game GameItem = (Game) items[itemIndex];
				totalFee = GameItem.returnItem(returnDate);
			}
		}

		if (!Double.isNaN(totalFee) && totalFee != 0) {
			System.out.println("The total fee payable is $ " + formatter.format(totalFee));
		}

	}

	/*
	* getDetails() ALGORITHM
	* BEGIN
	* COMPUTE nullIndexValue
	* CREATE a string builder
	* FOR every item in the items array
	* 		COMPUTE details of the item by calling the getDetails method of the Movie or Game class
	* 		APPEAND the details into the string builder
	* PRINT the details in the string builder 
	*END
	*/
	public void getDetails() {
		int itemIndex;
		nullIndexValue = nullIdexValue();
		//Builds a string containing all the details of every item
		//by calling the getDetails for every item, using the correct method
		//based on whether it is a game or movie and appending it to one string
		//This string is then printed and displays all the item related information contained
		//in all the classes
		StringBuilder Details = new StringBuilder(5000);
		for (itemIndex = 0; itemIndex < nullIndexValue; itemIndex++) {
			if (items[itemIndex] instanceof Movie) {
				Movie MovieItem = (Movie) items[itemIndex];
				Details.append(MovieItem.getDetails());
			} else if (items[itemIndex] instanceof Game) {
				Game GameItem = (Game) items[itemIndex];
				Details.append(GameItem.getDetails());
			}
		}
		System.out.println(Details.toString());
	}

	
	/*
	* seedData()  ALGORITHM
	* BEGIN
	* ADD items in the Item array
	* BORROW AND RETURN items depending on their specification
	*END
	*/
	public void seedData() {
		//Adds all the movie's details in the item array, all with different item indexes and only
		//executing if the first item in the item array is empty, which would also mean the entire array is empty
		if (items[0] == null) {
			try {
				items[0] = new Movie("DPL", "Deadpool", "Action",
						"Wade Wilson (Ryan Reynolds) is a former Special Forces operative who now works as a mercenary.",
						false);
				items[1] = new Movie("THR", "Thor", "Action",
						"As the son of Odin (Anthony Hopkins), king of the Norse gods, Thor (Chris Hemsworth) will soon inherit the throne of Asgard from his aging father.",
						false);
				items[2] = new Movie("CAP", "Captain America", "Action",
						"It is 1941 and the world is in the throes of war. Steve Rogers (Chris Evans) wants to do his part and join America's armed forces, but the military rejects him because of his small stature.",
						false);
				items[3] = new Movie("IRO", "Iron Man", "Action",
						"A billionaire industrialist and genius inventor, Tony Stark (Robert Downey Jr.), is conducting weapons tests overseas, but terrorists kidnap him to force him to build a devastating weapon. ",
						false);
				items[4] = new Movie("SPI", "Spiderman", "Action",
						"Under the watchful eye of mentor Tony Stark, Parker starts to embrace his newfound identity as Spider-Man. ",
						false);
				items[5] = new Movie("SOL", "Solo", "Action",
						"Through a series of daring escapades, young Han Solo meets his future co-pilot Chewbacca and encounters the notorious gambler Lando Calrissian.",
						true);
				items[6] = new Movie("DP2", "Deadpool2", "Action",
						"Wisecracking mercenary Deadpool joins forces with three mutants -- Bedlam, Shatterstar and Domino -- to protect a boy from the all-powerful Cable.",
						true);
				items[7] = new Movie("BLA", "BlackPanther", "Action",
						"After the death of his father, T'Challa returns home to the African nation of Wakanda to take his rightful place as king. ",
						true);
				items[8] = new Movie("ANT", "AntMan and the Wasp", "Action",
						"Scott Lang is grappling with the consequences of his choices as both a superhero and a father. ",
						true);
				items[9] = new Movie("RPO", "Ready Player One", "Action",
						"From filmmaker Steven Spielberg comes the science fiction action adventure Ready Player One, based on Ernest Clines bestseller of the same name, which has become a worldwide phenomenon.",
						true);
				items[10] = new Game("BF1", "Battlefield 1", "FPS",
						"Battlefield 1 is a first-person shooter video game developed by EA DICE and published by Electronic Arts.",
						new String[] { "PlayStation 4", " Xbox One", "Microsoft Windows" });
				items[11] = new Game("GOW", "God of War", "Action",
						"God of War is a third-person action-adventure game developed by Santa Monica Studio and published by Sony Interactive Entertainment. ",
						new String[] { "PlayStation 4" });
				items[12] = new Game("PUG", "PlayerUnknown's Battlegrounds", "FPS",
						"PlayerUnknown's Battlegrounds is a multiplayer online battle royale game developed and published by PUBG Corporation, a subsidiary of publisher Bluehole.",
						new String[] { " Xbox One", "Android", "iOS", "Microsoft Windows" });
				items[13] = new Game("OWH", "Overwatch", "FPS",
						"Overwatch is a team-based multiplayer first-person shooter video game developed and published by Blizzard Entertainment, ",
						new String[] { "PlayStation 4", " Xbox One", "Microsoft Windows" });
			} catch (IdException e1) {
				System.out.println(e1.getMessage());
			}
			//Casts the items into the respective classes and calls the 
			//borrow and return methods to match the specifications of
			//the movies and games
			Movie MovieItem2 = (Movie) items[1];
			Movie MovieItem3 = (Movie) items[2];
			Movie MovieItem4 = (Movie) items[3];
			Movie MovieItem5 = (Movie) items[4];
			Movie MovieItem7 = (Movie) items[6];
			Movie MovieItem8 = (Movie) items[7];
			Movie MovieItem9 = (Movie) items[8];
			Movie MovieItem10 = (Movie) items[9];

			Game GameItem2 = (Game) items[11];
			Game GameItem3 = (Game) items[12];
			Game GameItem4 = (Game) items[13];
			try {
				MovieItem2.borrow("MEM", 2);
				MovieItem3.borrow("MEM", 2);
				MovieItem3.returnItem(new DateTime(7));
				MovieItem4.borrow("MEM", 2);
				MovieItem4.returnItem(new DateTime(12));
				MovieItem5.borrow("MEM", 2);
				MovieItem5.returnItem(new DateTime(12));
				MovieItem5.borrow("MEY", 13);
				MovieItem7.borrow("MEM", 2);
				MovieItem8.borrow("MEM", 2);
				MovieItem8.returnItem(new DateTime(3));
				MovieItem9.borrow("MEM", 2);
				MovieItem9.returnItem(new DateTime(5));
				MovieItem10.borrow("MEM", 2);
				MovieItem10.returnItem(new DateTime(5));
				MovieItem10.borrow("MEY", 6);
				GameItem2.borrow("MEM", "N", 2);
				GameItem3.borrow("MEM", "N", 2);
				GameItem3.returnItem(new DateTime(21));
				GameItem4.borrow("MEM", "Y", 2);
				GameItem4.returnItem(new DateTime(34));
			} catch (BorrowException | IdException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	/*
	* getDetails() ALGORITHM
	* BEGIN
	* COMPUTE nullIndexValue
	* CREATE NEW FILE
	* FOR every item in the items array
	* 		PRINT in data using the toString method of the Movie or Games class
	* 		PRINT in data using the toString method of Hiring Record class
	* COPY DATA into backup file
	*END
	*/
	public void writeData() {
		nullIndexValue = nullIdexValue();
		int itemIndex;
		File filename = new File("src/Data.txt");
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException e1) {
			System.out.println(e1.getMessage());
		}
		//For every item in the item array the appropriate toString method is called 
		//which returns a string. This string is then written into the data file
		//is an way so that when it needs to be read it can do so in an orderly manner
		//The hire history is then added into the file until the every item in the 
		//items array in written into the file
		for (itemIndex = 0; itemIndex < nullIndexValue; itemIndex++) {
			if (items[itemIndex] instanceof Movie) {
				Movie MovieItem = (Movie) items[itemIndex];
				writer.println(MovieItem.toString());

				int nullIndexValueHire = 0;
				for (int k = 0; k < items[itemIndex].getHireHistory2().length; k++) {
					if (items[itemIndex].getHireHistory(k) == null) {
						break;
					} else {
						nullIndexValueHire += 1;
					}
				}
				for (int x = 0; x < nullIndexValueHire; x++) {
					writer.println(items[itemIndex].getHireHistory(x).toString());
				}
			}

			else if (items[itemIndex] instanceof Game) {
				Game GameItem = (Game) items[itemIndex];
				writer.println(GameItem.toString());

				int nullIndexValueHire = 0;
				for (int k = 0; k < items[itemIndex].getHireHistory2().length; k++) {
					if (items[itemIndex].getHireHistory(k) == null) {
						break;
					} else {
						nullIndexValueHire += 1;
					}
				}

				for (int x = 0; x < nullIndexValueHire; x++) {
					writer.println(items[itemIndex].getHireHistory(x).toString());
				}
			}
		}

		writer.close();
		
		//Once the file is complete the Data is then copied into a 
		//backup document and prints the program has ended
		Path sourceFile = Paths.get("src/Data.txt");
		Path BackupFile = Paths.get("src/Data_backup.txt");

		try {
			Files.copy(sourceFile, BackupFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("Program has ended");
		}
	}

	
	//Converts any DateTime that has been converted to 8 numbers, back into a DateTime object
	//and returns the new DateTime object
	public DateTime eightNumbersToDateTime(String eightNumbers) {
		DateTime Date = new DateTime(Integer.parseInt(eightNumbers.substring(0, 2)),
				Integer.parseInt(eightNumbers.substring(2, 4)), Integer.parseInt(eightNumbers.substring(4, 8)));
		return Date;

	}

	// Finds the index of the null value in the items array
	// This value will be need to determine the number of loops needed in the next
	// "for loop" as to not run into an Exception relating to the index of an array
	//the index value is then returned
	public int nullIdexValue() {
		nullIndexValue = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				break;
			}
			nullIndexValue += 1;
		}
		return nullIndexValue;
	}

	//Find the index of the item in the Items array
	//If the item is found return the index of the item
	//Else the item is not in the array and return NaN
	public double idMatch(String id, int nullIndexValue) {
		boolean existMovie = false;
		int i;
		for (i = 0; i < nullIndexValue; i++) {
			if (id.equals(items[i].getIdNoPrefix())) {
				existMovie = true;
				break;
			}
		}
		if (existMovie) {
			return i;
		} else {
			return Double.NaN;
		}
	}

}
