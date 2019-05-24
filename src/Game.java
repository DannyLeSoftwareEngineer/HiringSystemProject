import java.util.Scanner;
import java.util.Arrays;

/*
* Class: Game
* Description: The class represents a single Game item for
* 				any type of item that can be hired.
* 				It is also contains functions that are needed to modify the hiring record
* 				and build a string containing the information contained in the game class.
* Author: [Danny le] - [s3722067]
*/
public class Game extends Item {
	private final static double RENTAL_SURCHARGE = 7.00;
	private final static double EXTENDED_MULITIPLIER = 0.50;
	private final static double NOT_EXTENDED_MULITIPLIER = 1.00;
	private final int RENTAL_PERIOD = 7;
	private String[] platforms = new String[100];
	private boolean extended;
	Scanner scanner = new Scanner(System.in);

	public Game(String id, String title, String genre, String description, String[] platforms) throws IdException {
		super("G_" + id, title, genre, description, RENTAL_SURCHARGE);
		this.platforms = platforms;
	}

	/*
	* borrow ALGORITHM
	* BEGIN
	* IF input of Extended is Y
	* 		SET extended to true
	* ELSE input of Extended is N
	* 		SET extended to false
	* CALL the super borrow method
	* IF an exception is thrown and caught, throw the same exception
	* RETURN borrowingFee
	* END
	*/
	public double borrow(String memberid, String inputExtended, int daysinadvance) throws BorrowException, IdException {
		//Sets the borrowing fee by prompting the user to enter whether the hire is extended
		//then calling the item borrow method and returns the borrowing fee
		//If a borrow exception was thrown and caught, it will throw the exception again to the
		//movie master class to handle
		if (inputExtended.equals("Y")) {
			this.extended = true;
		} else if (inputExtended.equals("N")) {
			this.extended = false;
		}
		double borrowingFee;
		try {
			borrowingFee = super.borrow(memberid, new DateTime(daysinadvance));
		} catch (BorrowException e) {
			throw new BorrowException(e.getMessage());
		}
		return borrowingFee;
	}

	/*
	* returnItem ALGORITHM
	* BEGIN
	* COMPUTE the difference in days
	* IF difference in days is greater or equal to 0 
	* 		COMPUTE the borrowDays 
	* 		COMPUTE number of late days
	* 		IF number of late days is greater than 0
	* 			COMPUTE extended multiplier
	* 			COMPUTE the late fee
	* 			CALL the super method returnItem
	* 		ELSE
	* 			SET late fee to 0
	* 			CALL the super method returnItem
	* ELSE 
	* 		PRINT an error message as the item cannot be return before the borrow date
	* RETURN total fee
	* END
	*/
	public double returnItem(DateTime returnDate) {
		int borrowDays;
		double lateFee;
		double extendedMultiplier;
		double totalFee = 0;
		// Finds the difference in days based on the return date and the borrow date if it
		// greater than or equal to zero than the rest of the code is ran, however if the difference is days is
		//it is not valid as the user is try to return the item before the borrow date and an error message is printed to the console
		int differenceInDays = DateTime.diffDays(returnDate, getCurrentlyBorrowed().getborrowDate());
		
		// Calculates extended multiplier  to then calculate the late fee which is then sent to the 
		//return item method of the item class to calculate the total fee which is then returned
		if (differenceInDays >= 0) {
			borrowDays = RENTAL_PERIOD;
			int noOfLateDays = DateTime.diffDays(returnDate,
					new DateTime((getCurrentlyBorrowed().getborrowDate()), borrowDays));

			if (noOfLateDays > 0) {
				if (extended) {
					extendedMultiplier = EXTENDED_MULITIPLIER;
				} else {
					extendedMultiplier = NOT_EXTENDED_MULITIPLIER ;
				}
				lateFee = (((1 * noOfLateDays) + (5 * (noOfLateDays / 7))) * extendedMultiplier);
				totalFee = super.returnItem(returnDate, lateFee);
			} else {
				lateFee = 0;
				totalFee = super.returnItem(returnDate, lateFee);
			}
		} else {
			System.out.println("Error -Can not be returned before the borrow date");
			totalFee = Double.NaN;
		}
		return totalFee;
	}

	/*
	* getDetails ALGORITHM
	* BEGIN
	* CREATE new string builder
	* APPEAD details passed by the getDeatils method in the items class
	* IF	Game is currently borrowed and is on extended hire
	* 		SET string that represents the loan status to "Extended"
	* ELSE IF the game is only currently borrowed
	* 		SET string that represents the loan status to "True"
	* ELSE IF the game in not on loan
	* 		SET string that represents the loan status to "False"
	* APPEND details to the string builder
	* APPEND details passed by the getDeatils2 method in the items class
	* RETURN string builder to string
	* END
	*/
	public String getDetails() {

		String onloan = null;

		StringBuilder Details = new StringBuilder(1000);
		Details.append(super.getDetails());

		if (getCurrentlyBorrowed() != null && extended) {
			onloan = "Extended";
		} else if (getCurrentlyBorrowed() != null) {
			onloan = "True";
		} else if (getCurrentlyBorrowed() == null) {
			onloan = "False";
		}

		Details.append(String.format("%-25s %s\n", "Platforms:", Arrays.toString(platforms)));
		Details.append(String.format("%-25s %s\n", "Rental Period:", RENTAL_PERIOD + " days"));
		Details.append(String.format("%-25s %s\n", "On loan:", onloan));
		Details.append(String.format("%25s %-25s\n", "", "Borrowing Record"));

		Details.append(super.getDetails2());

		return Details.toString();

	}

	/*
	* toString ALGORITHM
	* BEGIN
	* FOR the number of platforms in the platforms array
	* 		APPEND the platform name to a string
	*  IF the game has a currently borrowed record and is extended
	* 		SET hire type to "E"
	* ELSE the game has a currently borrowed record
	* 		SET hire type to "T"
	*ELSE IF currently borrowed is empty
	* 		SET loan status to "F"
	*  SET string to the formatted with the string given by the super class, the string of platforms and loan status
	* RETURN the formatted string
	* END
	*/
	public String toString() {
		String platform = "";
		for (int i = 0; i < platforms.length; i++) {
			platform = platform + platforms[i] + ",";
		}
		String loanStatus = null;
		if (getCurrentlyBorrowed() != null && extended) {
			loanStatus = "E";
		} else if (getCurrentlyBorrowed() != null) {
			loanStatus = "T";
		} else if (getCurrentlyBorrowed() == null) {
			loanStatus = "F";
		}
		String toString = String.format("%s:%s:%s\n", super.toString(), platform, loanStatus);
		return toString;
	}

	public void setExtened(boolean extended) {
		this.extended = extended;
	}
}
