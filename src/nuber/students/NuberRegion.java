package nuber.students;

import java.util.concurrent.Future;

/**
 * A single Nuber region that operates independently of other regions, other than getting 
 * drivers from bookings from the central dispatch.
 * 
 * A region has a maxSimultaneousJobs setting that defines the maximum number of bookings 
 * that can be active with a driver at any time. For passengers booked that exceed that 
 * active count, the booking is accepted, but must wait until a position is available, and 
 * a driver is available.
 * 
 * Bookings do NOT have to be completed in FIFO order.
 * 
 * @author james
 *
 */

// Dispatch controls a number of different geographical regions, 
// e.g. one for North and one for South. Each region has a maximum number of bookings that can be running with a driver at any point. 
// If a job for a passenger starts in the North region, it counts towards North’s active job count for the entirety of the job, 
// i.e. it doesn’t start in the North region’s count, and end in the South region. Wherever the job begins, 
// that’s where it remains for counting purposes.

// When a new region is created, it is given a reference to the Dispatch object, the region’s name, 
// as well as the maximum number of simultaneous jobs that this region can perform. 
// You can think of this like having a pool of available jobs, and those jobs can be executed.

// When a region is asked to book a passenger by dispatch, it creates a new Booking object, and adds the booking ready for processing.

public class NuberRegion {

	private NuberDispatch dispatch;
	private String regionName;
	public int maxSimultaneousJobs;
	private int currentSimultaneousJobs;
	ExecutorService executor;
	private boolean shutDown = false;
	/**
	 * Creates a new Nuber region
	 * 
	 * @param dispatch The central dispatch to use for obtaining drivers, and logging events
	 * @param regionName The regions name, unique for the dispatch instance
	 * @param maxSimultaneousJobs The maximum number of simultaneous bookings the region is allowed to process
	 */
	public NuberRegion(NuberDispatch dispatch, String regionName, int maxSimultaneousJobs)
	{
		this.executor = Executors.newFixedThreadPool(maxSimultaneousJobs);
		this.dispatch = dispatch;
		this.regionName = regionName;
		this.maxSimultaneousJobs = maxSimultaneousJobs;
	}
	
	/**
	 * Creates a booking for given passenger, and adds the booking to the 
	 * collection of jobs to process. Once the region has a position available, and a driver is available, 
	 * the booking should commence automatically. 
	 * 
	 * If the region has been told to shutdown, this function should return null, and log a message to the 
	 * console that the booking was rejected.
	 * 
	 * @param waitingPassenger
	 * @return a Future that will provide the final BookingResult object from the completed booking
	 */
	public Future<BookingResult> bookPassenger(Passenger waitingPassenger)
	{		
		if(shutDown){
			System.out.println("[NuberRegion]" + regionName+": Booking Rejected - Shutting Down.");
			return null;
		}else if (currentSimultaneousJobs >= maxSimultaneousJobs) {
			System.out.println("[NuberRegion]" + regionName+": Booking Rejected - maxSimultaneousJobs reached.");
			return null;
		}else {
			currentSimultaneousJobs++;
			Booking booking = new Booking(dispatch, waitingPassenger);
			dispatch.addBooking(booking);
			return booking.call();
		}
	}
	
	/**
	 * Called by dispatch to tell the region to complete its existing bookings and stop accepting any new bookings
	 */
	public void shutdown()
	{
		shutDown = true;
		System.out.println("[NuberRegion]" + regionName+": Starting to Shut down...");
	}
		
}
