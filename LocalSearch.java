import java.util.ArrayList;
import java.util.List;

//Local search
public class LocalSearch {

    // Perform local search on pSearch proportion of the population
    public static void Search(ArrayList<Chromosome> pop, double pSearch, VRPInstance vrp) {
        for (int i = 0; i < pop.size(); i++) {
            if(Math.random()<pSearch)
                pop.set(i, greedyLocalSearch(pop.get(i), vrp));

        }
    }

    //Performs greedy local search
    private static Chromosome greedyLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome relocateChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome swapChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome two_optChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome maxChromosome = chromosome;
        //Returns the best solution for a chromosome using Relocate local search
        relocateChromosome = relocateLocalSearch(relocateChromosome, vrp);
        if (maxChromosome.getFitness() > relocateChromosome.getFitness())
            maxChromosome = relocateChromosome;
        //Returns the best solution for a chromosome using Swap local search
        swapChromosome = swapLocalSearch(swapChromosome, vrp);
        if (maxChromosome.getFitness() > swapChromosome.getFitness())
            maxChromosome = swapChromosome;
        //Returns the best solution for a chromosome using Two_opt local search
        two_optChromosome = two_optLocalSearch(two_optChromosome, vrp);
        if (maxChromosome.getFitness() > two_optChromosome.getFitness())
            maxChromosome = two_optChromosome;

