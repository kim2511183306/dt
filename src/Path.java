import java.util.ArrayList;
import java.util.List;

/**
 * 地铁路径类，表示从起点到终点的完整路径
 */
public class Path implements Comparable<Path> {
    private List<Station> stations; // 路径经过的站点
    private List<Line> lines; // 路径经过的线路
    private double totalDistance; // 总距离
    private int transferCount; // 换乘次数
    
    public Path() {
        stations = new ArrayList<>();
        lines = new ArrayList<>();
        totalDistance = 0;
        transferCount = 0;
    }
    
    public Path(Path other) {
        this.stations = new ArrayList<>(other.stations);
        this.lines = new ArrayList<>(other.lines);
        this.totalDistance = other.totalDistance;
        this.transferCount = other.transferCount;
    }
    
    public void addStation(Station station, Line line, double distance) {
        if (!stations.isEmpty() && !lines.isEmpty()) {
            Line lastLine = lines.get(lines.size() - 1);
            if (!lastLine.equals(line)) {
                transferCount++;
            }
        }
        
        stations.add(station);
        lines.add(line);
        totalDistance += distance;
    }
    
    public void addFirstStation(Station station) {
        stations.add(station);
    }
    
    public List<Station> getStations() {
        return stations;
    }
    
    public List<Line> getLines() {
        return lines;
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public int getTransferCount() {
        return transferCount;
    }
    
    public int getStationCount() {
        return stations.size();
    }
    
    public boolean containsStation(Station station) {
        return stations.contains(station);
    }
    
    @Override
    public int compareTo(Path other) {
        // 首先比较总距离
        int distanceComparison = Double.compare(this.totalDistance, other.totalDistance);
        if (distanceComparison != 0) {
            return distanceComparison;
        }
        
        // 如果距离相同，比较换乘次数
        return Integer.compare(this.transferCount, other.transferCount);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("路径: ");
        for (int i = 0; i < stations.size(); i++) {
            sb.append(stations.get(i).getName());
            if (i < stations.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append("\n距离: ").append(String.format("%.2f", totalDistance)).append("公里");
        sb.append("\n换乘次数: ").append(transferCount);
        return sb.toString();
    }
} 