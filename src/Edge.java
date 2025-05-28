/**
 * 地铁边类，代表两个站点之间的连接
 */
public class Edge {
    private Station source; // 源站点
    private Station destination; // 目标站点
    private Line line; // 所属线路
    private double distance; // 两站之间的距离（公里）
    
    public Edge(Station source, Station destination, Line line, double distance) {
        this.source = source;
        this.destination = destination;
        this.line = line;
        this.distance = distance;
    }
    
    public Station getSource() {
        return source;
    }
    
    public Station getDestination() {
        return destination;
    }
    
    public Line getLine() {
        return line;
    }
    
    public double getDistance() {
        return distance;
    }
    
    @Override
    public String toString() {
        return source.getName() + " -> " + destination.getName() + 
               " (" + line.getName() + ", " + distance + "km)";
    }
} 