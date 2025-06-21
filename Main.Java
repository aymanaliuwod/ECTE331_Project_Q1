package faulttolerant;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class to simulate a fault-tolerant embedded system with 3 sensors.
 */
public class Main {

    public static final Random random = new Random();
    private static double lastValidSensor3Value = 0.0;

    public static void main(String[] args) {
        String baseLogName = "log"; // base log file name

        // Generate and display temperature and humidity readings
        System.out.println("Temperature: " + generateTemperature(42.5));
        System.out.println("Humidity: " + generateHumidity(100.0));

        // Generate readings from three redundant Sensor 3 replicas
        double s1 = generateSensor3Value();
        double s2 = generateSensor3Value();
        double s3 = generateSensor3Value();

        System.out.println("Sensor 3.1: " + s1);
        System.out.println("Sensor 3.2: " + s2);
        System.out.println("Sensor 3.3: " + s3);

        // Perform majority voting
        Double majority = majorityVote(s1, s2, s3);
        if (majority != null) {
            lastValidSensor3Value = majority;
            System.out.println("Majority Value: " + majority);
        } else {
            // Identify and log outlier sensors
            List<String> outliers = getOutlierSensors(s1, s2, s3);
            String outlierStr = String.join(", ", outliers);
            String msg = String.format(
                "Discrepancy Detected - Values: %.2f, %.2f, %.2f. Outliers: %s. Fallback to previous: %.2f",
                s1, s2, s3, outlierStr, lastValidSensor3Value
            );
            FileLogger.log(baseLogName, msg);
            System.out.println(msg);
        }
    }

    /**
     * Generates a random temperature reading.
     */
    public static double generateTemperature(double maxTemp) {
        return random.nextDouble() * maxTemp;
    }

    /**
     * Generates a random humidity reading.
     */
    public static double generateHumidity(double maxHumidity) {
        return random.nextDouble() * maxHumidity;
    }

    /**
     * Simulates a sensor 3 reading.
     */
    public static double generateSensor3Value() {
        return random.nextDouble() * 100;
    }

    /**
     * Returns the majority value among three inputs, or null if no majority exists.
     */
    public static Double majorityVote(double v1, double v2, double v3) {
        final double TOL = 0.01;
        boolean ab = Math.abs(v1 - v2) < TOL;
        boolean bc = Math.abs(v2 - v3) < TOL;
        boolean ac = Math.abs(v1 - v3) < TOL;

        if (ab && bc) return v1;
        if (ab) return v1;
        if (bc) return v2;
        if (ac) return v1;

        return null; // No agreement
    }

    /**
     * Identifies which sensors are outliers when no majority exists.
     */
    public static List<String> getOutlierSensors(double s1, double s2, double s3) {
        final double TOL = 0.01;
        List<String> outliers = new ArrayList<>();

        boolean ab = Math.abs(s1 - s2) < TOL;
        boolean bc = Math.abs(s2 - s3) < TOL;
        boolean ac = Math.abs(s1 - s3) < TOL;

        if (ab && !ac && !bc) outliers.add("Sensor 3.3");
        else if (ac && !ab && !bc) outliers.add("Sensor 3.2");
        else if (bc && !ab && !ac) outliers.add("Sensor 3.1");
        else {
            outliers.add("Sensor 3.1");
            outliers.add("Sensor 3.2");
            outliers.add("Sensor 3.3");
        }

        return outliers;
    }
}
