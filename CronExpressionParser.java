import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Executing cmd
 * javac CronExpressionParser.java
 * java CronExpressionParser "* /15 0 1,15 * 1-5 /usr/bin/find"
 */

// */15 0 1,15 * 1-5 /usr/bin/find

/*
 * Syntax <minutes> <hours> <day-of-month> <month> <day-of-week> <shell-command>
 * Character    Meaning
 *      *       all possiblility
 *      -       range 1-9 
 *      ,       multiple values 
 *      /       increment. Specifies the amount by which to increment the values of a field
 */

public class CronExpressionParser {

    // Use to add and reterive the value from a map in order and it is constant
    public static final String[] FIELD_NAMES = { "minute", "hour", "day of month", "month", "day of week" };

    
    public static Map<String, List<String>> parseCron(String cronExpression) {

        String[] fields = cronExpression.split("\\s+"); // using regex to split instead of regex we can use .split(" ")

        if (fields.length != 6) {
            throw new IllegalArgumentException("Invalid cron expression format: Expected 6 fields");
        }

        Map<String, List<String>> cronMap = new HashMap<>();

        // Adding the Field with expanded Field value in hashmap and passing min and max
        // value to avoid overhead
        cronMap.put(FIELD_NAMES[0], expandField(fields[0], 0, 59)); // minute min = 0 max = 59
        cronMap.put(FIELD_NAMES[1], expandField(fields[1], 0, 23)); // hour min = 0 max = 23
        cronMap.put(FIELD_NAMES[2], expandField(fields[2], 1, 31)); // day of month min = 1 max = 31
        cronMap.put(FIELD_NAMES[3], expandField(fields[3], 1, 12)); // month min = 1 max = 12
        cronMap.put(FIELD_NAMES[4], expandField(fields[4], 0, 6)); // day of week min = 0 max = 6
        cronMap.put("command", Arrays.asList(fields[5])); // linux command

        // return the value for testing and printing reason
        return cronMap;

    }

    /*
     * Function responsible for expanding Field base on the special character
     * present in the field
     */
    private static List<String> expandField(String field, int min, int max) {
        List<String> expandedValues = new ArrayList<>();

        // special condition which has to be at top to avoid logical error
        if (field.contains("*/")) {
            String[] parts = field.split("/"); 
            int step = Integer.parseInt(parts[1]); 

            for (int i = min; i <= max; i = i + step) {
                expandedValues.add(String.valueOf(i));
            }

        } else if (field.contains("*")) {
            for (int i = min; i <= max; i++) {
                expandedValues.add(String.valueOf(i));
            }

        } else if (field.contains("-")) {
            String[] parts = field.split("-"); 
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            for (int i = start; i <= end; i++) {
                expandedValues.add(String.valueOf(i));
            }

        } else if (field.contains(",")) {
            String[] values = field.split(","); 
            for (int i = 0; i < values.length; i++) {
                expandedValues.add(values[i]);
            }
        } else if (field.contains("/")) {
            String[] parts = field.split("/");
            int step = Integer.parseInt(parts[1]); 
            int base = Integer.parseInt(parts[0]);
            for (int i = base; i <= max; i = i + step) {
                expandedValues.add(String.valueOf(i));
            }
        } else {
            expandedValues.add(field);
        }

        return expandedValues;
    }

    /*
     * A Function to display Fields in a table formate
     */
    public static void showExpandedFields(Map<String, List<String>> cronMap) {
        System.out.printf("%-14s %s \n", "Field Name", "Times");
        int i;
        for (i = 0; i < cronMap.size() - 1; i++) {
            // formating output
            System.out.printf("%-14s %s \n", FIELD_NAMES[i], String.join(" ", cronMap.get(FIELD_NAMES[i])));
            //.join convert list to string
        }
        System.out.printf("%-14s %s \n", "command", String.join(" ", cronMap.get("command")));
    }

    public static void main(String[] args) {

        // checking if command line argument are passed or not
        if (args.length != 1) {
            System.out.println("Please Pass Cron Expression in Command Line");
            System.out.println("Usage: java CronExpressionParser <cron_expression>");
            return; // because void 
        }

        try {
            Map<String, List<String>> cronMap = parseCron(args[0]);
            showExpandedFields(cronMap);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

}
