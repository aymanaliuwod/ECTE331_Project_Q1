package faulttolerant;

import java.util.Random;


public class Main {

    public static final Random random = new Random();
    private static double lastValidSensor3Value = 0.0;

    public static void main(String[] args) {
        String baseLogName = "log";
        System.out.println("Temperature: " + generateTemperature(42.5));
        System.out.println("Humidity: " + generateHumidity(100.0));

        double s1 = generateSensor3Value();
        double s2 = generateSensor3Value();
        double s3 = generateSensor3Value();

        System.out.println("Sensor 3.1: " + s1);
        System.out.println("Sensor 3.2: " + s2);
        System.out.println("Sensor 3.3: " + s3);

        Double majority = majorityVote(s1, s2, s3);
        if (majority != null) {
            lastValidSensor3Value = majority;
            System.out.println("Majority Value: " + majority);
        } else {
            String msg = String.format("Discrepancy Detected - Values: %.2f, %.2f, %.2f. Fallback to previous: %.2f",
                    s1, s2, s3, lastValidSensor3Value);
            FileLogger.log(baseLogName, msg);
            System.out.println(msg);
        }
    }

    public static double generateTemperature(double maxTemp) {
        return random.nextDouble() * maxTemp;
    }

    public static double generateHumidity(double maxHumidity) {
        return random.nextDouble() * maxHumidity;
    }

    public static double generateSensor3Value() {
        return random.nextDouble() * 100;
    }

    public static Double majorityVote(double v1, double v2, double v3) {
        final double TOL = 0.01;
        boolean ab = Math.abs(v1 - v2) < TOL;
        boolean bc = Math.abs(v2 - v3) < TOL;
        boolean ac = Math.abs(v1 - v3) < TOL;

        if (ab && bc) return v1;
        if (ab) return v1;
        if (bc) return v2;
        if (ac) return v1;

        return null; // All different
    }
}