        //Loops until solution cannot improve further
        if (maxChromosome.getFitness() == chromosome.getFitness())
            return new Chromosome(chromosome.getSolution(), chromosome.getFitness());
        else
            return greedyLocalSearch(maxChromosome, vrp);
    }

    //Relocate Local Search
    private static Chromosome relocateLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        ArrayList<Integer> relocateGene;
        ArrayList<Integer> reverseRelocateGene;
        for (int sizeofGene = 1; sizeofGene < 3; sizeofGene++) {
            boolean isUpgraded = true;
            while (isUpgraded) {
                isUpgraded = false;
                for (ArrayList<Integer> route : chromosome.getSolution()) {
                    for (int integer = 0; integer < route.size(); integer++) {
                        relocateGene = new ArrayList<>();
                        reverseRelocateGene = new ArrayList<>();
                        relocateGene.add(route.get(integer));
                        if (sizeofGene > 1 && route.size() > 1 && integer < route.size() - 1) {
                            relocateGene.add(route.get(integer + 1));
                            //reverse relocate genes
                            reverseRelocateGene.add(route.get(integer + 1));
                            reverseRelocateGene.add(route.get(integer));
                        }
                        if (sizeofGene > 1 && relocateGene.size() < 2)
                            break;
                        for (int position = 1; position < vrp.getDimension(); position++) {
                            tempChromosome = relocateGene(chromosome, relocateGene, position, vrp);
                            isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                        }
                        //check the reverse of the relocate gene;
                        if (sizeofGene > 1) {
                            for (int position = 1; position < vrp.getDimension(); position++) {
                                tempChromosome = relocateGene(chromosome, reverseRelocateGene, position, vrp);
                                isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                            }
                        }
                    }
                }
            }
        }
        return new Chromosome(chromosome.getSolution(), chromosome.getFitness());
    }

    // performs relocation of a customer
    private static Chromosome relocateGene(Chromosome ch, ArrayList<Integer> relocateGene, int position, VRPInstance vrp) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        int count = 0;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            for (Integer integer : route) {
                count++;
                if (!relocateGene.contains(integer))
                    tempRoute.add(integer);
                else
                    check = true;
                if (count == position) {
                    tempRoute.addAll(relocateGene);
                    check = true;
                }
            }
            if (!tempRoute.isEmpty()) {
                if (check) {
                    if (routeFeasibility(tempRoute, vrp)) {
                        tempChromosome.add(tempRoute);
                    } else
                        return null;
                } else
                    tempChromosome.add(tempRoute);
            }
            check = false;
        }
        return new Chromosome(tempChromosome, 0.0);
    }


    //swap local search
    private static Chromosome swapLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        for (int sizeofGene = 1; sizeofGene < 3; sizeofGene++) {
            boolean isUpgraded = true;
            ArrayList<Integer> gene1;
            ArrayList<Integer> reverseGene1;
            ArrayList<Integer> gene2;
            ArrayList<Integer> reverseGene2;
            int gene1Position;
            int gene2Position;
            while (isUpgraded) {
                isUpgraded = false;
                for (ArrayList<Integer> route : chromosome.getSolution()) {
                    for (int integer = 0; integer < route.size(); integer++) {
                        gene1 = new ArrayList<>();
                        reverseGene1 = new ArrayList<>();
                        gene1.add(route.get(integer));
                        if (sizeofGene > 1 && route.size() > 1 && integer < route.size() - 1) {
                            gene1.add(route.get(integer + 1));
                            //reverse swap genes
                            reverseGene1.add(route.get(integer + 1));
                            reverseGene1.add(route.get(integer));
                        }
                        if (sizeofGene > 1 && gene1.size() < 2)
                            break;
                        for (ArrayList<Integer> route2 : chromosome.getSolution()) {
                            for (int index = 0; index < route2.size(); index++) {
                                gene2 = new ArrayList<>();
                                reverseGene2 = new ArrayList<>();
                                gene2.add(route2.get(index));
                                //swap with 1 customer
                                isUpgraded = isValidSwap(chromosome, vrp, isUpgraded, gene1, gene2);
                                //swap with two customers
                                if (route2.size() > 1 && index < route2.size() - 1) {
                                    gene2.add(route2.get(index + 1));
                                    isUpgraded = isValidSwap(chromosome, vrp, isUpgraded, gene1, gene2);
                                    //swap with two reverse customers
                                    reverseGene2.add(route2.get(index + 1));
                                    reverseGene2.add(route2.get(index));
                                    if (!gene1.equals(reverseGene2) && !gene1.contains(reverseGene2.getFirst()) && !gene1.contains(reverseGene2.getLast())) {
                                        gene1Position = gene1.getFirst();
                                        gene2Position = gene2.getLast();
                                        tempChromosome = swapGene(chromosome, gene1, reverseGene2, gene1Position, gene2Position, vrp);
                                        isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                                    }
                                }
                            }
                        }
                        //check the reverse of the swap gene;
                        if (sizeofGene > 1) {

                            for (ArrayList<Integer> route2 : chromosome.getSolution()) {
                                for (int position = 0; position < route2.size(); position++) {
                                    gene2 = new ArrayList<>();
                                    reverseGene2 = new ArrayList<>();
                                    gene2.add(route2.get(position));
                                    //swap with 1 customer
                                    if (!reverseGene1.equals(gene2) && !reverseGene1.contains(gene2.getFirst()) && !gene1.contains(gene2.getLast())) {
                                        gene1Position = gene1.getLast();
                                        gene2Position = gene2.getFirst();
                                        tempChromosome = swapGene(chromosome, reverseGene1, gene2, gene1Position, gene2Position, vrp);
                                        isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                                    }
                                    //swap with two customers
                                    if (route2.size() > 1 && position < route2.size() - 1) {
                                        gene2.add(route2.get(position + 1));
                                        gene1Position = gene1.getLast();
                                        gene2Position = gene2.getFirst();
                                        if (!reverseGene1.equals(gene2) && !reverseGene1.contains(gene2.getFirst()) && !gene1.contains(gene2.getLast())) {
                                            tempChromosome = swapGene(chromosome, reverseGene1, gene2, gene1Position, gene2Position, vrp);
                                            isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                                        }
                                        //swap with two reverse customers with reverse customer
                                        reverseGene2.add(route2.get(position + 1));
                                        reverseGene2.add(route2.get(position));
                                        if (!reverseGene1.equals(reverseGene2) && !reverseGene1.contains(reverseGene2.getFirst()) && !reverseGene1.contains(reverseGene2.getLast())) {
                                            gene1Position = gene1.getLast();
                                            gene2Position = gene2.getLast();
                                            tempChromosome = swapGene(chromosome, reverseGene1, reverseGene2, gene1Position, gene2Position, vrp);
                                            isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new Chromosome(chromosome.getSolution(), chromosome.getFitness());
    }

    private static boolean isValidSwap(Chromosome chromosome, VRPInstance vrp, boolean isUpgraded, ArrayList<Integer> gene1, ArrayList<Integer> gene2) {
        int gene1Position;
        int gene2Position;
        Chromosome tempChromosome;
        if (!gene1.equals(gene2) && !gene1.contains(gene2.getFirst()) && !gene1.contains(gene2.getLast())) {
            gene1Position = gene1.getFirst();
            gene2Position = gene2.getFirst();
            tempChromosome = swapGene(chromosome, gene1, gene2, gene1Position, gene2Position, vrp);
            isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
        }
        return isUpgraded;
    }



    //Check if the new chromosome is an upgrade of the current chromosome
    //Or Chromosome has been improved
    private static boolean isValidChromosome(Chromosome chromosome, VRPInstance vrp, Chromosome tempChromosome, boolean isUpgraded) {
        if (tempChromosome != null) {
            FitnessFunction.evaluate(new ArrayList<>(List.of(tempChromosome)), vrp);
            if (chromosome.getFitness() > tempChromosome.getFitness()) {
                chromosome.setSolution(tempChromosome.getSolution());
                chromosome.setFitness(tempChromosome.getFitness());
                isUpgraded = true;
            }
        }
        return isUpgraded;
    }


    //Swaps customers

    private static Chromosome swapGene(Chromosome ch, ArrayList<Integer> gene1, ArrayList<Integer> gene2, int gene1Position, int gene2Position, VRPInstance vrp) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            for (Integer integer : route) {
                if (!gene1.contains(integer) && !gene2.contains(integer))
                    tempRoute.add(integer);
                else if (integer == gene1Position) {
                    tempRoute.addAll(gene2);
                    check = true;
                } else if (integer == gene2Position) {
                    tempRoute.addAll(gene1);
                    check = true;
                }
            }
            if (!tempRoute.isEmpty()) {
                if (check) {
                    if (routeFeasibility(tempRoute, vrp)) {
                        tempChromosome.add(tempRoute);
                    } else
                        return null;
                } else
                    tempChromosome.add(tempRoute);
            }
            check = false;
        }
        return new Chromosome(tempChromosome, 0.0);
    }

    //Two opt Local Search
    private static Chromosome two_optLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        boolean isUpgraded = true;
        while (isUpgraded) {
            isUpgraded = false;
            for (int route1 = 0; route1 < chromosome.getSolution().size(); route1++) {
                for (int route2 = 0; route2 < chromosome.getSolution().size(); route2++) {
                    if (route1 > route2) {
                        int maxOfTwo_opt = Math.max(chromosome.getSolution().get(route1).size(), chromosome.getSolution().get(route2).size());
                        for (int dividePosition = 1; dividePosition < maxOfTwo_opt; dividePosition++) {
                            tempChromosome = two_optGene(chromosome, route1, route2, vrp, dividePosition);
                            isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                        }
                    }
                }
            }
        }
        return new Chromosome(chromosome.getSolution(), chromosome.getFitness());
    }



    //Perform division and addition of route
    private static Chromosome two_optGene(Chromosome ch, int route1Position, int route2Position, VRPInstance vrp, int dividePosition) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        ArrayList<Integer> route1 = new ArrayList<>();
        ArrayList<Integer> route2 = new ArrayList<>();
        ArrayList<Integer> tempRoute2 = new ArrayList<>();
        divideRoute(ch, route1Position, route1, tempRoute2, dividePosition);
        divideRoute(ch, route2Position, route2, route1, dividePosition);
        route2.addAll(tempRoute2);
        int routeNumber = 0;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            if (routeNumber == route1Position) {
                tempRoute.addAll(route1);
                check = true;
            } else if (routeNumber == route2Position) {
                tempRoute.addAll(route2);
                check = true;
            } else {
                tempRoute.addAll(route);
            }
            if (!tempRoute.isEmpty()) {
                if (check) {
                    if (routeFeasibility(tempRoute, vrp)) {
                        tempChromosome.add(tempRoute);
                    } else
                        return null;
                } else
                    tempChromosome.add(tempRoute);
            }
            check = false;
            routeNumber++;
        }
        return new Chromosome(tempChromosome, 0.0);
    }

    //adds up divided route by Two-opt
    private static void divideRoute(Chromosome ch, int routePosition, ArrayList<Integer> route, ArrayList<Integer> tempRoute, int dividePosition) {
        ArrayList<Integer> integers = new ArrayList<>(ch.getSolution().get(routePosition));
        for (int g = 0; g < integers.size(); g++) {
            if (g <= dividePosition) {
                route.add(integers.get(g));
            } else {
                tempRoute.add(integers.get(g));
            }
        }
    }


    //Checks if route is feasible
    public static boolean routeFeasibility(ArrayList<Integer> route, VRPInstance vrp) {
        double currentCapacity = 0.0;
        double startingCapacity = 0.0;
        String key = "0," + route.getFirst();
        double totalTime = Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
        ArrayList<String> customerDetails;
        for (int i = 0; i < route.size(); i++) {
            customerDetails = vrp.getNodes().get(route.get(i));
            startingCapacity += Double.parseDouble(customerDetails.getFirst());
            if (startingCapacity > vrp.getVehicleCapacity()) {
                return false;
            }
            if (totalTime > Double.parseDouble(customerDetails.get(3)))
                return false;
            if (i < route.size() - 1) {
                key = route.get(i) + "," + route.get(i + 1);
                if (totalTime > Double.parseDouble(customerDetails.get(2)))
                    totalTime += Double.parseDouble(customerDetails.getLast()) + Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
                else
                    totalTime = Double.parseDouble(customerDetails.get(2)) + Double.parseDouble(customerDetails.getLast()) + Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
            }
        }
        for (int customer : route) {
            currentCapacity += Double.parseDouble(vrp.getNodes().get(customer).get(1)) - Double.parseDouble(vrp.getNodes().get(customer).getFirst());
            if (currentCapacity > vrp.getVehicleCapacity()) {
                return false;
            }
        }

        key = route.getLast() + ",0";
        customerDetails = vrp.getNodes().get(route.getLast());
        totalTime += Double.parseDouble(customerDetails.getLast()) + Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
        return !(totalTime > Double.parseDouble(vrp.getNodes().get(0).get(3)));
    }

}

