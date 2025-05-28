import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 测试类，用于测试地铁系统的各项功能
 */
public class Test {
    public static void main(String[] args) {
        SubwaySystem subwaySystem = new SubwaySystem();
        
        try {
            // 加载地铁数据
            subwaySystem.loadFromFile("src/subway.txt");
            System.out.println("地铁数据加载成功！");
            
            // 1. 测试查找所有中转站
            testFindTransferStations(subwaySystem);
            
            // 2. 测试查找指定距离内的站点
            testFindStationsWithinDistance(subwaySystem);
            
            // 3. 测试查找所有路径
            testFindAllPaths(subwaySystem);
            
            // 4. 测试查找最短路径
            testFindShortestPath(subwaySystem);
            
            // 5. 测试格式化路径输出
            testFormatPath(subwaySystem);
            
            // 6. 测试计算票价（普通票）
            testCalculateFare(subwaySystem);
            
            // 7. 测试计算票价（武汉通和日票）
            testCalculateSpecialFare(subwaySystem);
            
            // 交互式测试
            // interactiveTest(subwaySystem);
            
        } catch (IOException e) {
            System.err.println("加载地铁数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试查找所有中转站
     */
    private static void testFindTransferStations(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试1：查找所有中转站 =====");
        List<Map.Entry<String, Set<String>>> transferStations = subwaySystem.getTransferStations();
        System.out.println("武汉地铁共有 " + transferStations.size() + " 个中转站：");
        for (Map.Entry<String, Set<String>> entry : transferStations) {
            System.out.println(entry.getKey() + "：" + entry.getValue());
        }
    }
    
    /**
     * 测试查找指定距离内的站点
     */
    private static void testFindStationsWithinDistance(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试2：查找指定距离内的站点 =====");
        try {
            String stationName = "华中科技大学";
            int distance = 2;
            System.out.println("查找距离 " + stationName + " 站 " + distance + " 站以内的所有站点：");
            
            List<Map.Entry<String, Map.Entry<String, Integer>>> nearbyStations = 
                subwaySystem.getStationsWithinDistance(stationName, distance);
            
            for (Map.Entry<String, Map.Entry<String, Integer>> entry : nearbyStations) {
                String nearbyStationName = entry.getKey();
                String lineName = entry.getValue().getKey();
                int stationDistance = entry.getValue().getValue();
                System.out.println(nearbyStationName + "，" + lineName + "，距离：" + stationDistance + " 站");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试查找所有路径
     */
    private static void testFindAllPaths(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试3：查找所有路径 =====");
        try {
            String startName = "光谷广场";
            String endName = "中南路";
            System.out.println("查找从 " + startName + " 到 " + endName + " 的所有路径：");
            
            List<Path> paths = subwaySystem.findAllPaths(startName, endName);
            System.out.println("共找到 " + paths.size() + " 条路径");
            
            // 显示前3条路径
            int count = Math.min(3, paths.size());
            for (int i = 0; i < count; i++) {
                System.out.println("\n路径 " + (i+1) + ":");
                System.out.println(paths.get(i));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试查找最短路径
     */
    private static void testFindShortestPath(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试4：查找最短路径 =====");
        try {
            String startName = "光谷广场";
            String endName = "中南路";
            System.out.println("查找从 " + startName + " 到 " + endName + " 的最短路径：");
            
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                System.out.println(shortestPath);
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试格式化路径输出
     */
    private static void testFormatPath(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试5：格式化路径输出 =====");
        try {
            String startName = "光谷广场";
            String endName = "中南路";
            System.out.println("从 " + startName + " 到 " + endName + " 的乘车指南：");
            
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                String formattedPath = subwaySystem.formatPath(shortestPath);
                System.out.println(formattedPath);
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试计算票价（普通票）
     */
    private static void testCalculateFare(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试6：计算票价（普通票） =====");
        try {
            String startName = "光谷广场";
            String endName = "中南路";
            System.out.println("计算从 " + startName + " 到 " + endName + " 的票价：");
            
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                double fare = subwaySystem.calculateFare(shortestPath);
                System.out.println("距离: " + String.format("%.2f", shortestPath.getTotalDistance()) + " 公里");
                System.out.println("普通票价: " + fare + " 元");
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 测试计算票价（武汉通和日票）
     */
    private static void testCalculateSpecialFare(SubwaySystem subwaySystem) {
        System.out.println("\n===== 测试7：计算票价（武汉通和日票） =====");
        try {
            String startName = "光谷广场";
            String endName = "中南路";
            System.out.println("计算从 " + startName + " 到 " + endName + " 的票价：");
            
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                double standardFare = subwaySystem.calculateFare(shortestPath);
                double wuhanTongFare = subwaySystem.calculateWuhanTongFare(shortestPath);
                
                System.out.println("距离: " + String.format("%.2f", shortestPath.getTotalDistance()) + " 公里");
                System.out.println("普通票价: " + standardFare + " 元");
                System.out.println("武汉通票价(9折): " + wuhanTongFare + " 元");
                System.out.println("1日票: " + subwaySystem.calculateDayPassFare("1日票") + " 元");
                System.out.println("3日票: " + subwaySystem.calculateDayPassFare("3日票") + " 元");
                System.out.println("7日票: " + subwaySystem.calculateDayPassFare("7日票") + " 元");
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    /**
     * 交互式测试
     */
    private static void interactiveTest(SubwaySystem subwaySystem) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== 武汉地铁查询系统 =====");
            System.out.println("1. 查询中转站");
            System.out.println("2. 查询指定距离内的站点");
            System.out.println("3. 查询两站之间的所有路径");
            System.out.println("4. 查询两站之间的最短路径");
            System.out.println("5. 获取行程指南");
            System.out.println("6. 计算票价");
            System.out.println("0. 退出");
            System.out.print("请选择功能(0-6): ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请重新输入");
                continue;
            }
            
            switch (choice) {
                case 0:
                    exit = true;
                    break;
                case 1:
                    interactiveTestFindTransferStations(subwaySystem);
                    break;
                case 2:
                    interactiveTestFindStationsWithinDistance(subwaySystem, scanner);
                    break;
                case 3:
                    interactiveTestFindAllPaths(subwaySystem, scanner);
                    break;
                case 4:
                    interactiveTestFindShortestPath(subwaySystem, scanner);
                    break;
                case 5:
                    interactiveTestFormatPath(subwaySystem, scanner);
                    break;
                case 6:
                    interactiveTestCalculateFare(subwaySystem, scanner);
                    break;
                default:
                    System.out.println("选择无效，请重新选择");
                    break;
            }
        }
        
        scanner.close();
        System.out.println("谢谢使用，再见！");
    }
    
    private static void interactiveTestFindTransferStations(SubwaySystem subwaySystem) {
        List<Map.Entry<String, Set<String>>> transferStations = subwaySystem.getTransferStations();
        System.out.println("\n武汉地铁共有 " + transferStations.size() + " 个中转站：");
        for (Map.Entry<String, Set<String>> entry : transferStations) {
            System.out.println(entry.getKey() + "：" + entry.getValue());
        }
    }
    
    private static void interactiveTestFindStationsWithinDistance(SubwaySystem subwaySystem, Scanner scanner) {
        System.out.print("\n请输入站点名称: ");
        String stationName = scanner.nextLine().trim();
        
        System.out.print("请输入距离（站数）: ");
        int distance;
        try {
            distance = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("距离输入无效");
            return;
        }
        
        try {
            List<Map.Entry<String, Map.Entry<String, Integer>>> nearbyStations = 
                subwaySystem.getStationsWithinDistance(stationName, distance);
            
            System.out.println("距离 " + stationName + " 站 " + distance + " 站以内的所有站点：");
            for (Map.Entry<String, Map.Entry<String, Integer>> entry : nearbyStations) {
                String nearbyStationName = entry.getKey();
                String lineName = entry.getValue().getKey();
                int stationDistance = entry.getValue().getValue();
                System.out.println(nearbyStationName + "，" + lineName + "，距离：" + stationDistance + " 站");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    private static void interactiveTestFindAllPaths(SubwaySystem subwaySystem, Scanner scanner) {
        System.out.print("\n请输入起点站: ");
        String startName = scanner.nextLine().trim();
        
        System.out.print("请输入终点站: ");
        String endName = scanner.nextLine().trim();
        
        try {
            List<Path> paths = subwaySystem.findAllPaths(startName, endName);
            System.out.println("从 " + startName + " 到 " + endName + " 共找到 " + paths.size() + " 条路径");
            
            // 显示前5条路径
            int count = Math.min(5, paths.size());
            for (int i = 0; i < count; i++) {
                System.out.println("\n路径 " + (i+1) + ":");
                System.out.println(paths.get(i));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    private static void interactiveTestFindShortestPath(SubwaySystem subwaySystem, Scanner scanner) {
        System.out.print("\n请输入起点站: ");
        String startName = scanner.nextLine().trim();
        
        System.out.print("请输入终点站: ");
        String endName = scanner.nextLine().trim();
        
        try {
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                System.out.println("\n从 " + startName + " 到 " + endName + " 的最短路径：");
                System.out.println(shortestPath);
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    private static void interactiveTestFormatPath(SubwaySystem subwaySystem, Scanner scanner) {
        System.out.print("\n请输入起点站: ");
        String startName = scanner.nextLine().trim();
        
        System.out.print("请输入终点站: ");
        String endName = scanner.nextLine().trim();
        
        try {
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                System.out.println("\n从 " + startName + " 到 " + endName + " 的乘车指南：");
                String formattedPath = subwaySystem.formatPath(shortestPath);
                System.out.println(formattedPath);
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
    
    private static void interactiveTestCalculateFare(SubwaySystem subwaySystem, Scanner scanner) {
        System.out.print("\n请输入起点站: ");
        String startName = scanner.nextLine().trim();
        
        System.out.print("请输入终点站: ");
        String endName = scanner.nextLine().trim();
        
        try {
            Path shortestPath = subwaySystem.findShortestPath(startName, endName);
            if (shortestPath != null) {
                System.out.println("\n从 " + startName + " 到 " + endName + " 的票价信息：");
                
                double standardFare = subwaySystem.calculateFare(shortestPath);
                double wuhanTongFare = subwaySystem.calculateWuhanTongFare(shortestPath);
                
                System.out.println("距离: " + String.format("%.2f", shortestPath.getTotalDistance()) + " 公里");
                System.out.println("换乘次数: " + shortestPath.getTransferCount() + " 次");
                System.out.println("普通票价: " + standardFare + " 元");
                System.out.println("武汉通票价(9折): " + wuhanTongFare + " 元");
                System.out.println("1日票: " + subwaySystem.calculateDayPassFare("1日票") + " 元");
                System.out.println("3日票: " + subwaySystem.calculateDayPassFare("3日票") + " 元");
                System.out.println("7日票: " + subwaySystem.calculateDayPassFare("7日票") + " 元");
                
                // 比较不同票价方式
                System.out.println("\n乘车建议：");
                if (standardFare > 18.0) {
                    System.out.println("购买1日票更划算！");
                } else if (wuhanTongFare < standardFare) {
                    System.out.println("使用武汉通卡更划算！");
                } else {
                    System.out.println("购买普通单程票即可。");
                }
            } else {
                System.out.println("未找到路径");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
} 