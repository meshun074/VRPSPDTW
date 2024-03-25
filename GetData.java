import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

//data about VRP problem from a file
public class GetData {
    public static VRPInstance readData(File file){
        int dimension = 0;
        int vehicle = 0;
        double dispatchCost=0.0;
        double unitCost=0.0;
        double vehicleCapacity=0.0;
        int section = 0;
        String txt;
        HashMap<Integer, ArrayList<String>> nodes = new HashMap<>();
        HashMap<String, ArrayList<String>> nodesDistance = new HashMap<>();
        try {
            Scanner scanner = new Scanner(file);
            String[] strings;
            while (scanner.hasNext()){
                txt=scanner.nextLine();
                if(section < 1) {
                    //number of nodes
                    if (txt.contains("DIMENSION")) {
                        dimension = Integer.parseInt(getInteger(txt));
                        //number of vehicles
                    } else if (txt.contains("VEHICLES"))
                        vehicle = Integer.parseInt(getInteger(txt));
                    else if (txt.contains("DISPATCHINGCOST"))
                        dispatchCost = Double.parseDouble(getInteger(txt));
                    else if (txt.contains("UNITCOST"))
                        unitCost = Double.parseDouble(getInteger(txt));
                    //Vehicle capacity
                    else if (txt.contains("CAPACITY"))
                        vehicleCapacity = Double.parseDouble(getInteger(txt));
                    //customers and their requirement (time window, delivery and pickup)
                    else if (txt.contains("NODE_SECTION")) {
                        section++;
                    }
                }else if(section < 2){
                    //Distance and time between nodes info
                    if(txt.contains("DISTANCETIME_SECTION"))
                        section++;
                    else {
                        strings = txt.substring(txt.indexOf(",") + 1).split(",");
                        nodes.put(Integer.parseInt(txt.substring(0, txt.indexOf(","))), new ArrayList<>(List.of(strings)));
                    }
                } else if (section <3 ) {
                    if(txt.contains("DEPOT_SECTION"))
                        break;
                    else {
                        strings = txt.substring(txt.indexOf(",",txt.indexOf(",")+1) + 1).split(",");
                        nodesDistance.put(txt.substring(0,txt.indexOf(",",txt.indexOf(",")+1)),new ArrayList<>(List.of(strings)));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new VRPInstance(dimension,vehicle,dispatchCost,unitCost,vehicleCapacity,nodes,nodesDistance);
    }
    private static String getInteger(String text){
        return text.substring(text.indexOf(":") + 2);
    }
}
