import java.util.Scanner;

/*
* Class: Item
* Description: The class represents any information and logic that is in common to 
* 				all items any type of item that can be hired.
* 				It is also contains functions that are needed to modify the hiring record
* 				and build a string containing the information contained in the item class.
* Author: [Danny le] - [s3722067]
*/
public class Item {
	private String id;
	private String title;
	private String genre;
	private String description;
	private double fee;
	private final int ID_LENGTH = 3;
	private final int ID_WITH_PREFIX_LENGTH = 5;
	private HiringRecord currentlyBorrowed;
	private HiringRecord[] hireHistory = new HiringRecord[10];
	Scanner scanner = new Scanner(System.in);

	public Item(String id, String title, String genre, String description, double fee) throws IdException {
		// If the id is invalid, throw an idException
		if (id.length() != ID_WITH_PREFIX_LENGTH) {
			throw new IdException(
					"Error - The Id \"" + id.substring(2, id.length()) + "\" is invalid. Please enter a 3 digit id.");
		}
		this.id = id;
		this.title = title;
		this.genre = genre;
		this.description = description;
		this.fee = fee;
	}

	/*
	* borrow ALGORITHM
	* BEGIN
	* IF member id is not length 3
	* 		THROW id Exception as the id needs to be length 3
	* IF borrow date is before today's date 
	* 		Throw borrow Exception as the member id needs to be length 3
	* IF currentlyBorrowed record does not exist
	* 		SET currentlyBorrowed to a new hiring record
	* 		IF the hiring record is full 
	* 			MOVE all the records so that the oldest record is discarded
	* 		ELSE
	* 			ADD the currentlyBorrowed record to the Hiring Record
	* ELSE
	* 		THROW borrow Exception as item is already on loan
	* RETURN fee
	* END
	*
	*/
	public double borrow(String memberid, DateTime borrowDate) throws BorrowException, IdException {
		int nullIndexValueHire = 0;
		int hireHistoryIndex;
		// If the member id is invalid, throw an idException
		// If the borrow date is before today's date throw an exception
		if (memberid.length() != ID_LENGTH) {
			throw new IdException("Error - The Id \"" + memberid + "\" is invalid. Please enter a 3 digit id.");
		}
		if (DateTime.diffDays(borrowDate, new DateTime()) < 0) {
			throw new BorrowException("Error- Can not borrow before today's date");
		}
		//Calculates if the item is on loan.
		//If it is on loan throw a borrow exception and it can't be borrowed
		//If it is not on loan then create a currently borrowed record and  add that record to the 
		// hire history. If the hire history is full move all the hire history indexes so that the oldest
		//record is discarded and the newest record is added. If the hire record is not full then add
		//the new record to the hiring record.
		//It also returns the fee
		if (this.currentlyBorrowed == null) {
			this.currentlyBorrowed = new HiringRecord(id, memberid, fee, borrowDate);
			for (int k = 0; k < hireHistory.length; k++) {
				if ((hireHistory[k]) == null) {
					break;
				} else {
					nullIndexValueHire += 1;
				}
			}
			if (nullIndexValueHire == hireHistory.length) {
				for (hireHistoryIndex = 0; hireHistoryIndex < hireHistory.length - 1; hireHistoryIndex++)
					hireHistory[hireHistoryIndex] = hireHistory[hireHistoryIndex + 1];
				if (hireHistoryIndex == hireHistory.length - 1) {
					hireHistory[hireHistory.length - 1] = currentlyBorrowed;
				}
			} else {
				hireHistory[nullIndexValueHire] = currentlyBorrowed;
			}
		}

		else if (currentlyBorrowed != null) {
			throw new BorrowException("The item with id " + id + " is currently on loan");
		}
		return fee;
	}

	/*
	* return ALGORITHM
	* BEGIN
	* FOR every hiring record 
	* IF if the hiring record is empty
	* 	BREAK out of the loop
	* ELSE
	* 		Add one to nullIndexValueHire
	* COMPUTE the total Fee by calling the return item method on the correct hiring record
	* SET the currentlyBorrowed to null
	* RETURN the totalFee
	* END
	*
	*/
	public double returnItem(DateTime returnDate, double lateFee) {
		int nullIndexValueHire = 0;
		//Finds the correct hire history record in the hire history array
		//and calls the returnItem method to get the total fee
		//and returns the total fee
		for (int k = 0; k < hireHistory.length; k++) {
			if (hireHistory[k] == null) {
				break;
			} else {
				nullIndexValueHire += 1;
			}
		}
		double totalFee = hireHistory[nullIndexValueHire - 1].returnItem(returnDate, lateFee);
		currentlyBorrowed = null;
		return totalFee;
	}

	/*
	* getDetails ALGORITHM
	* BEGIN
	*CREATE new string builder
	*APPEND add details share by the Movie and Game class
	*RETURN the string builder as a string
	* END
	*
	*/
	public String getDetails() {
		StringBuilder Details = new StringBuilder(1000);
		Details.append(String.format("%-25s %s\n", "ID:", id));
		Details.append(String.format("%-25s %s\n", "Title:", title));
		Details.append(String.format("%-25s %s\n", "Genre:", genre));
		Details.append(String.format("%-25s %s\n", "Description:", description));
		Details.append(String.format("%-25s $%.2f\n", "Standard Fee:", fee));
		return Details.toString();
	}

	
	/*
	* getDetails2 ALGORITHM
	* BEGIN
	*CREATE new string builder
	*COMPUTE the index of the hireHistory 
	*IF the hiring record is empty then print there is no borrowing record
	*		APPEND the string that represents that there is no borrowing record
	*ELSE calls the get details of the hiring record class
	*		APPEND the hiring record
	*RETURN the string builder as a string
	* END
	*
	*/
	public String getDetails2() {
		int nullIndexValueHire = 0;
		StringBuilder Details = new StringBuilder(1000);
		for (int k = 0; k < hireHistory.length; k++) {
			if (hireHistory[k] == null) {
				break;
			} else {
				nullIndexValueHire += 1;
			}
		}
		if (nullIndexValueHire == 0) {
			Details.append(String.format("%23s  %-25s", "", "_________________________________________\n"));
			Details.append(String.format("%25s %-25s\n", "", "Borrowing Record"));
			Details.append(String.format("%25s %-25s\n", "", "None"));
			Details.append(String.format("%23s  %-25s", "", "_________________________________________\n"));
		}
		for (int k = nullIndexValueHire; k > 0; k--) {
			if ((nullIndexValueHire == 0)) {
				Details.append(String.format("%25s %-25s", "", "Borrowing Record"));
				Details.append(String.format("%25s %-25s", "", "None"));
			} else {
				Details.append(hireHistory[k - 1].getDetails());
			}
		}

		return Details.toString();
	}

	/*
	* toString ALGORITHM
	* BEGIN
	* CREATE a formatted a string with details shared by both Game and Movie class
	* RETURN the formatted string
	* END
	*/
	public String toString() {
		String toString = String.format("%s:%s:%s:%s:%s", id, title, description, genre, fee);
		return toString;
	}

	public String getIdNoPrefix() {
		String id2 = this.id.substring(2, 5);
		return id2;
	}

	public String getId() {
		return this.id;
	}

	public HiringRecord getCurrentlyBorrowed() {
		return currentlyBorrowed;
	}

	public HiringRecord getHireHistory(int i) {
		return hireHistory[i];
	}

	public HiringRecord[] getHireHistory2() {
		return hireHistory;
	}

}
