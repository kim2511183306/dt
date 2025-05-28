import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 地铁站点类
 */
public class Station {
    private String name; // 站点名称
    private Map<String, Line> lines; // 该站点所属的线路，线路名称->线路对象
    private Map<Station, Edge> adjacentStations; // 相邻站点及连接边
    
    public Station(String name) {
        this.name = name;
        this.lines = new HashMap<>();
        this.adjacentStations = new HashMap<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void addLine(Line line) {
        lines.put(line.getName(), line);
    }
    
    public Map<String, Line> getLines() {
        return lines;
    }
    
    public boolean isTransferStation() {
        return lines.size() > 1;
    }
    
    public void addAdjacentStation(Station station, Edge edge) {
        adjacentStations.put(station, edge);
    }
    
    public Map<Station, Edge> getAdjacentStations() {
        return adjacentStations;
    }
    
    @Override
    public String toString() {
        return name;
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
} 