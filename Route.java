import java.util.ArrayList;

public class Route {
    private double currentCapacity;
    private double startingCapacity;
    private double totalTime;
    private final ArrayList<Integer> customers = new ArrayList<>();

    public Route(int customer , ArrayList<String> customerDetail, ArrayList<String> routeDetails) {
        startingCapacity =Double.parseDouble(customerDetail.getFirst());
        currentCapacity = Double.parseDouble(customerDetail.get(1));
        double start = Double.parseDouble(customerDetail.get(2));
        double timeTo = Double.parseDouble(routeDetails.get(1));
        double servingTime = Double.parseDouble(customerDetail.get(4));
        if(timeTo<start)
            totalTime = start+servingTime;
        else
            totalTime = timeTo+servingTime;
        customers.add(customer);
//        if(customer==163){
//            System.out.println("*****");
//            System.out.println(customerDetail);
//            System.out.println(routeDetails);
//            System.out.println(getTotalTime());
//        }
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity += currentCapacity;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getStartingCapacity() {
        return startingCapacity;
    }

    public void setStartingCapacity(double startingCapacity) {
        this.startingCapacity += startingCapacity;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public ArrayList<Integer> getCustomers() {
        return customers;
    }

    public void addCustomers(int customer,ArrayList<String> customerDetail, ArrayList<String> routeDetails) {
        setStartingCapacity (Double.parseDouble(customerDetail.getFirst()));
        setCurrentCapacity(Double.parseDouble(customerDetail.get(1))-Double.parseDouble(customerDetail.getFirst()));
        double start = Double.parseDouble(customerDetail.get(2));
        double timeTo = Double.parseDouble(routeDetails.get(1))+totalTime;
        double servingTime = Double.parseDouble(customerDetail.get(4));
        if(timeTo<start)
            setTotalTime(start+servingTime);
        else
            setTotalTime(timeTo+servingTime);
        customers.add(customer);
    }
}
