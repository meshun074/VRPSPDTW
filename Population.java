import java.util.ArrayList;
import java.util.Random;

public class Population {
    private static VRPInstance instance;
    private static Random random;
    public static ArrayList<Chromosome> initializePopulation(int PopSize, VRPInstance vrpInstance){
        instance = vrpInstance;
        ArrayList<Chromosome> population = new ArrayList<>();
        ArrayList<ArrayList<Integer>> chromosome=new ArrayList<>();
//        ArrayList<Integer> route = new ArrayList<>();
        ArrayList<Integer> customerInRoute = new ArrayList<>();
        random = new Random();
//        int nextCustomer = random.nextInt(1,instance.getNodes().size());
        int nextCustomer = getNearestCustomer(new Route(0,instance.getNodes().get(0),instance.getNodesDistance().get("1,0")),new ArrayList<>(){{add(0);}});
//        System.out.println(nextCustomer);
//        System.exit(1);
//        route.add(nextCustomer);
        customerInRoute.add(nextCustomer);
        Route r =  new Route(nextCustomer,instance.getNodes().get(nextCustomer),instance.getNodesDistance().get("0,"+nextCustomer));
        for(int i = 1; i<=PopSize; i++){
            while (true) {
                if(i==1) {
                    nextCustomer = getNearestCustomer(r, customerInRoute);
                    //System.out.println(customerInRoute);
                }
                else
                    nextCustomer = getRandomCustomer(customerInRoute);
                if (nextCustomer > 0) {
//                    if(nextCustomer==110&&i==1) {
//                        System.out.println(r.getCustomers());
//                        System.out.println(r.getTotalTime());
//                        System.out.println(feasibility(r, nextCustomer));
//                    }
                    if (feasibility(r, nextCustomer)) {

                        r.addCustomers(nextCustomer,instance.getNodes().get(nextCustomer),instance.getNodesDistance().get(r.getCustomers().getLast()+","+nextCustomer));
                        //System.out.println(r.getCustomers());
                        customerInRoute.add(nextCustomer);
//                        System.out.println(customerInRoute);
                    } else {
                        chromosome.add(new ArrayList<>(r.getCustomers()));
                        //System.out.println(chromosome);
                        //System.out.println(r.getCurrentCapacity()+" "+r.getTotalTime());
//                        route = new ArrayList<>();
//                        route.add(nextCustomer);
                        r = new Route(nextCustomer,instance.getNodes().get(nextCustomer), instance.getNodesDistance().get("0,"+nextCustomer));
                        customerInRoute.add(nextCustomer);
//                        if(nextCustomer==163&&i==1){
//                            System.out.println("++++++");
//                            System.out.println(r.getCustomers());
//                            System.out.println(r.getTotalTime());
//                            System.out.println(Population.feasibility(r,110));
//                        }
                    }
                }
                if(customerInRoute.size()==instance.getNodes().size()-1){
                    population.add(new Chromosome(chromosome,0.0));
                    //System.out.println(population);
                    customerInRoute = new ArrayList<>();
                    break;
                }
            }
            chromosome = new ArrayList<>();
            nextCustomer = random.nextInt(1,instance.getNodes().size());
//            route = new ArrayList<>();
//            route.add(nextCustomer);
            customerInRoute.add(nextCustomer);
            r =  new Route(nextCustomer,instance.getNodes().get(nextCustomer),instance.getNodesDistance().get("0,"+nextCustomer));
        }
        //System.out.println(population.getFirst());
        return population;
    }
    private static int getNearestCustomer(Route route, ArrayList<Integer> customerInRoute){
        int currentSelectedCustomer=-1;
        for(int i=1; i<instance.getNodes().size(); i++)
        {
            if(!customerInRoute.contains(i)){
                currentSelectedCustomer=i;
                break;
            }
        }
        if(currentSelectedCustomer==-1)
            return -1;
        int endingCustomer = route.getCustomers().getLast();
        String key;
        if(endingCustomer==1){
            key = endingCustomer+",2";
        }else
            key = endingCustomer+",1";
        ArrayList<String> routeSection= new ArrayList<>(instance.getNodesDistance().get(key));
        double bestNearestDistanceTime = Double.parseDouble(routeSection.getFirst())+Double.parseDouble(routeSection.getLast());
        double tempDistanceTime;
        for(int i = currentSelectedCustomer+1; i<instance.getNodes().size();i++){
            if(i!=endingCustomer&&!customerInRoute.contains(i)){
                key = endingCustomer + "," + i;
                routeSection = new ArrayList<>(instance.getNodesDistance().get(key));
                tempDistanceTime = Double.parseDouble(routeSection.getFirst()) + Double.parseDouble(routeSection.getLast());
                if (bestNearestDistanceTime > tempDistanceTime) {
                    bestNearestDistanceTime = tempDistanceTime;
                    currentSelectedCustomer = i;
                }
            }
        }
//        System.out.println(currentSelectedCustomer);
        return currentSelectedCustomer;
    }
    private static int getRandomCustomer(ArrayList<Integer> customerInRoute){
        int currentSelectedCustomer=random.nextInt(1,instance.getNodes().size());
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
            while (customerInRoute.contains(currentSelectedCustomer)) {
                currentSelectedCustomer = random.nextInt(1, instance.getNodes().size());
            }
        }
        return currentSelectedCustomer;
    }
    public static boolean feasibility(Route route, int customer){
        ArrayList<String> customerDetails = instance.getNodes().get(customer);
        String key=route.getCustomers().getLast()+","+customer;
        ArrayList<String> routeDetails = instance.getNodesDistance().get(key);
        double delivery = Double.parseDouble(customerDetails.get(0));
        double pickup = Double.parseDouble(customerDetails.get(1));
        double currentTime = route.getTotalTime()+Double.parseDouble(routeDetails.getLast());
        if(route.getCurrentCapacity()-delivery+pickup>instance.getVehicleCapacity())
            return false;
        else if(Double.parseDouble(customerDetails.get(3))< currentTime) {
            return false;
        } else if (route.getStartingCapacity()+delivery>instance.getVehicleCapacity()) {
            return false;
        }
        return true;
    }
}