import java.io.*;
import java.util.*;

/**
 * 地铁网络系统核心类
 */
public class MetroNetwork {
    private Map<String, Station> stations;          // 所有站点
    private Map<String, MetroLine> lines;           // 所有线路
    private static final double BASE_PRICE = 2.0;   // 基础票价
    private static final double[] PRICE_RANGES = {6, 10, 16, 24, 32};  // 里程分段
    private static final double[] PRICES = {2, 3, 4, 5, 6, 7};        // 对应票价
    
    public MetroNetwork() {
        this.stations = new HashMap<>();
        this.lines = new HashMap<>();
    }
    
    /**
     * 从文件加载地铁数据
     */
    public void loadDataFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        String currentLineName = null;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // 识别线路标题
            if (line.contains("号线站点间距") || line.contains("线站点间距")) {
                currentLineName = extractLineName(line);
                if (!lines.containsKey(currentLineName)) {
                    lines.put(currentLineName, new MetroLine(currentLineName));
                }
                continue;
            }
            
            // 跳过表头
            if (line.contains("站点名称") || line.contains("间距")) {
                continue;
            }
            
            // 解析站点间距数据
            if (currentLineName != null && line.contains("---") && line.contains("\t")) {
                parseStationDistance(line, currentLineName);
            }
        }
        reader.close();
        
        // 构建站点邻接关系
        buildStationNetwork();
    }
    
    private String extractLineName(String line) {
        if (line.contains("阳逻线")) return "阳逻线";
        
        // 提取数字
        for (int i = 0; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                StringBuilder number = new StringBuilder();
                while (i < line.length() && Character.isDigit(line.charAt(i))) {
                    number.append(line.charAt(i));
                    i++;
                }
                return number.toString() + "号线";
            }
        }
        return line.split("站点间距")[0];
    }
    
    private void parseStationDistance(String line, String lineName) {
        String[] parts = line.split("\t");
        if (parts.length >= 2) {
            String stationPair = parts[0].trim();
            String distanceStr = parts[1].trim();
            
            if (stationPair.contains("---")) {
                String[] stationNames = stationPair.split("---");
                if (stationNames.length == 2) {
                    String from = stationNames[0].trim();
                    String to = stationNames[1].trim();
                    
                    try {
                        double distance = Double.parseDouble(distanceStr);
                        
                        // 创建或获取站点
                        Station fromStation = stations.computeIfAbsent(from, Station::new);
                        Station toStation = stations.computeIfAbsent(to, Station::new);
                        
                        // 添加线路信息
                        fromStation.addLine(lineName);
                        toStation.addLine(lineName);
                        
                        // 添加到线路
                        MetroLine metroLine = lines.get(lineName);
                        metroLine.addStationDistance(from, to, distance);
                        
                    } catch (NumberFormatException e) {
                        System.err.println("解析距离失败: " + distanceStr);
                    }
                }
            }
        }
    }
    
    private void buildStationNetwork() {
        for (MetroLine line : lines.values()) {
            for (String stationName : line.getStations()) {
                Station station = stations.get(stationName);
                if (station != null) {
                    // 添加邻接站点
                    for (String otherStation : line.getStations()) {
                        if (!stationName.equals(otherStation)) {
                            double distance = line.getDistance(stationName, otherStation);
                            if (distance > 0) {
                                station.addNeighbor(line.getName(), otherStation, distance);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 1. 获取所有中转站
     */
    public Set<String> getTransferStations() {
        Set<String> transferStations = new HashSet<>();
        for (Station station : stations.values()) {
            if (station.isTransferStation()) {
                StringBuilder info = new StringBuilder();
                info.append("<").append(station.getName()).append("站，<");
                List<String> linesList = new ArrayList<>(station.getLines());
                Collections.sort(linesList);
                info.append(String.join("、", linesList));
                info.append(">>");
                transferStations.add(info.toString());
            }
        }
        return transferStations;
    }
    
    /**
     * 2. 获取距离指定站点小于n公里的所有站点
     */
    public Set<String> getNearbyStations(String stationName, double maxDistance) {
        if (!stations.containsKey(stationName)) {
            throw new IllegalArgumentException("站点不存在: " + stationName);
        }
        
        Set<String> result = new HashSet<>();
        Map<String, Double> distances = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(
            Comparator.comparing(distances::get)
        );
        
        distances.put(stationName, 0.0);
        queue.offer(stationName);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            double currentDist = distances.get(current);
            
            if (currentDist >= maxDistance) continue;
            
            Station currentStation = stations.get(current);
            for (String line : currentStation.getLines()) {
                Map<String, Double> neighbors = currentStation.getNeighbors().get(line);
                if (neighbors != null) {
                    for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                        String neighbor = entry.getKey();
                        double edgeWeight = entry.getValue();
                        double newDist = currentDist + edgeWeight;
                        
                        if (newDist < maxDistance && 
                            (!distances.containsKey(neighbor) || newDist < distances.get(neighbor))) {
                            distances.put(neighbor, newDist);
                            queue.offer(neighbor);
                            
                            if (!neighbor.equals(stationName)) {
                                String info = String.format("<%s站，%s，%.3f>", 
                                    neighbor, line, newDist);
                                result.add(info);
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 3. 获取起点到终点的所有路径（不含环路）
     */
    public List<Path> getAllPaths(String start, String end) {
        if (!stations.containsKey(start) || !stations.containsKey(end)) {
            throw new IllegalArgumentException("起点或终点站点不存在");
        }
        
        List<Path> allPaths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Path currentPath = new Path();
        
        dfsAllPaths(start, end, visited, currentPath, allPaths);
        return allPaths;
    }
    
    private void dfsAllPaths(String current, String target, Set<String> visited, 
                           Path currentPath, List<Path> allPaths) {
        visited.add(current);
        
        if (current.equals(target)) {
            if (!currentPath.isEmpty()) {
                allPaths.add(new Path(currentPath.getStations(), 
                                    currentPath.getLines(), 
                                    currentPath.getTotalDistance()));
            }
            visited.remove(current);
            return;
        }
        
        Station currentStation = stations.get(current);
        for (String line : currentStation.getLines()) {
            Map<String, Double> neighbors = currentStation.getNeighbors().get(line);
            if (neighbors != null) {
                for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                    String neighbor = entry.getKey();
                    double distance = entry.getValue();
                    
                    if (!visited.contains(neighbor)) {
                        currentPath.addSegment(neighbor, line, distance);
                        dfsAllPaths(neighbor, target, visited, currentPath, allPaths);
                        
                        // 回溯
                        List<String> stations = currentPath.getStations();
                        List<String> lines = currentPath.getLines();
                        if (!stations.isEmpty()) {
                            stations.remove(stations.size() - 1);
                            lines.remove(lines.size() - 1);
                            currentPath = new Path(stations, lines, 
                                                 currentPath.getTotalDistance() - distance);
                        }
                    }
                }
            }
        }
        
        visited.remove(current);
    }
    
    /**
     * 4. 获取最短路径
     */
    public Path getShortestPath(String start, String end) {
        if (!stations.containsKey(start) || !stations.containsKey(end)) {
            throw new IllegalArgumentException("起点或终点站点不存在");
        }
        
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Map<String, String> lineUsed = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(
            Comparator.comparing(distances::get)
        );
        
        // 初始化
        for (String station : stations.keySet()) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.offer(start);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(end)) break;
            
            Station currentStation = stations.get(current);
            for (String line : currentStation.getLines()) {
                Map<String, Double> neighbors = currentStation.getNeighbors().get(line);
                if (neighbors != null) {
                    for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                        String neighbor = entry.getKey();
                        double edgeWeight = entry.getValue();
                        double newDist = distances.get(current) + edgeWeight;
                        
                        if (newDist < distances.get(neighbor)) {
                            distances.put(neighbor, newDist);
                            previous.put(neighbor, current);
                            lineUsed.put(neighbor, line);
                            queue.offer(neighbor);
                        }
                    }
                }
            }
        }
        
        // 重构路径
        if (!previous.containsKey(end) && !start.equals(end)) {
            return new Path(); // 空路径表示无法到达
        }
        
        List<String> pathStations = new ArrayList<>();
        List<String> pathLines = new ArrayList<>();
        String current = end;
        
        while (current != null) {
            pathStations.add(0, current);
            if (lineUsed.containsKey(current)) {
                pathLines.add(0, lineUsed.get(current));
            }
            current = previous.get(current);
        }
        
        return new Path(pathStations, pathLines, distances.get(end));
    }
    
    /**
     * 5. 格式化打印路径
     */
    public void printFormattedPath(Path path) {
        if (path.isEmpty()) {
            System.out.println("无法找到路径");
            return;
        }
        
        List<String> stations = path.getStations();
        List<String> lines = path.getLines();
        
        if (stations.size() < 2) {
            System.out.println("起点和终点相同");
            return;
        }
        
        System.out.println("推荐路径：");
        System.out.printf("总距离：%.3f公里，换乘次数：%d次\n", 
                         path.getTotalDistance(), path.getTransferCount());
        
        String currentLine = lines.get(0);
        String segmentStart = stations.get(0);
        
        for (int i = 1; i < stations.size(); i++) {
            if (i == stations.size() - 1 || !lines.get(i).equals(currentLine)) {
                // 输出当前段
                System.out.printf("乘坐%s从%s站到%s站", 
                                currentLine, segmentStart, stations.get(i));
                if (i < stations.size() - 1) {
                    System.out.print("，换乘" + lines.get(i));
                }
                System.out.println();
                
                if (i < stations.size() - 1) {
                    currentLine = lines.get(i);
                    segmentStart = stations.get(i);
                }
            }
        }
    }
    
    /**
     * 6. 计算普通单程票价
     */
    public double calculateRegularFare(Path path) {
        double distance = path.getTotalDistance();
        
        for (int i = 0; i < PRICE_RANGES.length; i++) {
            if (distance <= PRICE_RANGES[i]) {
                return PRICES[i];
            }
        }
        return PRICES[PRICES.length - 1]; // 超过最大范围使用最高票价
    }
    
    /**
     * 7. 计算武汉通票价（9折）
     */
    public double calculateWuhanCardFare(Path path) {
        return calculateRegularFare(path) * 0.9;
    }
    
    /**
     * 计算日票票价
     */
    public double calculateDayPassFare(int days) {
        switch (days) {
            case 1: return 18.0;
            case 3: return 45.0;
            case 7: return 90.0;
            default: return 0.0; // 无效天数
        }
    }
    
    // Getter方法
    public Map<String, Station> getStations() {
        return stations;
    }
    
    public Map<String, MetroLine> getLines() {
        return lines;
    }
} 