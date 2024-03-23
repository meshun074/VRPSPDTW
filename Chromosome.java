import java.util.ArrayList;

public class Chromosome {
    private ArrayList<ArrayList<Integer>> solution;
    private double fitness;
    public Chromosome(ArrayList<ArrayList<Integer>> solution, double fitness){
        this.solution=new ArrayList<>(solution);
        this.fitness=fitness;
    }

    public ArrayList<ArrayList<Integer>> getSolution() {
        return solution;
    }

    public void setSolution(ArrayList<ArrayList<Integer>> solution) {
        this.solution = solution;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
