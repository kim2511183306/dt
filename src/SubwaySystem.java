import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 地铁系统类，核心类，包含所有地铁线路和站点信息，实现各种查询功能
 */
public class SubwaySystem {
    private Map<String, Station> stations; // 所有站点，站点名称->站点
    private Map<String, Line> lines; // 所有线路，线路名称->线路
    private PricingSystem pricingSystem; // 计价系统
    
    public SubwaySystem() {
        stations = new HashMap<>();
        lines = new HashMap<>();
        pricingSystem = new PricingSystem();
    }
    
    /**
     * 从文件加载地铁数据
     * @param filePath 数据文件路径
     */
    public void loadFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        String currentLineName = null;
        Line currentLine = null;
        Station previousStation = null;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // 检查是否是新的线路标题
            if (line.contains("线站点间距")) {
                currentLineName = extractLineName(line);
                currentLine = getOrCreateLine(currentLineName);
                previousStation = null;
                continue;
            }
            
            // 检查是否是站点名称和距离
            if (line.contains("---")) {
                String[] parts = line.split("---");
                if (parts.length >= 2) {
                    String sourceStationName = parts[0].trim();
                    String destStationName = parts[1].trim();
                    
                    // 提取距离
                    double distance = 0;
                    String[] distanceParts = parts[1].split("\\s+");
                    if (distanceParts.length >= 2) {
                        try {
                            distance = Double.parseDouble(distanceParts[distanceParts.length - 1]);
                        } catch (NumberFormatException e) {
                            System.err.println("无法解析距离: " + line);
                            continue;
                        }
                    }
                    
                    Station sourceStation = getOrCreateStation(sourceStationName);
                    Station destStation = getOrCreateStation(destStationName.replaceAll("\\s+\\d+\\.\\d+$", ""));
                    
                    // 将站点添加到线路
                    if (!currentLine.hasStation(sourceStation.getName())) {
                        currentLine.addStation(sourceStation);
                    }
                    if (!currentLine.hasStation(destStation.getName())) {
                        currentLine.addStation(destStation);
                    }
                    
                    // 创建双向连接
                    Edge forwardEdge = new Edge(sourceStation, destStation, currentLine, distance);
                    Edge backwardEdge = new Edge(destStation, sourceStation, currentLine, distance);
                    sourceStation.addAdjacentStation(destStation, forwardEdge);
                    destStation.addAdjacentStation(sourceStation, backwardEdge);
                }
            }
        }
        reader.close();
    }
    
    /**
     * 从标题中提取线路名称
     */
    private String extractLineName(String line) {
        if (line.contains("号线")) {
            return line.substring(0, line.indexOf("号线") + 2);
        }
        if (line.contains("阳逻线")) {
            return "阳逻线";
        }
        // 处理其他可能的线路名称格式
        return line.split("站")[0].trim();
    }
    
    /**
     * 获取或创建线路
     */
    private Line getOrCreateLine(String lineName) {
        Line line = lines.get(lineName);
        if (line == null) {
            line = new Line(lineName);
            lines.put(lineName, line);
        }
        return line;
    }
    
    /**
     * 获取或创建站点
     */
    private Station getOrCreateStation(String stationName) {
        Station station = stations.get(stationName);
        if (station == null) {
            station = new Station(stationName);
            stations.put(stationName, station);
        }
        return station;
    }
    
    /**
     * 获取所有中转站（至少有两条线路经过的站点）
     * @return 中转站集合，每个元素包含站点名称和通过的线路
     */
    public List<Map.Entry<String, Set<String>>> getTransferStations() {
        List<Map.Entry<String, Set<String>>> result = new ArrayList<>();
        
        for (Station station : stations.values()) {
            if (station.isTransferStation()) {
                Set<String> lineNames = station.getLines().keySet();
                result.add(new AbstractMap.SimpleEntry<>(station.getName(), lineNames));
            }
        }
        
        return result;
    }
    
    /**
     * 查找距离给定站点n站内的所有站点
     * @param stationName 起始站点名称
     * @param n 距离（站数）
     * @return 满足条件的站点集合，包含站点名称、所在线路和距离
     */
    public List<Map.Entry<String, Map.Entry<String, Integer>>> getStationsWithinDistance(String stationName, int n) {
        Station startStation = stations.get(stationName);
        if (startStation == null) {
            throw new IllegalArgumentException("站点不存在: " + stationName);
        }
        
        List<Map.Entry<String, Map.Entry<String, Integer>>> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<Object[]> queue = new LinkedList<>();
        
        // 将起始站点加入队列：[站点, 距离, 线路]
        for (Line line : startStation.getLines().values()) {
            queue.offer(new Object[]{startStation, 0, line});
        }
        visited.add(startStation.getName());
        
        while (!queue.isEmpty()) {
            Object[] current = queue.poll();
            Station station = (Station) current[0];
            int distance = (int) current[1];
            Line line = (Line) current[2];
            
            if (distance > 0) {
                // 不包括起始站点自己
                result.add(new AbstractMap.SimpleEntry<>(
                    station.getName(), 
                    new AbstractMap.SimpleEntry<>(line.getName(), distance)
                ));
            }
            
            if (distance < n) {
                for (Map.Entry<Station, Edge> entry : station.getAdjacentStations().entrySet()) {
                    Station nextStation = entry.getKey();
                    Edge edge = entry.getValue();
                    
                    if (!visited.contains(nextStation.getName()) && edge.getLine().equals(line)) {
                        queue.offer(new Object[]{nextStation, distance + 1, line});
                        visited.add(nextStation.getName());
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 查找从起点到终点的所有可能路径（无环路径）
     * @param startName 起点站名称
     * @param endName 终点站名称
     * @return 所有可能的路径集合
     */
    public List<Path> findAllPaths(String startName, String endName) {
        Station startStation = stations.get(startName);
        Station endStation = stations.get(endName);
        
        if (startStation == null) {
            throw new IllegalArgumentException("起点站不存在: " + startName);
        }
        if (endStation == null) {
            throw new IllegalArgumentException("终点站不存在: " + endName);
        }
        
        List<Path> result = new ArrayList<>();
        Path currentPath = new Path();
        currentPath.addFirstStation(startStation);
        Set<Station> visited = new HashSet<>();
        visited.add(startStation);
        
        findAllPathsDFS(startStation, endStation, visited, currentPath, result);
        
        return result;
    }
    
    /**
     * 使用深度优先搜索查找所有路径
     */
    private void findAllPathsDFS(Station current, Station end, Set<Station> visited, 
                                 Path currentPath, List<Path> result) {
        if (current.equals(end)) {
            result.add(new Path(currentPath));
            return;
        }
        
        for (Map.Entry<Station, Edge> entry : current.getAdjacentStations().entrySet()) {
            Station next = entry.getKey();
            Edge edge = entry.getValue();
            
            if (!visited.contains(next)) {
                visited.add(next);
                Path newPath = new Path(currentPath);
                newPath.addStation(next, edge.getLine(), edge.getDistance());
                findAllPathsDFS(next, end, visited, newPath, result);
                visited.remove(next);
            }
        }
    }
    
    /**
     * 查找从起点到终点的最短路径（按距离最短）
     * @param startName 起点站名称
     * @param endName 终点站名称
     * @return 最短路径
     */
    public Path findShortestPath(String startName, String endName) {
        Station startStation = stations.get(startName);
        Station endStation = stations.get(endName);
        
        if (startStation == null) {
            throw new IllegalArgumentException("起点站不存在: " + startName);
        }
        if (endStation == null) {
            throw new IllegalArgumentException("终点站不存在: " + endName);
        }
        
        // 使用Dijkstra算法找最短路径
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previousStations = new HashMap<>();
        Map<Station, Edge> previousEdges = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(
            Comparator.comparingDouble(station -> distances.getOrDefault(station, Double.MAX_VALUE))
        );
        
        // 初始化
        for (Station station : stations.values()) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(startStation, 0.0);
        queue.add(startStation);
        
        while (!queue.isEmpty()) {
            Station current = queue.poll();
            
            if (current.equals(endStation)) {
                break;
            }
            
            if (distances.get(current) == Double.MAX_VALUE) {
                break;
            }
            
            for (Map.Entry<Station, Edge> entry : current.getAdjacentStations().entrySet()) {
                Station neighbor = entry.getKey();
                Edge edge = entry.getValue();
                double distance = distances.get(current) + edge.getDistance();
                
                if (distance < distances.get(neighbor)) {
                    distances.put(neighbor, distance);
                    previousStations.put(neighbor, current);
                    previousEdges.put(neighbor, edge);
                    
                    // 重新入队，以更新优先级
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        // 如果没有找到路径
        if (previousStations.get(endStation) == null) {
            return null;
        }
        
        // 重建路径
        Path path = new Path();
        Station current = endStation;
        Stack<Station> stationStack = new Stack<>();
        Stack<Edge> edgeStack = new Stack<>();
        
        while (current != startStation) {
            stationStack.push(current);
            edgeStack.push(previousEdges.get(current));
            current = previousStations.get(current);
        }
        
        path.addFirstStation(startStation);
        
        while (!stationStack.isEmpty()) {
            Station station = stationStack.pop();
            Edge edge = edgeStack.pop();
            path.addStation(station, edge.getLine(), edge.getDistance());
        }
        
        return path;
    }
    
    /**
     * 将路径以简洁形式输出
     * @param path 路径对象
     * @return 格式化的路径信息
     */
    public String formatPath(Path path) {
        if (path == null || path.getStations().size() <= 1) {
            return "无效路径";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("乘车路线：\n");
        
        List<Station> stations = path.getStations();
        List<Line> lines = path.getLines();
        
        Line currentLine = lines.get(0);
        Station startStation = stations.get(0);
        Station currentStation = startStation;
        
        sb.append("从 ").append(startStation.getName()).append(" 乘坐 ").append(currentLine.getName());
        
        for (int i = 1; i < stations.size(); i++) {
            Line line = lines.get(i-1);
            Station station = stations.get(i);
            
            // 检查是否换乘
            if (i < lines.size() && !line.equals(lines.get(i))) {
                sb.append(" 到 ").append(station.getName()).append("\n");
                sb.append("换乘 ").append(lines.get(i).getName());
                currentLine = lines.get(i);
            } else if (i == stations.size() - 1) {
                sb.append(" 到终点站 ").append(station.getName());
            }
            
            currentStation = station;
        }
        
        sb.append("\n总距离: ").append(String.format("%.2f", path.getTotalDistance())).append("公里");
        sb.append("\n换乘次数: ").append(path.getTransferCount());
        
        return sb.toString();
    }
    
    /**
     * 计算路径票价（普通单程票）
     * @param path 路径对象
     * @return 票价（元）
     */
    public double calculateFare(Path path) {
        return pricingSystem.calculateFare(path.getTotalDistance());
    }
    
    /**
     * 计算使用武汉通卡的票价（9折）
     * @param path 路径对象
     * @return 票价（元）
     */
    public double calculateWuhanTongFare(Path path) {
        return pricingSystem.calculateWuhanTongFare(path.getTotalDistance());
    }
    
    /**
     * 计算使用日票的票价（固定价格）
     * @param ticketType 票类型（"1日票", "3日票", "7日票"）
     * @return 票价（元）
     */
    public double calculateDayPassFare(String ticketType) {
        return pricingSystem.calculateDayPassFare(ticketType);
    }
    
    /**
     * 获取站点
     * @param name 站点名称
     * @return 站点对象
     */
    public Station getStation(String name) {
        return stations.get(name);
    }
    
    /**
     * 获取线路
     * @param name 线路名称
     * @return 线路对象
     */
    public Line getLine(String name) {
        return lines.get(name);
    }
    
    /**
     * 获取所有站点
     * @return 所有站点的Map
     */
    public Map<String, Station> getStations() {
        return stations;
    }
    
    /**
     * 获取所有线路
     * @return 所有线路的Map
     */
    public Map<String, Line> getLines() {
        return lines;
    }
} 