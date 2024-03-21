import java.util.ArrayList;
import java.util.HashMap;

public class FitnessFunction {
    public static void evaluate(ArrayList<Chromosome> pop, VRPInstance vrp){
        double numberOfRoute;
        HashMap<String, ArrayList<String>> routeDistance = vrp.getNodesDistance();
        for (Chromosome chromosome:pop){
            numberOfRoute = chromosome.getSolution().size();
            double totalDistance = 0.0;
            for (ArrayList<Integer> route: chromosome.getSolution()){
                totalDistance += Double.parseDouble(routeDistance.get("0,"+route.getFirst()).getFirst());
                for (int genes=0; genes<route.size(); genes++){
                    if(genes==route.size()-1) {
                        totalDistance += Double.parseDouble(routeDistance.get(route.get(genes) + ",0").getFirst());
                    }
                    else {
                        totalDistance += Double.parseDouble(routeDistance.get(route.get(genes) + "," + route.get(genes + 1)).getFirst());
                    }
                }
            }
            chromosome.setFitness(vrp.getUnitCost()*numberOfRoute+ vrp.getDispatchCost()*totalDistance);
        }
    }
}
