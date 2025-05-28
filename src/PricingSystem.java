/**
 * 地铁票价计算系统类
 */
public class PricingSystem {
    // 票价区间
    private final double[] DISTANCE_TIERS = {4, 8, 12, 24, 40, 50, 70};
    // 对应票价（元）
    private final double[] FARE_TIERS = {2, 3, 4, 5, 6, 7, 8, 9};
    
    // 定期票价格
    private final double ONE_DAY_PASS = 18.0;
    private final double THREE_DAY_PASS = 45.0;
    private final double SEVEN_DAY_PASS = 90.0;
    
    // 武汉通折扣率
    private final double WUHAN_TONG_DISCOUNT = 0.9;
    
    /**
     * 根据乘车距离计算票价（普通单程票）
     * @param distance 乘车距离（公里）
     * @return 票价（元）
     */
    public double calculateFare(double distance) {
        for (int i = 0; i < DISTANCE_TIERS.length; i++) {
            if (distance <= DISTANCE_TIERS[i]) {
                return FARE_TIERS[i];
            }
        }
        return FARE_TIERS[FARE_TIERS.length - 1];
    }
    
    /**
     * 计算武汉通卡票价（9折优惠）
     * @param distance 乘车距离（公里）
     * @return 票价（元）
     */
    public double calculateWuhanTongFare(double distance) {
        double standardFare = calculateFare(distance);
        return Math.round(standardFare * WUHAN_TONG_DISCOUNT * 10) / 10.0;
    }
    
    /**
     * 计算定期票票价
     * @param ticketType 票类型（"1日票", "3日票", "7日票"）
     * @return 票价（元）
     */
    public double calculateDayPassFare(String ticketType) {
        switch (ticketType) {
            case "1日票":
                return ONE_DAY_PASS;
            case "3日票":
                return THREE_DAY_PASS;
            case "7日票":
                return SEVEN_DAY_PASS;
            default:
                throw new IllegalArgumentException("无效的定期票类型: " + ticketType);
        }
    }
} 