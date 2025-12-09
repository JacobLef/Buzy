package app;

/**
 * Driver class for the Spring Boot application
 * Delegates to BusinessManagementApplication as the main Spring Boot entry point
 * 
 * @author Amit Singh Tomar
 */
public class Driver {

	public static void main(String[] args) {
		System.out.println("============Spring Boot Application Start===================\n\n");
		
		
		BusinessManagementApplication.main(args);
		
		System.out.println("\n\n============Spring Boot Application Running===================");
	}

}

