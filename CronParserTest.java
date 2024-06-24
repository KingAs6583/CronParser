
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Syntax <minutes> <hours> <day-of-month> <month> <day-of-week> <shell-command>
/* Executing CMDS In Linux Mint
 * javac -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar CronParserTest.java 
 * java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore CronParserTest
 */

public class CronParserTest {

    @Test
    public void testParseSimpleCron() {
        String cronExpr = "1 3 5 7 * ls";

        Map<String, List<String>> expected = new HashMap<>();
        expected.put(CronExpressionParser.FIELD_NAMES[0], List.of("1"));
        expected.put(CronExpressionParser.FIELD_NAMES[1], List.of("3"));
        expected.put(CronExpressionParser.FIELD_NAMES[2], List.of("5"));
        expected.put(CronExpressionParser.FIELD_NAMES[3], List.of("7"));
        expected.put(CronExpressionParser.FIELD_NAMES[4], List.of("0", "1", "2", "3", "4", "5", "6"));
        expected.put("command", Arrays.asList("ls"));

        Map<String, List<String>> actual = CronExpressionParser.parseCron(cronExpr);
        assertEquals(expected, actual);

    }

    @Test
    public void testParseCronWithComplexExpr() {
        String cronExpr = "*/15 */4 1,15 1-3 * find";
        Map<String, List<String>> expected = new HashMap<>();
        expected.put(CronExpressionParser.FIELD_NAMES[0], List.of("0", "15", "30", "45"));
        expected.put(CronExpressionParser.FIELD_NAMES[1], List.of("0", "4", "8", "12", "16", "20"));
        expected.put(CronExpressionParser.FIELD_NAMES[2], List.of("1", "15"));
        expected.put(CronExpressionParser.FIELD_NAMES[3], List.of("1", "2", "3"));
        expected.put(CronExpressionParser.FIELD_NAMES[4], List.of("0", "1", "2", "3", "4", "5", "6"));
        expected.put("command", Arrays.asList("find"));

        Map<String, List<String>> actual = CronExpressionParser.parseCron(cronExpr);
        assertEquals(expected, actual);
    }

    @Test
    public void testParseCronWithIntervel() {
        String cronExpr = "3/5 1-3 4 5 3 find";
        Map<String, List<String>> expected = new HashMap<>();
        expected.put(CronExpressionParser.FIELD_NAMES[0],
                List.of("3", "8", "13", "18", "23", "28", "33", "38", "43", "48", "53", "58"));
        expected.put(CronExpressionParser.FIELD_NAMES[1], List.of("1", "2", "3"));
        expected.put(CronExpressionParser.FIELD_NAMES[2], List.of("4"));
        expected.put(CronExpressionParser.FIELD_NAMES[3], List.of("5"));
        expected.put(CronExpressionParser.FIELD_NAMES[4], List.of("3"));
        expected.put("command", Arrays.asList("find"));

        Map<String, List<String>> actual = CronExpressionParser.parseCron(cronExpr);
        assertEquals(expected, actual);
    }

    @Test
    public void testParseCronWithRange() {
        String cronExpr = "0 1-3 4 5 3 echo";
        Map<String, List<String>> expected = new HashMap<>();
        expected.put(CronExpressionParser.FIELD_NAMES[0], List.of("0"));
        expected.put(CronExpressionParser.FIELD_NAMES[1], List.of("1", "2", "3"));
        expected.put(CronExpressionParser.FIELD_NAMES[2], List.of("4"));
        expected.put(CronExpressionParser.FIELD_NAMES[3], List.of("5"));
        expected.put(CronExpressionParser.FIELD_NAMES[4], List.of("3"));
        expected.put("command", Arrays.asList("echo"));

        Map<String, List<String>> actual = CronExpressionParser.parseCron(cronExpr);
        assertEquals(expected, actual);
    }

    /* Testing expection */
    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidCron() {
        String cronExpr = " Invalid expression";
        CronExpressionParser.parseCron(cronExpr);
    }

    @Test
    public void testParserWithComma() {
        String cronExpr = "15 0 1,15 5 1-5 ls";
        Map<String, List<String>> expected = new HashMap<>();
        expected.put(CronExpressionParser.FIELD_NAMES[0], List.of("15"));
        expected.put(CronExpressionParser.FIELD_NAMES[1], List.of("0"));
        expected.put(CronExpressionParser.FIELD_NAMES[2], List.of("1", "15"));
        expected.put(CronExpressionParser.FIELD_NAMES[3], List.of("5"));
        expected.put(CronExpressionParser.FIELD_NAMES[4], List.of("1", "2", "3", "4", "5"));
        expected.put("command", Arrays.asList("ls"));

        Map<String, List<String>> actual = CronExpressionParser.parseCron(cronExpr);
        assertEquals(expected, actual);
    }

    @Test
    public void testParserWithQusestionExpr() {
        String cronExpr = "*/15 0 1,15 * 1-5 /usr/bin/find";
        Map<String, List<String>> expected = new HashMap<>();
        expected.put(CronExpressionParser.FIELD_NAMES[0], List.of("0", "15", "30", "45"));
        expected.put(CronExpressionParser.FIELD_NAMES[1], List.of("0"));
        expected.put(CronExpressionParser.FIELD_NAMES[2], List.of("1", "15"));
        expected.put(CronExpressionParser.FIELD_NAMES[3],
                List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
        expected.put(CronExpressionParser.FIELD_NAMES[4], List.of("1", "2", "3", "4", "5"));
        expected.put("command", Arrays.asList("/usr/bin/find"));

        Map<String, List<String>> actual = CronExpressionParser.parseCron(cronExpr);
        assertEquals(expected, actual);

    }

}
