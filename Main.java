import java.io.File;

public class Main {
    // instantiate and start genetic algorithm
    public static void main(String [] args){
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.startGA(150,50,0.1,4,5,0.1,new File("200_1.vrpsdptw"));

    }
}
