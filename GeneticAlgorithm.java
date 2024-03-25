import java.io.File;
import java.util.*;

public class GeneticAlgorithm {
    private static final Random r = new Random(System.currentTimeMillis());

    public void startGA(int gen, int PopSize, double elitismRate, int tsRate, int fSearch, double pSearch,File file) {
        // create and store a class for the dataset
        VRPInstance vrp = GetData.readData(file);
        //Initialize population
        ArrayList<Chromosome> population = Population.initializePopulation(PopSize, vrp);
        ArrayList<Chromosome> newPopulation;
        ArrayList<Chromosome> tempPopulation;
        //Evaluate fitness of population
        FitnessFunction.evaluate(population, vrp);
        //Sort population
        sortPop(population);
        //Local Search
        LocalSearch.Search(population, pSearch, vrp);
        sortPop(population);
        System.out.println("Generation 0 Best Fitness "+population.getFirst().getFitness()+" Average Fitness "+averageFitness(population));
        for (int i = 1; i <= gen; i++) {
            //Maintains elitism
            newPopulation = new ArrayList<>(elitismPopulation(population, elitismRate));
            //Performs tournament selection
            tempPopulation = new ArrayList<>(tournamentSelection(population, tsRate));
            //Performs Best cost Route crossover with destruction
            BCRCD(tempPopulation, newPopulation, vrp);
            //Update population
            for (int ch=0; ch<population.size();ch++)
                population.get(ch).setSolution(newPopulation.get(ch).getSolution());
            FitnessFunction.evaluate(population, vrp);
            sortPop(population);
            if(i%fSearch==0) {
                LocalSearch.Search(population, pSearch, vrp);
                sortPop(population);
            }
            System.out.println("Generation "+i+" Best Fitness "+population.getFirst().getFitness()+" Average Fitness "+averageFitness(population));
            //prints solution in the last solution
            if (i==gen)
                System.out.println(population.getFirst().getSolution().size()+" === "+population.getFirst().getSolution());
        }
    }


    //Performs elitism
    private ArrayList<Chromosome> elitismPopulation(ArrayList<Chromosome> population, double elitismRate) {
        sortPop(population);
        ArrayList<ArrayList<Integer>> solution;
        ArrayList<Chromosome> elitismPop = new ArrayList<>();
        int count = 0;
        for (Chromosome ch : population) {
            count++;
            if (count > population.size() * elitismRate)
                break;
            else {
                solution = new ArrayList<>();
                for (ArrayList<Integer> r : ch.getSolution()) {
                    solution.add(new ArrayList<>(r));
                }
            }
            elitismPop.add(new Chromosome(solution, 0.0));
        }
        return elitismPop;
    }

    //Sort population
    private void sortPop(ArrayList<Chromosome> pop) {
        pop.sort(Comparator.comparingDouble(Chromosome::getFitness));
    }

    //Calculate average fitness
    private double averageFitness(ArrayList<Chromosome> pop) {
        double average = 0.0;
        for (Chromosome ch : pop)
            average += ch.getFitness();
        return average / pop.size();
    }

    //Performs tournament selection
    private ArrayList<Chromosome> tournamentSelection(ArrayList<Chromosome> population, int tsRate) {
        ArrayList<Chromosome> TSpop = new ArrayList<>();
        ArrayList<Chromosome> temp;
        for (int i = 0; i < population.size(); i++) {
            temp = new ArrayList<>();
            for (int k = 0; k < tsRate; k++) {
                temp.add(population.get(r.nextInt(population.size())));
            }
            sortPop(temp);
            TSpop.add(new Chromosome(temp.getFirst().getSolution(), temp.getFirst().getFitness()));
        }
        return TSpop;
    }

    //Crossover
    private void BCRCD(ArrayList<Chromosome> tempPopulation, ArrayList<Chromosome> newPopulation, VRPInstance vrp) {
        Chromosome c1;
        Chromosome c2;
        for (int i = 0; i < tempPopulation.size(); i++) {
            if (newPopulation.size() < tempPopulation.size()) {
                c1 = tempPopulation.get(i);
                if (i == tempPopulation.size() - 1)
                    c2 = tempPopulation.getFirst();
                else
                    c2 = tempPopulation.get(i + 1);
                int count = 0;
                while (c1.getFitness() == c2.getFitness() && c1.getSolution().size() == c2.getSolution().size() && equalChromosome(c1, c2) && count < 5) {
                    count++;
                    c2 = tempPopulation.get(r.nextInt(tempPopulation.size()));
                }
                crossover(c1, c2, vrp, newPopulation, tempPopulation.size());
            }
            else
                break;
        }

    }

