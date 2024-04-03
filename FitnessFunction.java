import java.util.ArrayList;
import java.util.HashMap;

public class FitnessFunction {
    //The fitness function is calculated by number of routes * dispatching cost +
    // total distance * cost per unit of travel distance
    public static void evaluate(ArrayList<Chromosome> pop, VRPInstance vrp){
        double numberOfRoute;
        //get the distances and time of travel between each nodes(customers or depot)
        HashMap<String, ArrayList<String>> routeDistance = vrp.getNodesDistance();
        for (Chromosome chromosome:pop){
            numberOfRoute = chromosome.getSolution().size();
            double totalDistance = 0.0;
            for (ArrayList<Integer> route: chromosome.getSolution()){
                //Add the distance of travel from the depot to the first customer
                totalDistance += Double.parseDouble(routeDistance.get("0,"+route.getFirst()).getFirst());
                for (int genes=0; genes<route.size(); genes++){
                    if(genes==route.size()-1) {
                        //Calculate the distance between the last customer and the depot
                        totalDistance += Double.parseDouble(routeDistance.get(route.get(genes) + ",0").getFirst());
                    }
                    else {
                        totalDistance += Double.parseDouble(routeDistance.get(route.get(genes) + "," + route.get(genes + 1)).getFirst());
                    }
                }
            }
            chromosome.setFitness(vrp.getUnitCost()*totalDistance+ vrp.getDispatchCost()*numberOfRoute);
        }
    }
}
// exchange unit cost and dispatch cost
//focus on distance only in (time + distance)
//Redo of Local
