import java.util.Scanner;

/*
* Class: Movie
* Description: The class represents a single Movie item for
* 				any type of item that can be hired.
* 				It is also contains functions that are needed to modify the hiring record
* 				and build a string containing the information contained in the Movie class.
* Author: [Danny le] - [s3722067]
*/
public class Movie extends Item {
	private boolean isNewRelease;
	private final static double NEW_RELEASE_SURCHARGE = 5.00;
	private final int NEW_RELEASE_RENTAL_PERIOD = 2;
	private final static double WEEKLY_SURCHARGE = 3.00;
	private final int WEEKLY_RENTAL_PERIOD = 7;
	Scanner scanner = new Scanner(System.in);

	public Movie(String id, String title, String genre, String description, boolean isNewRelease) throws IdException {
		super("M_" + id, title, genre, description, (isNewRelease ? NEW_RELEASE_SURCHARGE : WEEKLY_SURCHARGE));
		this.isNewRelease = isNewRelease;
	}

	/*
	* borrow ALGORITHM
	* BEGIN
	* CALL the super borrow method
	* IF an exception is thrown and caught, throw the same exception
	* RETURN borrowingFee
	* END
	*/
	public double borrow(String memberid, int daysinadvance) throws BorrowException, IdException {
		//Sets the borrowing fee by calling the item borrow method and returns the borrowing fee
		//If a borrow exception was thrown and caught, it will throw the exception again to the
		//movie master class to handle
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
	* 		COMPUTE the borrowDays and rentalFee depending on the isNewReleaseValue
	* 		COMPUTE number of late days
	* 		IF number of late days is greater than 0
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
		double rentalFee;
		double totalFee = 0;

		// Finds the difference in days based on the return date and the borrow date if it
		// greater than or equal to zero, then the rest of the code is ran, however if the difference is days is
		//it is not valid as the user is try to return the item before the borrow date and an error message is printed to the console
		int differenceInDays = DateTime.diffDays(returnDate, getCurrentlyBorrowed().getborrowDate());
		
		// Calculates the  borrow days and borrow days to then calculate the late fee which is then sent to the 
		//return item method of the item class to calculate the total fee which is then returned
		if (differenceInDays >= 0) {
			if (isNewRelease == true) {
				borrowDays = NEW_RELEASE_RENTAL_PERIOD;
				rentalFee = NEW_RELEASE_SURCHARGE;
			} else {
				borrowDays = WEEKLY_RENTAL_PERIOD;
				rentalFee = WEEKLY_SURCHARGE;
			}

			int noOfLateDays = DateTime.diffDays(returnDate,
					new DateTime((getCurrentlyBorrowed().getborrowDate()), borrowDays));
			if (noOfLateDays > 0) {
				lateFee = (rentalFee / 2) * noOfLateDays;
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
	* APPEAD details passed my the getDeatils method in the items class
	* IF	movie is a new release
	* 		COMPUTE string that represents new release status and rent period
	* ELSE
	* 		COMPUTE string that represents new release status and rent period
	* COMPUTE whether the movie is on loan
	* APPEND details to the string builder
	* APPEND details passed by the getDeatils2 method in the items class
	* RETURN string builder to string
	* END
	*/
	public String getDetails() {

		boolean onloan = true;
		String movieRelease;
		int rentPeriod;


		StringBuilder Details = new StringBuilder(1000);
		Details.append(super.getDetails());

		if (getisNewRelease() == true) {
			movieRelease = "New Release";
			rentPeriod = NEW_RELEASE_RENTAL_PERIOD;
		} else {
			movieRelease = "Weekly";
			rentPeriod = WEEKLY_RENTAL_PERIOD;
		}
		if (getCurrentlyBorrowed() == null) {
			onloan = false;
		}

		Details.append(String.format("%-25s %s\n", "On loan:", onloan));
		Details.append(String.format("%-25s %s\n", "Movie Type:", movieRelease));
		Details.append(String.format("%-25s %s\n", "Rental Period:", rentPeriod + " days"));
		Details.append(String.format("%25s %-25s\n", "", "Borrowing Record"));

		Details.append(super.getDetails2());

		return Details.toString();

	}

	/*
	* toString ALGORITHM
	* BEGIN
	* IF the movie is  a new release
	* 		SET hire type to new release
	* ELSE the movie is not a new release
	* 		SET hire type to weekly
	* IF currently borrowed is empty
	* 		SET loan status to "N"
	* ELSE IF there is a currently borrowed record
	* 		SET loan status to "Y"
	* SET string to the formatted with the string given by the super class, the string hire type and loan status
	* RETURN the formatted string
	* END
	*/
	public String toString() {
		String hireType = null;
		String loanStatus;
		if (isNewRelease = true) {
			hireType = "NR";
		} else if (isNewRelease = false) {
			hireType = "WK";
		}
		if (getCurrentlyBorrowed() == null) {
			loanStatus = "N";
		} else {
			loanStatus = "Y";
		}

		String toString = String.format("%s:%s:%s\n", super.toString(), hireType, loanStatus);

		return toString;
	}

	public boolean getisNewRelease() {
		return isNewRelease;
	}

}