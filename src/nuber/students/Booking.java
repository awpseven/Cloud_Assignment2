package nuber.students;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Callable;

/**
 * 
 * Booking represents the overall "job" for a passenger getting to their destination.
 * 
 * It begins with a passenger, and when the booking is commenced by the region 
 * responsible for it, an available driver is allocated from dispatch. If no driver is 
 * available, the booking must wait until one is. When the passenger arrives at the destination,
 * a BookingResult object is provided with the overall information for the booking.
 * 
 * The Booking must track how long it takes, from the instant it is created, to when the 
 * passenger arrives at their destination. This should be done using Date class' getTime().
 * 
 * Booking's should have a globally unique, sequential ID, allocated on their creation. 
 * This should be multi-thread friendly, allowing bookings to be created from different threads.
 * 
 * @author james
 *
 */

// When a booking is made, it’s given a reference to the Nuber Dispatch, 
// as well as the passenger that’s made the booking, these should be stored as private variables. 
// Each Booking has a globally unique, sequential, job ID. The first job created should be number 1. 
// When a booking is created, the current time should be recorded so we can track how long a booking takes.


// When a booking is created, Nuber might not have enough resource available to start that booking immediately 
// (e.g. a region that has no available spots in it’s booking pool, or no drivers are available from Dispatch). 
// At some point, the Nuber Region responsible for the booking can start it (has free spot), 
// and calls the Booking.call() to carry out the booking (see the class code for more information). 

public class Booking implements Callable<BookingResult>{
	private final Passenger bookedPassenger;
	private Driver bookedDriver;
	private final NuberDispatch dispatch;
	private final int ID;
	/**
	 * Creates a new booking for a given Nuber dispatch and passenger, noting that no
	 * driver is provided as it will depend on whether one is available when the region 
	 * can begin processing this booking.
	 * 
	 * @param dispatch
	 * @param passenger
	 */
	public Booking(NuberDispatch dispatch, Passenger passenger)
	{
		this.ID = dispatch.ID.incrementAndGet();
		this.dispatch = dispatch;
		this.bookedPassenger = passenger;
		this.bookedDriver = null;
		System.out.println("[Create] Created Booking:" + this );
	}
	
	/**
	 * At some point, the Nuber Region responsible for the booking can start it (has free spot),
	 * and calls the Booking.call() function, which:
	 * 1.	Asks Dispatch for an available driver
	 * 2.	If no driver is currently available, the booking must wait until one is available. 
	 * 3.	Once it has a driver, it must call the Driver.pickUpPassenger() function, with the 
	 * 			thread pausing whilst as function is called.
	 * 4.	It must then call the Driver.driveToDestination() function, with the thread pausing 
	 * 			whilst as function is called.
	 * 5.	Once at the destination, the time is recorded, so we know the total trip duration. 
	 * 6.	The driver, now free, is added back into Dispatch’s list of available drivers. 
	 * 7.	The call() function the returns a BookingResult object, passing in the appropriate 
	 * 			information required in the BookingResult constructor.
	 *
	 * @return A BookingResult containing the final information about the booking 
	 */
	public BookingResult call() throws InterruptedException {
		System.out.println("[Booking]" + this + "- Start to request a driver");
		this.bookedDriver = dispatch.getDriver();
		System.out.println("[Booking]" + this + "- Driver ready and start to pick up the passenger");
		this.bookedDriver.pickUpPassenger(bookedPassenger);
		System.out.println("[Booking]" + this  + "- Picked up the passenger & drive to the destination");
		this.bookedDriver.driveToDestination();
		long tripDuration = bookedDriver.tripDuration;
		System.out.println("[Booking]" + this +"- trip finish with [TripDuration]" + tripDuration);
		if( this.dispatch.addDriver(bookedDriver)){
			System.out.println("[Booking]" + this +": Free the driver.");
		}else{
			System.out.println("[ERROR][Booking]" + this +"- Failed to free the driver.");
		}
        return new BookingResult(ID, bookedPassenger, bookedDriver, tripDuration);
	}
	
	/***
	 * Should return the:
	 * - booking ID, 
	 * - followed by a colon, 
	 * - followed by the driver's name (if the driver is null, it should show the word "null")
	 * - followed by a colon, 
	 * - followed by the passenger's name (if the passenger is null, it should show the word "null")
	 * 
	 * @return The compiled string
	 */
	@Override
	public String toString()
	{
		String driverName = bookedDriver == null ? "null" : bookedDriver.name;
		String passengerName = bookedPassenger == null ? "null" : bookedPassenger.name;
        return String.format("%d:%s:%s", ID, driverName, passengerName);
	}

}
