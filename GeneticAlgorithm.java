import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class GeneticAlgorithm {
    public void startGA(int PopSize, File file){
        VRPInstance vrp =GetData.readData(file);
        ArrayList<Chromosome> population = Population.initializePopulation(PopSize, vrp);
        FitnessFunction.evaluate(population,vrp);
        sortPop(population);
//        population.forEach(x->System.out.println("-- "+x.getFitness()));
        LocalSearch.greedyLocalSearch(population.getFirst(),vrp);
        for (ArrayList<Integer> route: population.getFirst().getSolution())
            if(!LocalSearch.routeFeasibility(route, vrp)) {
                System.out.println(route);
            }
//        Route r = new Route( 163,vrp.getNodes().get(163),vrp.getNodesDistance().get("0,163"));
//        System.out.println("-----");
//        System.out.println(r.getCustomers());
//        System.out.println(r.getTotalTime());
//        System.out.println(Population.feasibility(r,110));
    }
    private void sortPop(ArrayList<Chromosome> pop){
        pop.sort(Comparator.comparingDouble(Chromosome::getFitness));
    }
}
