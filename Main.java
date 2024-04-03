import java.io.File;
import java.util.ArrayList;

public class Main {
    // instantiate and start genetic algorithm
    public static void main(String [] args){
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.startGA(1000,50,0.1,4,10,0.1,new File("200_1.vrpsdptw"));

    }

}
