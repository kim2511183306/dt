import java.util.*;

/**
 * 地铁线路类
 */
public class Line {
    private String name; // 线路名称
    private List<Station> stations; // 该线路包含的站点
    private Map<String, Integer> stationIndices; // 站点名称到索引的映射
    
    public Line(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
        this.stationIndices = new HashMap<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void addStation(Station station) {
        stationIndices.put(station.getName(), stations.size());
        stations.add(station);
        station.addLine(this);
    }
    
    public List<Station> getStations() {
        return stations;
    }
    
    public int getStationCount() {
        return stations.size();
    }
    
    public boolean hasStation(String stationName) {
        return stationIndices.containsKey(stationName);
    }
    
    public Station getStation(String stationName) {
        if (!hasStation(stationName)) {
            return null;
        }
        int index = stationIndices.get(stationName);
        return stations.get(index);
    }
    
    public int getStationIndex(String stationName) {
        return stationIndices.getOrDefault(stationName, -1);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Line line = (Line) obj;
        return Objects.equals(name, line.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
} 