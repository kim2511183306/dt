/**
 * 地铁系统异常类
 */
public class Exceptions {
    /**
     * 站点不存在异常
     */
    public static class StationNotFoundException extends RuntimeException {
        public StationNotFoundException(String stationName) {
            super("站点不存在: " + stationName);
        }
    }
    
    /**
     * 线路不存在异常
     */
    public static class LineNotFoundException extends RuntimeException {
        public LineNotFoundException(String lineName) {
            super("线路不存在: " + lineName);
        }
    }
    
    /**
     * 无法找到路径异常
     */
    public static class PathNotFoundException extends RuntimeException {
        public PathNotFoundException(String startName, String endName) {
            super("无法找到从 " + startName + " 到 " + endName + " 的路径");
        }
    }
    
    /**
     * 无效票价类型异常
     */
    public static class InvalidTicketTypeException extends RuntimeException {
        public InvalidTicketTypeException(String ticketType) {
            super("无效的票价类型: " + ticketType);
        }
    }
} 