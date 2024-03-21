import java.util.ArrayList;
import java.util.HashMap;

public class VRPInstance {
    private final int dimension;
    private final int vehicle;
    private final double dispatchCost;
    private final double unitCost;
    private final double vehicleCapacity;
    private final HashMap<Integer, ArrayList<String>> nodes;
    private final HashMap<String, ArrayList<String>> nodesDistance;
    public VRPInstance(int dimension, int vehicle, double dispatchCost, double unitCost, double vehicleCapacity, HashMap<Integer, ArrayList<String>> nodes, HashMap<String, ArrayList<String>> nodesDistance){
        this.dimension=dimension;
        this.vehicle=vehicle;
        this.dispatchCost=dispatchCost;
        this.unitCost=unitCost;
        this.vehicleCapacity=vehicleCapacity;
        this.nodes=nodes;
        this.nodesDistance = nodesDistance;
    }

    public int getDimension() {
        return dimension;
    }

    public int getVehicle() {
        return vehicle;
    }

    public double getDispatchCost() {
        return dispatchCost;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getVehicleCapacity() {
        return vehicleCapacity;
    }

    public HashMap<Integer, ArrayList<String>> getNodes() {
        return nodes;
    }

    public HashMap<String, ArrayList<String>> getNodesDistance() {
        return nodesDistance;
    }
}
