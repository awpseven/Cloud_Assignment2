package nuber.students;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ArrayBlockingQueue;
/**
 * The core Dispatch class that instantiates and manages everything for Nuber
 *
 * @author james
 *
 */

// Dispatch handles everything to do with running an instance of Nuber:
// adding/providing drivers, booking passengers, and when required, shutting everything down.

// It contains a map of the regions it’s responsible for, and a central collection of Drivers awaiting a booking.
// It also has a variable, set by its constructor, for whether the reportEvent() function prints information out to the console.
// You can use this function to print out debugging information if required.


// When a passenger is booked into dispatch using bookPassenger(),
// dispatch is also told which region the job is in. Dispatch then needs to get that region,
// and book the passenger into that specific “Nuber Region” using the region’s bookPassenger() function.

// Drivers are allocated on a first-in, first-out basis. Because passengers are allocated to a specific region,
// different regions will be accessing a central collection of available drivers from Dispatch.

public class NuberDispatch {

	/**
	 * The maximum number of idle drivers that can be awaiting a booking
	 */
	private final int MAX_DRIVERS = 999;
	public final AtomicInteger ID = new AtomicInteger();

	private boolean logEvents = false;

	private HashMap<String, Integer> regionInfo;

	private BlockingQueue<Driver> driverList;
	private HashMap<String, NuberRegion> regionMap;

	private int awaitingDriver;
	private Future<BookingResult> futureResult;

	/**
	 * Creates a new dispatch objects and instantiates the required regions and any other objects required.
	 * It should be able to handle a variable number of regions based on the HashMap provided.
	 *
	 * @param regionInfo Map of region names and the max simultaneous bookings they can handle
	 * @param logEvents Whether logEvent should print out events passed to it
	 */
	public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents)
	{
		this.futureResult = null;
		this.regionInfo = regionInfo;
		this.logEvents = logEvents;
		this.awaitingDriver = 0;
		this.driverList = new ArrayBlockingQueue<Driver>(MAX_DRIVERS);
		this.regionMap = new HashMap<String, NuberRegion>();

		System.out.println("[NuberDispatch] is creating " + this.regionInfo.size() + "Nuber Dispatch");
		regionInfo.forEach(
			(name, maxDelay) -> {
				System.out.println("Creating [NumberRegions]" + name + " ");

				NuberRegion _region = new NuberRegion(this, name, maxDelay);
				regionMap.put(name, _region);
				System.out.println("[NumberRegions]" + name + "  created successfully.");
			}
		);

	}

	/**
	 * Adds drivers to a queue of idle driver.
	 *
	 * Must be able to have drivers added from multiple threads.
	 *
	 * @param The driver to add to the queue.
	 * @return Returns true if driver was added to the queue
	 */
	public boolean addDriver(Driver newDriver)
	{
		try {
			driverList.put(newDriver);
			return true;
		} catch(Exception e) {
			System.out.println("[ERROR]Unable to getDriver():"+e.getMessage());
			return false;
		}
	}

	/**
	 * Gets a driver from the front of the queue
	 *
	 * Must be able to have drivers added from multiple threads.
	 *
	 * @return A driver that has been removed from the queue
	 */
	public Driver getDriver()
	{
		try {
			Driver valDriver = driverList.take();
			awaitingDriver--;
			return valDriver;
		}
		catch(Exception e) {
			System.out.println("[ERROR]Unable to getDriver():"+e.getMessage());
			return null;
		}
	}

	/**
	 * Prints out the string
	 * 	    booking + ": " + message
	 * to the standard output only if the logEvents variable passed into the constructor was true
	 *
	 * @param booking The booking that's responsible for the event occurring
	 * @param message The message to show
	 */
	public void logEvent(Booking booking, String message) {
		if (logEvents){
			System.out.println(booking + ": " + message);
		}
	}

	/**
	 * Books a given passenger into a given Nuber region.
	 *
	 * Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
	 *
	 * If the region has been asked to shutdown, the booking should be rejected, and null returned.
	 *
	 * @param passenger The passenger to book
	 * @param region The region to book them into
	 * @return returns a Future<BookingResult> object
	 */
	public Future<BookingResult> bookPassenger(Passenger passenger, String region) {
		NuberRegion allocatedRegion = regionMap.get(region);
		futureResult = allocatedRegion.bookPassenger(passenger);
		if(futureResult == null){
			return null;
		}else{
			awaitingDriver++;
			return futureResult;
		}
    }

	/**
	 * Gets the number of non-completed bookings that are awaiting a driver from dispatch
	 *
	 * Once a driver is given to a booking, the value in this counter should be reduced by one
	 *
	 * @return Number of bookings awaiting driver, across ALL regions
	 */
	public int getBookingsAwaitingDriver()
	{
		return awaitingDriver;
	}

	/**
	 * Tells all regions to finish existing bookings already allocated, and stop accepting new bookings
	 */
	public void shutdown() {
		regionMap.forEach(
				(key, region) -> {
					System.out.println("[NuberDipatch] Shutting down [Region]" + region.regionName + "...");
					region.shutdown();
				}
		);
	}
}
