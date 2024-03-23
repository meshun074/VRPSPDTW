import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocalSearch {
    private static final Random r = new Random(System.currentTimeMillis());

    public static void Search(ArrayList<Chromosome> pop, double pSearch, VRPInstance vrp) {
        int minpop = (int) (pop.size() * pSearch);
        for (int i = 0; i < pop.size(); i++) {
            if (i % minpop == 0) {
                pop.set(i,greedyLocalSearch(pop.get(i), vrp));
            }
        }
    }

    private static Chromosome greedyLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome relocateChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome swapChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome two_optChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome invertedChromosome = new Chromosome(new ArrayList<>(chromosome.getSolution()), chromosome.getFitness());
        Chromosome maxChromosome =  chromosome;
//        System.out.println(chromosome.getSolution());
//        System.out.println(chromosome.getFitness());
        relocateChromosome = relocateLocalSearch(relocateChromosome, vrp);
        if(maxChromosome.getFitness()>relocateChromosome.getFitness())
            maxChromosome = relocateChromosome;
//        System.out.println(relocateChromosome.getSolution());
//        System.out.println(relocateChromosome.getFitness());
        swapChromosome = swapLocalSearch(swapChromosome, vrp);
        if(maxChromosome.getFitness()>swapChromosome.getFitness())
            maxChromosome=swapChromosome;
//        System.out.println(swapChromosome.getFitness());
        two_optChromosome = two_optLocalSearch(two_optChromosome, vrp);
        if(maxChromosome.getFitness()>two_optChromosome.getFitness())
            maxChromosome=two_optChromosome;
//        System.out.println(two_optChromosome.getFitness());
        invertedChromosome = invertedLocalSearch(invertedChromosome, vrp);
        if(maxChromosome.getFitness()>invertedChromosome.getFitness())
            maxChromosome=invertedChromosome;
//        System.out.println(invertedChromosome.getFitness());
//        System.out.println(chromosome.getFitness());
        if(maxChromosome.getFitness()==chromosome.getFitness())
            return new Chromosome(chromosome.getSolution(), chromosome.getFitness());
        else
            return greedyLocalSearch(maxChromosome,vrp);
    }


    private static Chromosome relocateLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        //System.out.println("uu "+chromosome.getFitness());
        boolean isUpgraded=true;
        int loop = 0;
        int relocateGene;
        while (isUpgraded||loop<5) {
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
        while (isUpgraded||loop<5) {
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
//            System.out.println(chromosome.getFitness()+"--"+chromosome.getSolution().getFirst()+">"+tempChromosome.getFitness());
            if (chromosome.getFitness() > tempChromosome.getFitness()) {
                chromosome.setSolution(tempChromosome.getSolution());
                chromosome.setFitness(tempChromosome.getFitness());
                isUpgraded = true;
//                System.out.println("yes");
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

    private static Chromosome two_optLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        //System.out.println("uu "+chromosome.getFitness());
        for(int i=0; i<5; i++) {
            boolean isUpgraded = true;
            int dividePosition = 0;
            int route1 = r.nextInt(chromosome.getSolution().size());
            int route2 = r.nextInt(chromosome.getSolution().size());
            int maxOfTwo_opt = Math.max(chromosome.getSolution().get(route1).size(), chromosome.getSolution().get(route2).size());
            while (isUpgraded || dividePosition < maxOfTwo_opt) {
                isUpgraded = false;
                while (route1 == route2) {
                    route1 = r.nextInt(chromosome.getSolution().size());
                    route2 = r.nextInt(chromosome.getSolution().size());
                }
                tempChromosome = two_optGene(chromosome, route1, route2, vrp, dividePosition);
                isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
                dividePosition++;
            }
        }
        return new Chromosome(chromosome.getSolution(),chromosome.getFitness());
    }

    private static Chromosome two_optGene(Chromosome ch, int route1Position, int route2Position, VRPInstance vrp, int dividePosition) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        ArrayList<Integer> route1 = new ArrayList<>();
        ArrayList<Integer> route2 = new ArrayList<>();
        ArrayList<Integer> tempRoute2 = new ArrayList<>();
        divideRoute(ch, route1Position, route1, tempRoute2,dividePosition);
        divideRoute(ch, route2Position, route2, route1,dividePosition);
        route2.addAll(tempRoute2);
        int routeNumber=0;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            if(routeNumber==route1Position){
                tempRoute.addAll(route1);
                check = true;
            }else if (routeNumber==route2Position){
                tempRoute.addAll(route2);
                check=true;
            }
            else {
                tempRoute.addAll(route);
            }
            if (!tempRoute.isEmpty()) {
                if(check)
                {
                    if (routeFeasibility(tempRoute, vrp)) {
//                        System.out.println("yes");
                        tempChromosome.add(tempRoute);
                    } else
                        return null;
                }else
                    tempChromosome.add(tempRoute);
            }
            check = false;
            routeNumber++;
        }
        return new Chromosome(tempChromosome,0.0);
    }

    private static void divideRoute(Chromosome ch, int routePosition, ArrayList<Integer> route, ArrayList<Integer> tempRoute,int dividePosition) {
        ArrayList<Integer> integers = new ArrayList<>(ch.getSolution().get(routePosition));
        for(int g = 0; g< integers.size(); g++){
            if (g<=dividePosition){
                route.add(integers.get(g));
            }else {
                tempRoute.add(integers.get(g));
            }
        }
    }
    private static Chromosome invertedLocalSearch(Chromosome chromosome, VRPInstance vrp) {
        Chromosome tempChromosome;
        //System.out.println("uu "+chromosome.getFitness());
        boolean isUpgraded=true;
        int loop = 0;
        int invertedRoute;
        while (isUpgraded||loop<5) {
            isUpgraded=false;
            invertedRoute = r.nextInt( chromosome.getSolution().size());
            while (chromosome.getSolution().get(invertedRoute).size()<2)
                invertedRoute = r.nextInt( chromosome.getSolution().size());
            for (int position = 1; position < vrp.getDimension(); position++) {
                tempChromosome = invertGene(chromosome, invertedRoute, vrp);
                isUpgraded = isValidChromosome(chromosome, vrp, tempChromosome, isUpgraded);
            }
            loop++;
        }
        return new Chromosome(chromosome.getSolution(),chromosome.getFitness());
    }

    private static Chromosome invertGene(Chromosome ch, int invertedRoute, VRPInstance vrp) {
        ArrayList<ArrayList<Integer>> tempChromosome = new ArrayList<>();
        ArrayList<Integer> tempRoute;
        int bound = ch.getSolution().get(invertedRoute).size()-2;
        int startInvertedRoute = bound>0?r.nextInt(ch.getSolution().get(invertedRoute).size()-2):0;
        int count = 0;
        boolean check = false;
        for (ArrayList<Integer> route : ch.getSolution()) {
            tempRoute = new ArrayList<>();
            if(count==invertedRoute) {
                for (int i=0; i< route.size(); i++){
                    if (i==startInvertedRoute)
                    {
                        tempRoute.add(route.get(i+1));
                    } else if (i==startInvertedRoute+1) {
                        tempRoute.add(route.get(i-1));
                    }
                    else
                        tempRoute.add(route.get(i));
                }
                check =true;
            }
            else
                tempRoute.addAll(route);
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
            count++;
        }
        return new Chromosome(tempChromosome,0.0);
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

        key= route.getLast()+",0";
        customerDetails = vrp.getNodes().get(route.getLast());
        totalTime += Double.parseDouble(customerDetails.getLast()) + Double.parseDouble(vrp.getNodesDistance().get(key).getLast());
        return !(totalTime > Double.parseDouble(vrp.getNodes().get(0).get(3)));
    }

}
