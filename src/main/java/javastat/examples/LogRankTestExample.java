package javastat.examples;

import static java.lang.System.out;
import java.util.*;

import javastat.*;
import javastat.survival.inference.*;
import static javastat.util.Output.*;
import javastat.util.*;

/**
 *
 * <p>Example: class LogRankTest.</p>
 * <p>Data Source: Collett, D. (1994). Modelling Survival Data in Medical
 *    Research. New York: Chapman and Hall, pp. 290-291. </p>
 */

public class LogRankTestExample
{

    public static void main(String arg[])
    {
        double[] time1 = {156, 1040, 59, 329, 268, 638, 1106, 431, 855, 803,
                          115, 477, 448};
        double[] time2 = {421, 769, 365, 770, 1227, 475, 1129, 464, 1206, 563,
                          744, 353, 377};
        double[] censor1 = {1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0};
        double[] censor2 = {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0};
        DataManager dm = new DataManager();

        LogRankTest testclass1 =
                new LogRankTest(time1, censor1, time2, censor2);
        double testStatistic = testclass1.testStatistic;
        double pValue = testclass1.pValue;
        out.println("The log rank statistic based on non-null constructor" +
                    "   = " + dm.roundDigits(testStatistic, 3.0));
        out.println("The p-value for the test based on non-null constructor" +
                    " = " + dm.roundDigits(pValue, 3.0));

        LogRankTest testclass2 = new LogRankTest();
        testStatistic =
                testclass2.testStatistic(time1, censor1, time2, censor2);
        pValue = testclass2.pValue(time1, censor1, time2, censor2);
        out.println("\n\nThe log rank statistic based on null constructor" +
                    "       = " + dm.roundDigits(testStatistic, 3.0));
        out.println("The p-value for the test based on non-null constructor" +
                    " = " + dm.roundDigits(pValue, 3.0));

        Hashtable argument = new Hashtable();
        StatisticalAnalysis testclass3 = new LogRankTest(argument, time1,
                censor1, time2, censor2).statisticalAnalysis;
        testStatistic = (Double) testclass3.output.get(TEST_STATISTIC);
        pValue = (Double) testclass3.output.get(PVALUE);
        out.println("\n\nThe log rank statistic based on non-null constructor" +
                    "   = " + dm.roundDigits(testStatistic, 3.0));
        out.println("The p-value for the test based on non-null constructor" +
                    " = " + dm.roundDigits(pValue, 3.0));
        out.println("\n\n" + testclass3.output.toString());

        LogRankTest testclass4 = new LogRankTest(argument, null);
        testStatistic = testclass4.testStatistic(argument, time1, censor1,
                                                 time2, censor2);
        pValue = testclass4.pValue(argument, time1, censor1, time2, censor2);
        out.println("\n\nThe log rank statistic based on null constructor" +
                    "       = " + dm.roundDigits(testStatistic, 3.0));
        out.println("The p-value for the test based on non-null " +
                    "constructor = " + dm.roundDigits(pValue, 3.0));
        out.println("\n\n" + testclass4.output.toString());
    }

}
