import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

//Population initialization
public class Population {
    private static VRPInstance instance;
    private static Random random;
    public static ArrayList<Chromosome> initializePopulation(int PopSize, VRPInstance vrpInstance){
        instance = vrpInstance;
        ArrayList<Chromosome> population = new ArrayList<>();
        ArrayList<ArrayList<Integer>> chromosome=new ArrayList<>();
        HashSet<Integer> customerInRoute = new HashSet<>();
        random = new Random();
        //Gets the closest node the depot.
        // route here is only used to get the closest node to depot to start a route
        int nextCustomer = getNearestCustomer(new Route(0,instance.getNodes().get(0),instance.getNodesDistance().get("1,0")),new HashSet<>(){{add(0);}});
        //add customer to customer assigned to a route.
        //node is never the depot
        customerInRoute.add(nextCustomer);
        //Create a new route with the closest node to the depot
        Route r =  new Route(nextCustomer,instance.getNodes().get(nextCustomer),instance.getNodesDistance().get("0,"+nextCustomer));
        for(int i = 1; i<=PopSize; i++){
            while (true) {
                // Use Nearest Neighbour method for the first chromosome
                if(i==1) {
                    nextCustomer = getNearestCustomer(r, customerInRoute);
                }
                else
                    //Use Random Neighbour method for other chromosomes
                    nextCustomer = getRandomCustomer(customerInRoute);
                if (nextCustomer > 0) {
                    //add customer to route if feasible
                    if (feasibility(r, nextCustomer)) {
                        r.addCustomers(nextCustomer,instance.getNodes().get(nextCustomer),instance.getNodesDistance().get(r.getCustomers().getLast()+","+nextCustomer));
                        customerInRoute.add(nextCustomer);
                    } else {
                        //Add route to chromosome if no customer can not be added
                        chromosome.add(new ArrayList<>(r.getCustomers()));
                        r = new Route(nextCustomer,instance.getNodes().get(nextCustomer), instance.getNodesDistance().get("0,"+nextCustomer));
                        customerInRoute.add(nextCustomer);
                    }
                }
                //Add last route to chromosome and add it to the population
                if(customerInRoute.size()==instance.getNodes().size()-1){
                    if(!chromosome.contains(r.getCustomers()))
                    {
                        chromosome.add(new ArrayList<>(r.getCustomers()));
                    }
                    population.add(new Chromosome(chromosome,0.0));
                    customerInRoute = new HashSet<>();
                    break;
                }
            }
            // create a new chromosome and route with a RN method
            chromosome = new ArrayList<>();
            nextCustomer = random.nextInt(1,instance.getNodes().size());
            customerInRoute.add(nextCustomer);
            r =  new Route(nextCustomer,instance.getNodes().get(nextCustomer),instance.getNodesDistance().get("0,"+nextCustomer));
        }
        return population;
    }
    //Gets the nearest customer that can be added to a route without making the route infeasible
    private static int getNearestCustomer(Route route, HashSet<Integer> customerInRoute){
        int currentSelectedCustomer=-1;
        ArrayList<Integer> customersNotInRoute = new ArrayList<>();
        //The customer must not already be in the route or any route
        for(int i=1; i<instance.getNodes().size(); i++)
        {
            if(!customerInRoute.contains(i)){
                customersNotInRoute.add(i);
            }
        }
        //returns -1 if all customers have an assigned route
        if(customersNotInRoute.isEmpty())
            return -1;
        int endingCustomer = route.getCustomers().getLast();
        currentSelectedCustomer= customersNotInRoute.getFirst();
        String key;
        key=endingCustomer+","+currentSelectedCustomer;
        ArrayList<String> routeSection= new ArrayList<>(instance.getNodesDistance().get(key));
        //sets the curret best distance + time
        double bestNearestDistanceTime = Double.parseDouble(routeSection.getFirst())+Double.parseDouble(routeSection.getLast());
        double tempDistanceTime;
        //Loop to find the best distance from the last customer of the route to the closest customer not assigned to a route yet.
        for(int i :customersNotInRoute){
            if(i!=endingCustomer&&!customerInRoute.contains(i)&& i!=customersNotInRoute.getFirst()){
                key = endingCustomer + "," + i;
                routeSection = new ArrayList<>(instance.getNodesDistance().get(key));
                tempDistanceTime = Double.parseDouble(routeSection.getFirst()) + Double.parseDouble(routeSection.getLast());
                if (bestNearestDistanceTime > tempDistanceTime) {
                    bestNearestDistanceTime = tempDistanceTime;
                    currentSelectedCustomer = i;
                }
            }
        }
        return currentSelectedCustomer;
    }
    private static int getRandomCustomer(HashSet<Integer> customerInRoute){
        int currentSelectedCustomer=random.nextInt(1,instance.getNodes().size());
        //Loops through customer if 80% have already been assigned route
        if(customerInRoute.size()>instance.getNodes().size()*0.8){
            for(int i=1; i<instance.getNodes().size(); i++)
            {
                if(!customerInRoute.contains(i)){
                    currentSelectedCustomer=i;
                    break;
                }
            }
        }
        else{
            // returns a random customer to be added to a route or make a new route
            while (customerInRoute.contains(currentSelectedCustomer)) {
                currentSelectedCustomer = random.nextInt(1, instance.getNodes().size());
            }
        }
        return currentSelectedCustomer;
    }
    //Checks the feasibility of adding a customer to a route
    public static boolean feasibility(Route route, int customer){
        ArrayList<String> customerDetails = instance.getNodes().get(customer);
        ArrayList<String> depot = instance.getNodes().get(0);
        String key=route.getCustomers().getLast()+","+customer;
        //get details of the route from the last customer to the customer to be added
        ArrayList<String> routeDetails = instance.getNodesDistance().get(key);
        key=customer+",0";
        //route details from the customer to be added to the depot
        ArrayList<String> lastRouteDetails = instance.getNodesDistance().get(key);
        double delivery = Double.parseDouble(customerDetails.get(0));
        double pickup = Double.parseDouble(customerDetails.get(1));
        double currentTime = route.getTotalTime()+Double.parseDouble(routeDetails.getLast());
        double timeToDepot;
        //checks for capacity constraints and time window constraints
        if(route.getCurrentCapacity()-delivery+pickup>instance.getVehicleCapacity())
            return false;
        else if(Double.parseDouble(customerDetails.get(3))< currentTime) {
            return false;
        } else if (route.getStartingCapacity()+delivery>instance.getVehicleCapacity()) {
            return false;
        }
        //checks if a return to the depot satisfies the route constraints
        if(currentTime<Double.parseDouble(customerDetails.get(2))){
            timeToDepot = Double.parseDouble(customerDetails.get(2))+Double.parseDouble(customerDetails.get(4))+Double.parseDouble(lastRouteDetails.getLast());
            return !(timeToDepot > Double.parseDouble(depot.get(3)));
        }else {
            timeToDepot = currentTime+Double.parseDouble(customerDetails.get(4))+Double.parseDouble(lastRouteDetails.getLast());
            return !(timeToDepot > Double.parseDouble(depot.get(3)));
        }
    }
}
