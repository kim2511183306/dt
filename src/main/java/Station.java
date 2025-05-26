import java.util.*;

/**
 * 地铁站点类
 */
public class Station {
    private String name;           // 站点名称
    private Set<String> lines;     // 经过该站的线路集合
    private Map<String, Map<String, Double>> neighbors; // 邻接站点信息 <线路名, <邻接站点名, 距离>>
    
    public Station(String name) {
        this.name = name;
        this.lines = new HashSet<>();
        this.neighbors = new HashMap<>();
    }
    
    public String getName() {
        return name;
    }
    
    public Set<String> getLines() {
        return new HashSet<>(lines);
    }
    
    public void addLine(String line) {
        this.lines.add(line);
        if (!neighbors.containsKey(line)) {
            neighbors.put(line, new HashMap<>());
        }
    }
    
    public void addNeighbor(String line, String neighborName, double distance) {
        if (!neighbors.containsKey(line)) {
            neighbors.put(line, new HashMap<>());
        }
        neighbors.get(line).put(neighborName, distance);
    }
    
    public Map<String, Map<String, Double>> getNeighbors() {
        return neighbors;
    }
    
    public boolean isTransferStation() {
        return lines.size() >= 2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Station station = (Station) obj;
        return Objects.equals(name, station.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", lines=" + lines +
                '}';
    }
}