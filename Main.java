import java.io.File;

public class Main {
    public static void main(String [] args){
        //GetData.readData(new File("200_1.vrpsdptw"));
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.startGA(50,new File("200_1.vrpsdptw"));
    }
}
