/*
* Class: HiringRecord
* Description: The class represents a single hiring record for
* 				any type of item that can be hired.
* 				It is also contains functions that are needed to modify the hiring record
* 				and build a string containing the information contained in the hiring record.
* Author: [Danny le] - [s3722067]
*/
public class HiringRecord {
	private String id;
	private double rentalFee;
	private double lateFee;
	private DateTime borrowDate;
	private DateTime returnDate;
	
	public HiringRecord(String id, String memberId, double rentalFee, DateTime borrowDate) {
		this.id = id + "_" + memberId + "_" + borrowDate.getEightDigitDate();
		this.rentalFee = rentalFee;
		this.lateFee = 0;
		this.borrowDate = borrowDate;
	}

	/*
	* returnItem ALGORITHM
	* BEGIN
	* SET the return date
	* SET the late fee
	* IF the late fee is not negative
	* 		COMPUTE the total fee
	* RETURN total fee
	* END
	* 
	* TEST
	* 				LateFee = 10.0 and rentalFee = 7.0, then total fee is 17.0
	* 				LateFee < 0 and rentalFee = 7.0, the code is not ran
	*/
	public double returnItem(DateTime returnDate, double LateFee) {
		/*
		 * Calculates the total fee by first verifying that the late fee is equal
		 * or greater than zero.
		 * Then adds the rental fee and late fee to calculate the total fee
		 * which is then returned
		 */
		double totalFee = 0;
		this.returnDate = returnDate;
		this.lateFee = LateFee;
		if (lateFee >= 0) {
			totalFee = LateFee + rentalFee;
		}
		return totalFee;
	}

	/*
	* getDetails ALGORITHM
	* BEGIN
	* CREATE string builder
	* APPEND the hire id and the borrow date
	* IF the return date is not null
	* 		APPEAD the return date, rental fee, late fee and the total fee
	* RETURN string builder to string
	* END
	*/
	public String getDetails() {
		//Since the Hire ID and borrow date is always appended there is on need to add
		//an if statement, however is the Hiring record has a return date
		//other details such as late fee and total fee is also appends into the string builder
		//the and string builder is returned as a string
		StringBuilder hiringDetails = new StringBuilder(1000);
		hiringDetails.append(String.format("%23s  %-25s", "", "_________________________________________\n"));
		hiringDetails.append(String.format("%25s %-25s %s\n", "", "Hire ID:", id));
		hiringDetails.append(String.format("%25s %-25s %s\n", "", "Borrow Date:", borrowDate.getFormattedDate()));
		if (returnDate != null) {

			hiringDetails.append(String.format("%25s %-25s %s\n", "", "Return Date:", returnDate.getFormattedDate()));
			hiringDetails.append(String.format("%25s %-25s $%.2f\n", "", "Fee:", rentalFee));
			hiringDetails.append(String.format("%25s %-25s $%.2f\n", "", "Late Fee:", lateFee));
			hiringDetails.append(String.format("%25s %-25s $%.2f\n", "", "Total Fee:", rentalFee + lateFee));
		}

		hiringDetails.append(String.format("%23s  %-25s", "", "_________________________________________\n"));
		return hiringDetails.toString();

	}

	/*
	* toString ALGORITHM
	* BEGIN
	* IF return date is null
	* 		CREATE a formated string with id and borrow date, replacing any retails the class does not have with "none"
	* ELSE
	* 		CREATE a formated string with id, borrow date, return date, fee and late fee
	* RETURN formatted string
	* END
	*/
	public String toString() {
		String toString;
		if (returnDate == null) {
			toString = String.format("%s:%s:%s:%s:%s\n", id, borrowDate.getEightDigitDate(), "none", "none", "none");

		} else {
			toString = String.format("%s:%s:%s:%.2f:%.2f\n", id, borrowDate.getEightDigitDate(),
					returnDate.getEightDigitDate(), rentalFee, lateFee);
		}
		return toString;
	}

	public DateTime getborrowDate() {
		return this.borrowDate;
	}

	public DateTime getreturnDate() {
		return returnDate;
	}

}