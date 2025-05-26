import java.util.*;

/**
 * 地铁线路类
 */
public class MetroLine {
    private String name;                    // 线路名称
    private List<String> stations;          // 站点序列
    private Map<String, Double> distances;  // 相邻站点间距离
    
    public MetroLine(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
        this.distances = new HashMap<>();
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getStations() {
        return new ArrayList<>(stations);
    }
    
    public void addStationDistance(String from, String to, double distance) {
        // 确保站点在列表中
        if (!stations.contains(from)) {
            stations.add(from);
        }
        if (!stations.contains(to)) {
            stations.add(to);
        }
        
        // 添加双向距离
        distances.put(from + "->" + to, distance);
        distances.put(to + "->" + from, distance);
    }
    
    public double getDistance(String from, String to) {
        return distances.getOrDefault(from + "->" + to, -1.0);
    }
    
    public Map<String, Double> getDistances() {
        return new HashMap<>(distances);
    }
    
    public int getStationCount() {
        return stations.size();
    }
    
    @Override
    public String toString() {
        return "MetroLine{" +
                "name='" + name + '\'' +
                ", stationCount=" + stations.size() +
                '}';
    }
} 