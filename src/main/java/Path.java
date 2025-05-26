import java.util.*;

/**
 * 路径类，表示从起点到终点的一条完整路径
 */
public class Path {
    private List<String> stations;          // 经过的站点序列
    private List<String> lines;             // 对应的线路序列
    private double totalDistance;           // 总距离
    private int transferCount;              // 换乘次数
    
    public Path() {
        this.stations = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.totalDistance = 0.0;
        this.transferCount = 0;
    }
    
    public Path(List<String> stations, List<String> lines, double totalDistance) {
        this.stations = new ArrayList<>(stations);
        this.lines = new ArrayList<>(lines);
        this.totalDistance = totalDistance;
        this.transferCount = calculateTransferCount();
    }
    
    public void addSegment(String station, String line, double distance) {
        stations.add(station);
        lines.add(line);
        totalDistance += distance;
    }
    
    private int calculateTransferCount() {
        if (lines.size() <= 1) return 0;
        
        int count = 0;
        String currentLine = lines.get(0);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).equals(currentLine)) {
                count++;
                currentLine = lines.get(i);
            }
        }
        return count;
    }
    
    public List<String> getStations() {
        return new ArrayList<>(stations);
    }
    
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public int getTransferCount() {
        return transferCount;
    }
    
    public void setTransferCount(int transferCount) {
        this.transferCount = transferCount;
    }
    
    public boolean isEmpty() {
        return stations.isEmpty();
    }
    
    public String getStartStation() {
        return stations.isEmpty() ? null : stations.get(0);
    }
    
    public String getEndStation() {
        return stations.isEmpty() ? null : stations.get(stations.size() - 1);
    }
    
    @Override
    public String toString() {
        return "Path{" +
                "stations=" + stations +
                ", lines=" + lines +
                ", totalDistance=" + String.format("%.3f", totalDistance) +
                ", transferCount=" + transferCount +
                '}';
    }
} 