    //cross two parents (customers)
    private void crossover(Chromosome c1, Chromosome c2, VRPInstance vrp, ArrayList<Chromosome> newPopulation, int limit) {
        int indexRandomRoute1 =0;
        int indexRandomRoute2 =0;
        try{
            indexRandomRoute1 = r.nextInt(c1.getSolution().size());
            indexRandomRoute2 = r.nextInt(c2.getSolution().size());
        }catch (IllegalArgumentException e){
            System.out.println(c1.getSolution());
            System.out.println(c2.getSolution());
            System.exit(1);
        }
        ArrayList<Integer> randomRoute1 = c1.getSolution().get(indexRandomRoute1);
        ArrayList<Integer> randomRoute2 = c2.getSolution().get(indexRandomRoute2);
        ArrayList<ArrayList<Integer>> tempSolution = new ArrayList<>();
        ArrayList<Integer> removedGenes = new ArrayList<>(randomRoute1);
        removeRouteAndGenes(c1, vrp, randomRoute1, randomRoute2, removedGenes, tempSolution);
        tempSolution = insertionOfGenes(tempSolution, removedGenes, vrp);
        newPopulation.add(new Chromosome(tempSolution, 0.0));
        if (newPopulation.size() < limit) {
            tempSolution = new ArrayList<>();
            removedGenes = new ArrayList<>(randomRoute2);
            removeRouteAndGenes(c2, vrp, randomRoute2, randomRoute1, removedGenes, tempSolution);
            tempSolution = insertionOfGenes(tempSolution, removedGenes, vrp);
            newPopulation.add(new Chromosome(tempSolution, 0.0));
        }
    }

    // remove customers for BCRCD
    private static void removeRouteAndGenes(Chromosome c1, VRPInstance vrp, ArrayList<Integer> randomRoute1, ArrayList<Integer> randomRoute2, ArrayList<Integer> removedGenes, ArrayList<ArrayList<Integer>> tempSolution) {
        ArrayList<Integer> tempRoute;
        ArrayList<Integer> testRoute;
        for (ArrayList<Integer> route : c1.getSolution()) {
            if (!route.equals(randomRoute1)) {
                tempRoute = new ArrayList<>(route);
                testRoute = new ArrayList<>(tempRoute);
                for (Integer gene : route) {
                    if (randomRoute2.contains(gene)) {
                        testRoute.remove(gene);
                        if (!testRoute.isEmpty()) {
                            if (LocalSearch.routeFeasibility(testRoute, vrp)) {
                                tempRoute = new ArrayList<>(testRoute);
                                removedGenes.add(gene);
                            } else
                                testRoute = new ArrayList<>(tempRoute);
                        }else
                            removedGenes.add(gene);
                    }
                }
                if (!testRoute.isEmpty())
                    tempSolution.add(tempRoute);
            }
        }
    }

    //Insert customer for BCRCD
    private ArrayList<ArrayList<Integer>> insertionOfGenes(ArrayList<ArrayList<Integer>> solution, ArrayList<Integer> removedGenes, VRPInstance vrp) {
        ArrayList<ArrayList<Integer>> tempSolution;
        ArrayList<Integer> tempRoute;
        ArrayList<Chromosome> currentChromosomeBestSolution;
        for (int removeGene : removedGenes) {
            currentChromosomeBestSolution = new ArrayList<>();
            for (ArrayList<Integer> route : solution) {
                for (int i = 0; i <= route.size(); i++) {
                    tempRoute = new ArrayList<>(route);
                    tempRoute.add(i, removeGene);
                    if (LocalSearch.routeFeasibility(tempRoute, vrp)) {
                        tempSolution = getCandidateSolution(solution, route, tempRoute);
                        currentChromosomeBestSolution.add(new Chromosome(tempSolution, 0.0));
                    }
                }
            }
            if (!currentChromosomeBestSolution.isEmpty()) {
                FitnessFunction.evaluate(currentChromosomeBestSolution, vrp);
                sortPop(currentChromosomeBestSolution);
                solution = currentChromosomeBestSolution.getFirst().getSolution();
            } else {
                tempRoute = new ArrayList<>();
                tempRoute.add(removeGene);
                solution.add(tempRoute);
            }
        }
        return solution;
    }

    //Replaces route
    //used by BCRCD to perform greedy insertion of customer in finding the best route.
    private ArrayList<ArrayList<Integer>> getCandidateSolution(ArrayList<ArrayList<Integer>> solution, ArrayList<Integer> routeToRemove, ArrayList<Integer> routeToReplace) {
        ArrayList<ArrayList<Integer>> tempSolution = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        for (ArrayList<Integer> route : solution) {
            if (route.equals(routeToRemove))
                tempRoute = new ArrayList<>(routeToReplace);
            else
                tempRoute = new ArrayList<>(route);
            tempSolution.add(tempRoute);
        }
        return tempSolution;
    }

    //Checks if chromosome are the same
    private boolean equalChromosome(Chromosome c1, Chromosome c2) {
        for (int i = 0; i < Math.min(c1.getSolution().size(), c2.getSolution().size()); i++) {
            if (!c1.getSolution().contains(c2.getSolution().get(i))) {
                return false;
            }
        }
        return true;
    }
}
