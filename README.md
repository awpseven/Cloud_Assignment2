# Cloud_Assignment2
Cloud and Concurrent Programming - Assigngment 2
The github Link is in https://github.com/awpseven/Cloud_Assignment2

In order to test, decomment one of the simulation	which is in AssignmentDriver Line 96 - Line 73


HashMap<String, Integer> regions = new HashMap<String, Integer>();  
		regions.put("North", 50);  
		regions.put("South", 50);  
  
//		new Simulation(regions, 1, 10, 10, logEvents);  
//		new Simulation(regions, 5, 10, 10, logEvents);  
//		new Simulation(regions, 10, 10, 10, logEvents);  
//		new Simulation(regions, 10, 100, 10, logEvents);  
		new Simulation(regions, 1, 50, 10, logEvents);  
