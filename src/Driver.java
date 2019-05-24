/*
* Class: Driver
* Description: The class that reads in the data, starts the program and
* 				gets out of the static method.
* Author: [Danny le] - [3722067]
*/
public class Driver {

	public static void main(String[] args) {
		MovieMaster MovieMaster = new MovieMaster();
		MovieMaster.readData();
		MovieMaster.Menu();
	}

}
