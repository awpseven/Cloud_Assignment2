package nuber.students;
// Driver inherits from Person (so we can store the person’s name and maxSleep). 
// It provides a function called pickUpPassenger that takes in a Passenger as an argument, 
// stores it into a private variable, and then sleeps the current thread for a delay between 0-maxDelay milliseconds. 
// The class has another function driveToDestination(), 
// that sleeps the current thread for a delay based on the current passenger’s getTravelTime().

public class Driver extends Person {

	private Passenger currentPassenger;
	
	public Driver(String driverName, int maxSleep)
	{
		super(driverName, maxSleep);
	}
	
	/**
	 * Stores the provided passenger as the driver's current passenger and then
	 * sleeps the thread for between 0-maxDelay milliseconds.
	 * 
	 * @param newPassenger Passenger to collect
	 * @throws InterruptedException
	 */
	public void pickUpPassenger(Passenger newPassenger) throws InterruptedException
	{
		this.currentPassenger = newPassenger;
		int pickUpTime = (int)(Math.random() * maxSleep);
		Thread.sleep(pickUpTime * 1000L);
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current 
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	public void driveToDestination() throws InterruptedException {
		int time = this.currentPassenger.getTravelTime();
		Thread.sleep(time * 1000L);
	}
	
}
