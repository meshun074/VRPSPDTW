import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocalSearch {
    private static final Random r = new Random(System.currentTimeMillis());

    public static void Search(ArrayList<Chromosome> pop, double pSearch, VRPInstance vrp) {
        int minpop = (int) (pop.size() * pSearch);
        for (int i = 0; i < pop.size(); i++) {
            if (i % minpop == 0) {
                greedyLocalSearch(pop.get(i), vrp);
            }
        }
    }

    public static void greedyLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome relocateChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome swapChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
//        Chromosome two_optChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
//        System.out.println(chromosome.getSolution());
        System.out.println(chromosome.getFitness());
        relocateChromosome = relocateLocalSearch(relocateChromosome, vrp);
//        System.out.println(relocateChromosome.getSolution());
        System.out.println(relocateChromosome.getFitness());
        swapChromosome = swapLocalSearch(swapChromosome, vrp);
        System.out.println(swapChromosome.getFitness());
        System.out.println(chromosome.getFitness());
//        two_optLocalSearch(two_optChromosome);
    }

    private static Chromosome relocateLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        //System.out.println("uu "+chromosome.getFitness());
        boolean isUpgraded=true;
        int loop = 0;
        int relocateGene;
        while (isUpgraded||loop<3) {
            isUpgraded=false;
            relocateGene = r.nextInt(1, vrp.getDimension());
            for (int position = 1; position < vrp.getDimension(); position++) {
                tempChromosome = relocateGene(chromosome, relocateGene, position, vrp);
                isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
            }
            loop++;
        }
        return new Chromosome(chromosome.getSolution(),chromosome.getFitness());
    }

    private static Chromosome relocateGene(Chromosome ch, int relocateGene, int position, VRPInstance vrp) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        int count = 0;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            for (Integer integer : route) {
                count++;
                if (integer != relocateGene)
                    tempRoute.add(integer);
                else
                    check=true;
                if (count == position) {
                    tempRoute.add(relocateGene);
                    check =true;
                }
            }
            if (!tempRoute.isEmpty()) {
                if(check)
                {
                    if (routeFeasibility(tempRoute, vrp)) {
                        tempChromosome.add(tempRoute);
                    } else
                        return null;
                }else
                    tempChromosome.add(tempRoute);
            }
            check = false;
        }
        return new Chromosome(tempChromosome,0.0);
    }

    private static Chromosome swapLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        //System.out.println("uu "+chromosome.getFitness());
        boolean isUpgraded=true;
        int loop = 0;
        int gene1 ;
        while (isUpgraded||loop<3) {
            isUpgraded=false;
            gene1 = r.nextInt(1, vrp.getDimension());
            for (int position = 1; position < vrp.getDimension(); position++) {
                if(position!=gene1){
                    tempChromosome = swapGene(chromosome, gene1, position, vrp);
                    isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                }
            }
            loop++;
        }
        return new Chromosome(chromosome.getSolution(),chromosome.getFitness());
    }

    private static boolean isValidChromosome(Chromosome chromosome, VRPInstance vrp, Chromosome tempChromosome, boolean isUpgraded) {
        if (tempChromosome != null) {
//                    System.out.println("yes");
            FitnessFunction.evaluate(new ArrayList<>(List.of(tempChromosome)), vrp);
            //System.out.println(chromosome.getFitness()+"--"+chromosome.getSolution().getFirst()+">"+tempChromosome.getFitness());
            if (chromosome.getFitness() > tempChromosome.getFitness()) {
                chromosome.setSolution(tempChromosome.getSolution());
                chromosome.setFitness(tempChromosome.getFitness());
                isUpgraded = true;
//                        System.out.println("yes");
//                        System.out.println(""+chromosome.getSolution());
//                        System.out.println("--- "+chromosome.getFitness());
            }
        }
        return isUpgraded;
    }

    private static Chromosome swapGene(Chromosome ch, int gene1, int gene2, VRPInstance vrp) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            for (Integer integer : route) {
                if (integer != gene1 && integer!=gene2)
                    tempRoute.add(integer);
                else if(integer == gene1) {
                    tempRoute.add(gene2);
                    check = true;
                }
                else {
                    tempRoute.add(gene1);
                    check =true;
                }
            }
            if (!tempRoute.isEmpty()) {
                if(check)
                {
                    if (routeFeasibility(tempRoute, vrp)) {
                        tempChromosome.add(tempRoute);
                    } else
                        return null;
                }else
                    tempChromosome.add(tempRoute);
            }
            check = false;
        }
        return new Chromosome(tempChromosome,0.0);
    }

    private static void two_optLocalSearch(Chromosome twoOptChromosome) {
    }

    public static boolean routeFeasibility(ArrayList<Integer> route, VRPInstance vrp){
        double currentCapacity=0.0;
        double startingCapacity=0.0;
        String key = "0,"+route.getFirst();
        double totalTime=Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
        ArrayList<String> customerDetails;
        for (int i=0; i<route.size(); i++){
            customerDetails = vrp.getNodes().get(route.get(i));
            startingCapacity+=Double.parseDouble(customerDetails.getFirst());
            if(startingCapacity> vrp.getVehicleCapacity()) {
//                System.out.println("start "+startingCapacity+" --- "+vrp.getVehicleCapacity());
                return false;
            }
            if(totalTime>Double.parseDouble(customerDetails.get(3)))
                return false;
            if(i<route.size()-1){
                key= route.get(i)+","+route.get(i+1);
                if (totalTime > Double.parseDouble(customerDetails.get(2)))
                    totalTime += Double.parseDouble(customerDetails.getLast()) + Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
                else
                    totalTime = Double.parseDouble(customerDetails.get(2)) + Double.parseDouble(customerDetails.getLast()) + Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
            }
        }
        for (int customer: route){
            currentCapacity+=Double.parseDouble(vrp.getNodes().get(customer).get(1)) - Double.parseDouble(vrp.getNodes().get(customer).getFirst());
            if(currentCapacity> vrp.getVehicleCapacity()) {
//                System.out.println("start "+currentCapacity+" --- "+vrp.getVehicleCapacity());
                return false;
            }
        }
        return true;
    }

}
