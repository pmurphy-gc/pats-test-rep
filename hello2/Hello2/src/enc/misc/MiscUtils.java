package enc.misc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.RowSet;




public class MiscUtils
{
    static public ArrayList iteratorToArrayList(Iterator it)
    {
        ArrayList list = new ArrayList();
        addAll(list, it);
        return list;
    }

    static public ArrayList enumerationToArrayList(Enumeration en)
    {
        ArrayList list = new ArrayList();
        addAll(list, en);
        return list;
    }

    static public ArrayList stringArrayToArrayList(String[] arr)
    {
        ArrayList list = new ArrayList();
        addStringArrayToArrayList(list, arr);
        return list;
    }

    // slow, should use is perfomance-critical areas
    static public ArrayList arrayToArrayList(Object arr)
    {
        if (arr == null)
            return null;
        int size = Array.getLength(arr);
        ArrayList list = new ArrayList(size);
        for (int i = 0; i < size; i++)
        {
            Object value = Array.get(arr, i);
            list.add(value);
        }
        return list;
    }

    static public void addAll(Collection list, Iterator it)
    {
        if (it == null)
            return;
        while (it.hasNext())
            list.add(it.next());
    }

    static public void addAll(Collection list, Enumeration en)
    {
        if (en == null)
            return;
        while (en.hasMoreElements())
            list.add(en.nextElement());
    }

    static public ArrayList addStringArrayToArrayList(ArrayList list, String[] arr)
    {
        if (arr != null && list != null)
            for (int i = 0; i < arr.length; i++)
                list.add(arr[i]);
        return list;
    }

    static public String[] collectionToStringArray(Collection c)
    {
        if (c == null)
            return null;
        int size = c.size();
        String[] strings = new String[size];
        Iterator it = c.iterator();
        int i = 0;
        while (it.hasNext())
        {
            strings[i] = it.next().toString();
            i++;
        }
        return strings;
    }

    // Return null if there is any problems
    static public Integer safeStringToInteger(String string)
    {
        if (string == null)
            return null;
        try
        {
            return Integer.valueOf(string);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    static public boolean safeEquals(Object s1, Object s2)
    {
        if (s1 == s2)
            return true; // includes null==null
        if (s1 == null || s2 == null)
            return false; // only one is null
        return s1.equals(s2);
    }

    static public boolean safeStringEqual_allEmptyAreEqual(String s1, String s2)
    {
        if (s1 == s2)
            return true; // includes null==null
        boolean s1e = MiscUtils.isEmpty(s1);
        boolean s2e = MiscUtils.isEmpty(s2);
        if (s1e && s2e)
            return true;
        if (s1 == null || s2 == null)
            return false;
        return s1.equals(s2);
    }

    // Works for nulls, Integers, Shorts and Longs. Does not work for really big
    // BigIntegers or floating point values
    static public boolean safeIntEquals(Number n1, Number n2)
    {
        if (n1 == n2)
            return true; // includes null==null
        if (n1 == null || n2 == null)
            return false; // only one is null
        return n1.longValue() == n2.longValue();
    }

    // Works for nulls, Integers, Shorts and Longs. Does not work for really big
    // BigIntegers or floating point values
    static public boolean isZero(Number n)
    {
        if (n == null)
            return false;
        return n.longValue() == 0L;
    }

    static SimpleDateFormat[] dateFormats = { new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd HH:mm"),
            new SimpleDateFormat("yyyy-MM-dd HH"), new SimpleDateFormat("yyyy-MM-dd"), new SimpleDateFormat("yyyy-MM"),
            new SimpleDateFormat("yyyy"), };
    // { dateFormats[5].setLenient(true); }
    static SimpleDateFormat roundUpDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static String[] roundUpStrings = { "", ".999", ":59.999", ":59:59.999", " 23:59:59.999", "", // The
                                                                                                 // endOfMonth
                                                                                                 // logic
                                                                                                 // is
                                                                                                 // hard-coded
            "-12-31 23:59:59.999", };

    static public Date smartFindDate(String dateString)
    {
        if (dateString == null || dateString.trim().length() == 0)
            return null;
        Date tempDate = null;
        for (int i = 0; i < dateFormats.length; i++)
        {
            try
            {
                tempDate = dateFormats[i].parse(dateString);
            }
            catch (ParseException e)
            {
            }
            if (tempDate != null) { return tempDate; }
        }
        return null;
    }

    static public Date smartFindDateRoundUp(String dateString)
    {
        if (dateString == null || dateString.trim().length() == 0)
            return null;
        Date tempDate = null;
        for (int i = 0; i < dateFormats.length; i++)
        {
            try
            {
                tempDate = dateFormats[i].parse(dateString);
            }
            catch (ParseException e)
            {
            }
            if (tempDate != null)
            {
                String newDateString = dateString + roundUpStrings[i];
                if (i == 5) // Special case: We must figure out what the last
                            // day of the month is.
                { return lastDayOfMonthCase(dateString); }
                try
                {
                    tempDate = roundUpDateFormat.parse(newDateString);
                }
                catch (ParseException e)
                {
                }
                return tempDate;
            }
        }
        return null;
    }

    static SimpleDateFormat dayPrefix = new SimpleDateFormat("yyyy-MM-dd");

    // input is left substring of "HH:mm:ss.SSS"
    static public Date todayAtTime(String timeString)
    {
        if (timeString == null || timeString.trim().length() == 0)
            return smartFindDate(dayPrefix.format(new Date()));
        return smartFindDate(dayPrefix.format(new Date()) + " " + timeString);
    }

    // This is the one hard-coded bit
    private static Date lastDayOfMonthCase(String dateString)
    {
        SimpleDateFormat baseFormat = new SimpleDateFormat("yyyy-MM");
        Date baseDate;
        try
        {
            baseDate = baseFormat.parse(dateString);
        }
        catch (ParseException e)
        {
            return null;
        }
        try
        {
            int year = Integer.parseInt(dateString.substring(0, 4));
            int month = Integer.parseInt(dateString.substring(5, dateString.length()));
            month++;
            Date newDate = roundUpDateFormat.parse("" + year + "-" + month + "-01 00:00:00.000");
            newDate = new Date(newDate.getTime() - 1);
            return newDate;
        }
        catch (Exception e)
        {
            return baseDate;
        }
    }

    static public void sortAndPrintMap(Map oldMap, OutputStream out)
    {
        TreeMap map = new TreeMap(oldMap);
        PrintStream myOut = new PrintStream(out);
        if (map == null)
            myOut.println("Map is null");
        // myOut.println("<Map class="+oldMap.getClass()+"
        // sortClass="+map.getClass()+" size="+map.size()+">");
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object value = map.get(key);
            myOut.println("  key=[" + key + "]>");
            myOut.println("  value=[" + value + "]\n");
        }
        myOut.flush();
    }

    static public void sortAndPrintMap(Map oldMap)
    {
        sortAndPrintMap(oldMap, System.out);
    }

    static public String sortAndPrintMapToString(Map oldMap)
    {
        TreeMap map = new TreeMap(oldMap);
        StringBuffer buf = new StringBuffer();
        if (map == null)
            buf.append("\nMap is null");
        // myOut.println("<Map class="+oldMap.getClass()+"
        // sortClass="+map.getClass()+" size="+map.size()+">");
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object value = map.get(key);
            buf.append("\n  key=[" + key + "]>");
            buf.append("\n  value=[" + value + "]\n");
        }
        return buf.toString();
    }

    static public void sortAndPrintMapNoFormatting(Map oldMap)
    {
        TreeMap map = new TreeMap(oldMap);
        if (map == null)
            System.out.println("Map is null");
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object value = map.get(key);
            System.out.println("" + value);
        }
    }

    static public String[] delimitedStringToStringArray(String text, String delimitChars)
    {
        ArrayList list = delimitedStringToArrayList(text, delimitChars);
        String[] arr = new String[list.size()];
        for (int i = 0; i < arr.length; i++)
        {
            arr[i] = (String) list.get(i);
        }
        return arr;
    }

    static public ArrayList delimitedStringToArrayList(String text, String delimitChars)
    {
        ArrayList list = new ArrayList();
        if (text == null)
            return list;
        StringTokenizer tokenizer = new StringTokenizer(text, delimitChars);
        while (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        return list;
    }

    static public ArrayList delimitedStringToArrayList_trimWhitespace(String text, String delimitChars)
    {
        ArrayList list = new ArrayList();
        if (text == null)
            return list;
        StringTokenizer tokenizer = new StringTokenizer(text, delimitChars);
        while (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken().trim();
            list.add(token);
        }
        return list;
    }

    static public String[] splitAtFirst(String source, char c)
    {
        int i = source.indexOf((int) c);
        if (i < 0)
            return new String[] { source };
        String firstPart = source.substring(0, i);
        String secondPart = source.substring(i + 1);
        return new String[] { firstPart, secondPart };
    }

    static public String[] splitAtLast(String source, char c)
    {
        int i = source.lastIndexOf((int) c);
        if (i < 0)
            return new String[] { source };
        String firstPart = source.substring(0, i);
        String secondPart = source.substring(i + 1);
        return new String[] { firstPart, secondPart };
    }

    static public String stringAfterLast(String source, char c)
    {
        int i = source.lastIndexOf((int) c);
        if (i < 0)
            return source;
        return source.substring(i + 1);
    }

    static public String[] splitOn(String text, char splitChar)
    {
        String delimitChars = Character.toString(splitChar);
        if (text == null)
            return new String[0];
        StringTokenizer tokenizer = new StringTokenizer(text, delimitChars);
        String[] tokens = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            tokens[i] = token.trim();
            i++;
        }
        return tokens;
    }

    static public String[] splitOn_includeEmptyStrings(String source, char c)
    {
        ArrayList subs = new ArrayList();
        for (int i = source.indexOf((int) c); i >= 0; i = source.indexOf((int) c))
        {
            String firstPart = source.substring(0, i);
            subs.add(firstPart);
            source = source.substring(i + 1);
        }
        subs.add(source);
        String[] asArray = new String[subs.size()];
        for (int i = 0; i < subs.size(); i++)
        {
            asArray[i] = (String) subs.get(i);
        }
        return asArray;
    }

    static public String substringBetween(String text, char c1, char c2)
    {
        System.out.println("   text:" + text);
        if (text == null)
            return null;
        int i1 = text.indexOf(c1);
        System.out.println("   i1:" + i1);
        if (i1 < 0)
            return null;
        int i2 = text.indexOf(c2, i1 + 1);
        System.out.println("   i2:" + i2);
        if (i2 < 0)
            return null;
        return text.substring(i1 + 1, i2);
    }

    static public boolean isEmpty(Object s)
    {
        return (s == null || s.toString().trim().length() == 0);
    }

    static public boolean isNotEmpty(String s)
    {
        return !(s == null || s.trim().length() == 0);
    }

    static public boolean isNotEmpty(Object o)
    {
        return !(o == null || ((o instanceof String) && ((String) o).trim().length() == 0));
    }

    static public boolean isEmpty(Collection col)
    {
        return (col == null || col.size() == 0);
    }

    static public boolean isNotEmpty(Collection col)
    {
        return !(col == null || col.size() == 0);
    }

    public static String integerToStringKeepNulls(Integer i)
    {
        if (i == null)
            return null;
        return i.toString();
    }

    public static Integer stringToIntegerKeepNulls(String s)
    {
        if (isEmpty(s))
            return null;
        return new Integer(s);
    }

    public static SimpleDateFormat dayOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String dayToString(Date day)
    {
        if (day == null)
            return null;
        return dayOnlyFormat.format(day);
    }

    public static Date stringToDay(String dateString) throws java.text.ParseException
    {
        if (MiscUtils.isEmpty(dateString))
            return null;
        return dayOnlyFormat.parse(dateString);
    }

    public static Date createdDayDate(int year, int month, int day)
    {
        try
        {
            return stringToDay("" + year + "-" + month + "-" + day);
        }
        catch (ParseException e)
        {
            // should never happen if input is sane
            e.printStackTrace();
            return null;
        }
    }

    public static boolean sameDay(Date d1, Date d2)
    {
        if (dayToString(d1).equals(dayToString(d2)))
            return true;
        return false;
    }
    
    public static Date today()  
    {
        Date now = new Date();
        
        try
        {
            return stringToDay(dayToString(now));
        }
        catch (ParseException e)
        {
            //Should never happen given no user input
            return null;
        }
        
    }
    

    public static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String dateToTimeStampString(Date date)
    {
        if (date == null)
            return null;
        return timestampFormat.format(date);
    }

    public static SimpleDateFormat timestampFormat_toMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String dateToTimeStampString_toMinute(Date date)
    {
        if (date == null)
            return null;
        return timestampFormat_toMinute.format(date);
    }

    public static String estCommentsTimeStamp(Date date)
    {
        if (date == null)
            return null;
        return timestampFormat_toMinute.format(date) + " EST";
    }

    public static String estCommmentsTimeStampNow()
    {
        return estCommentsTimeStamp(new Date());
    }

    public static SimpleDateFormat timestampFormat_toSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String dateToTimeStampString_toSecond(Date date)
    {
        if (date == null)
            return null;
        return timestampFormat_toSecond.format(date);
    }

    /*
     * 
     * Uses yyyy-MM-dd HH:mm:ss.SSS
     * 
     */
    public static Date stringToDate_strict(String dateString) throws java.text.ParseException
    {
        if (MiscUtils.isEmpty(dateString))
            return null;
        return timestampFormat.parse(dateString);
    }
    /*
     * public static Date safeStringToDate(String dateString)
     * 
     * {
     * 
     * if( MiscUtils.isEmpty(dateString) )
     * 
     * return null;
     * 
     * try
     * 
     * {
     * 
     * return
     * (Date)BeanReflector.defaultBeanReflector.converter.convert(Date.class,
     * dateString);
     * 
     * }
     * 
     * catch (Exception e)
     * 
     * {
     * 
     * e.printStackTrace();
     * 
     * return null;
     * 
     * }
     * 
     * }
     */

    public static String timeStampNow()
    {
        return dateToTimeStampString(new Date());
    }

    public static String timeStampNow_toSecond()
    {
        return dateToTimeStampString_toSecond(new Date());
    }

    public static java.sql.Date toSqlDate(java.util.Date date)
    {
        if (date == null)
            return null;
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Time toSqlTime(java.util.Date date)
    {
        if (date == null)
            return null;
        return new java.sql.Time(date.getTime());
    }

    /*
     * 
     * Only to be used when defaulting to local timezone
     * 
     */
    public static int secondsSinceMidnight(Date d)
    {
        return d.getSeconds() + d.getMinutes() * 60 + d.getHours() * 3600;
    }

    /*
     * 
     * Only to be used when defaulting to local timezone
     * 
     */
    public static boolean earlierInTheDay(Date d1, Date d2)
    {
        return secondsSinceMidnight(d1) < secondsSinceMidnight(d2);
    }

    /*
     * 
     * Only to be used when defaulting to local timezone
     * 
     */
    public static boolean earlierInTheDay_orEqual(Date d1, Date d2)
    {
        return secondsSinceMidnight(d1) <= secondsSinceMidnight(d2);
    }

    /*
     * 
     * Only to be used when defaulting to local timezone
     * 
     */
    public static boolean sameTimeOfDay(Date d1, Date d2)
    {
        return secondsSinceMidnight(d1) == secondsSinceMidnight(d2);
    }

    public static void updateInteger(RowSet rs, String column, Integer newValue) throws SQLException
    {
        if (newValue == null)
            rs.updateNull(column);
        else rs.updateInt(column, newValue.intValue());
    }

    public static void updateDouble(RowSet rs, String column, Double newValue) throws SQLException
    {
        if (newValue == null)
            rs.updateNull(column);
        else rs.updateDouble(column, newValue.doubleValue());
    }

    /*
     * 
     * Add years months and days to datesting in the format YYYY-MM-DD.
     * 
     * The result may look something like 2001-14-35. This can be converted into
     * 
     * a valid date via a SimpleDateFormat of "yyyy-MM-dd" format;
     * 
     * Validation should be done befor calling this function
     * 
     */
    public static String addToDateString(String oldDateString, int years, int months, int days) throws Exception
    {
        oldDateString = dayToString(stringToDay(oldDateString));
        String year = oldDateString.substring(0, 4);
        String month = oldDateString.substring(5, 7);
        String day = oldDateString.substring(8, 10);
        int newYear = Integer.parseInt(year) + years;
        int newMonth = Integer.parseInt(month) + months;
        int newDay = Integer.parseInt(day) + days;
        String newDateString = "" + newYear + "-" + newMonth + "-" + newDay;
        return newDateString;
    }

    /*
     * 
     * Add years months and days to datesting in the format YYYY-MM-DD.
     * 
     * Unlike addToDateString the result will not look like 2001-14-35.
     * 
     * This version should be used when the string will be shown to a user
     * 
     * befor it is converted to a Date object.
     * 
     * Validation should be done befor calling this function
     * 
     */
    public static String addToDateString_userView(String oldDateString, int years, int months, int days)
            throws Exception
    {
        return dayToString(stringToDay(addToDateString(oldDateString, years, months, days)));
    }

    public static String nullToEmpty(String s)
    {
        if (s == null)
            return "";
        return s;
    }

    public static Object nullObjectToEmptyString(Object o)
    {
        if (o == null)
            return "";
        return o;
    }

    public static String nullToNonBreaking(String s)
    {
        if (s == null)
            return "&nbsp;";
        return s;
    }

    public static Object nullToNonBreaking(Object o)
    {
        if (o == null)
            return "&nbsp;";
        return o;
    }

    public static String toString_keepNull(Object o)
    {
        if (o == null)
            return null;
        return o.toString();
    }

    public static String toString_null2empty(Object o)
    {
        if (o == null)
            return "";
        return o.toString();
    }

    // For user with LIKE '<value>' ESCAPE '!'
    public static String escapeSqlValueForLike(String value)
    {
        return value.replaceAll("'", "''").replaceAll("!", "!!").replaceAll("%", "!%").replaceAll("\\[", "!\\[")
                .replaceAll("\\]", "!\\]").replaceAll("_", "!_").replaceAll("\\*", "%").replaceAll("\\?", "_");
    }

    // For user with = '<value>'
    public static String escapeSqlValueForEqual(String value)
    {
        return value.replaceAll("'", "''");
    }

    /*
     * 
     * Strips out characters I don't approve of in filenames like spaces and
     * symbol characters
     * 
     * Takes a human readable name like "2009 - Sales Report for Client's in NA"
     * 
     * to "2009_Sales_Report_for Clients_in_NA"
     * 
     */
    public static String toFileName(String userInput)
    {
        char[] ui = userInput.toCharArray();
        StringBuffer res = new StringBuffer(ui.length);
        boolean lastWasSpace = false;
        boolean thisWasSpace = false;
        for (int i = 0; i < ui.length; i++)
        {
            thisWasSpace = false;
            char c = ui[i];
            boolean goodChar = false;
            if (c >= 'a' && c <= 'z')
            {
                goodChar = true;
            }
            else if (c >= 'A' && c <= 'Z')
            {
                goodChar = true;
            }
            else if (c >= '0' && c <= '9')
            {
                goodChar = true;
            }
            else if (c == '_' || c == ' ' || c == '\t')
            {
                c = ' ';
                thisWasSpace = true;
                if (lastWasSpace)
                    goodChar = false;
                else goodChar = true;
            }
            else
            {
            }
            if (goodChar)
                res.append(c);
            lastWasSpace = thisWasSpace;
        }
        return res.toString();
    }

    public static String padCropInteger(int val, int stringLength)
    {
        String returnVal = Integer.toString(val);
        if (returnVal.length() >= stringLength)
        {
            return returnVal.substring(returnVal.length() - stringLength, returnVal.length());
        }
        else
        {
            byte zero = '0';
            byte[] returnChars = new byte[stringLength];
            for (int i = 0; i < returnChars.length; i++)
                returnChars[i] = zero;
            byte[] valBytes = returnVal.getBytes();
            System.arraycopy(valBytes, 0, returnChars, stringLength - valBytes.length, valBytes.length);
            return new String(returnChars);
        }
    }

    public static String padLong_space(long val, int stringLength)
    {
        String returnVal = Long.toString(val);
        if (returnVal.length() >= stringLength)
        {
            return returnVal;
        }
        else
        {
            byte zero = ' ';
            byte[] returnChars = new byte[stringLength];
            for (int i = 0; i < returnChars.length; i++)
                returnChars[i] = zero;
            byte[] valBytes = returnVal.getBytes();
            System.arraycopy(valBytes, 0, returnChars, stringLength - valBytes.length, valBytes.length);
            return new String(returnChars);
        }
    }

    public static String padLong_zero(long val, int stringLength)
    {
        String returnVal = Long.toString(val);
        if (returnVal.length() >= stringLength)
        {
            return returnVal;
        }
        else
        {
            byte zero = '0';
            byte[] returnChars = new byte[stringLength];
            for (int i = 0; i < returnChars.length; i++)
                returnChars[i] = zero;
            byte[] valBytes = returnVal.getBytes();
            System.arraycopy(valBytes, 0, returnChars, stringLength - valBytes.length, valBytes.length);
            return new String(returnChars);
        }
    }

    public static String stripCharFromEnds(String source, char c)
    {
        if (source == null)
            return null;
        int start = 0;
        int end = source.length() - 1;
        boolean noChange = true;
        while (source.charAt(start) == c)
        {
            noChange = false;
            start++;
        }
        while (source.charAt(end) == c)
        {
            noChange = false;
            end--;
        }
        if (noChange)
            return source;
        if (start >= end)
            return "";
        return source.substring(start, end + 1);
    }

    // Not reliable. Must fix
    public static String replaceAll(String source, String replace, String with)
    {
        // System.out.println(" source:" +source );
        // System.out.println(" replace:" + replace);
        // System.out.println(" with:" +with );
        String result = source.replaceAll(replace, with);
        // System.out.println(" result:" + result);
        return result;
    }

    // Not optimized, do so if needed. Although minimal String/object creation,
    // so better than using String class
    public static void replaceAll(StringBuffer buf, String replace, String with)
    {
        if (replace == null || replace.length() == 0)
            return;
        int rlen = replace.length();
        int replaceOff;
        while ((replaceOff = buf.indexOf(replace)) >= 0)
        {
            buf.replace(replaceOff, replaceOff + rlen, with);
        }
    }

    public static String exception2String(Throwable e)
    {
        return printStackTrace_asString(e);
    }

    public static String printStackTrace_asString(Throwable e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static void whereAmI()
    {
        try
        {
            throw new Exception("Where am I?");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String whereAmI_string()
    {
        try
        {
            throw new Exception("Where am I?");
        }
        catch (Exception e)
        {
            return printStackTrace_asString(e);
        }
    }

    public static void stringToFile(String s, String fileName) throws IOException
    {
        stringToFile(s, fileName, true);
    }

    public static void stringToFile(String s, String fileName, boolean createPathIfNeeded) throws IOException
    {
        createPathForFile(fileName);
        FileWriter fw = new FileWriter(fileName);
        fw.write(s);
        fw.close();
    }

    public static void createPathForFile(String fileName) throws IOException
    {
        String[] pathFile = MiscUtils.splitAtLast(fileName, '/');
        createPath(pathFile[0]);
    }

    public static void createPath(String folderNameNoFinalSlash) throws IOException
    {
        System.out.println("path:" + folderNameNoFinalSlash);
        File path = new File(folderNameNoFinalSlash);
        if (!path.exists())
            path.mkdirs();
    }

    public static String fileToString(String fileName) throws IOException
    {
        FileReader fr = new FileReader(fileName);
        StringBuffer buf = new StringBuffer(20000);
        char[] subBuf = new char[5000];
        int ammountRead;
        while ((ammountRead = fr.read(subBuf)) != -1)
        {
            buf.append(subBuf, 0, ammountRead);
        }
        fr.close();
        return buf.toString();
    }

    private static String utf8 = "UTF-8";
    private static String iso1 = "ISO-8859-1";

    public static String iso1ToUtf8(String s) throws UnsupportedEncodingException
    {
        s = new String(s.getBytes(iso1), utf8);
        return s;
    }

    public static String iso1ToUtf8_hideException(String s)
    {
        try
        {
            s = new String(s.getBytes(iso1), utf8);
            return s;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return s;
        }
    }

    public static String utf8ToIso1(String s)
    {
        try
        {
            s = new String(s.getBytes(utf8), iso1);
            return s;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return s;
        }
    }

    /*
     * 
     * Seems insufficient to work 100% when the console is not 100% utf-8
     * supporting. And unnecessary when the console is.
     * 
     * 
     * 
     * Makes the output on a windows 7 console less mangled though.
     * 
     */
    public static void printUtf(String s)
    {
        try
        {
            PrintStream out = new PrintStream(System.out, true, "UTF-8");
            out.println(s);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            System.out.println(s);
        }
    }
    
    
    
    public static String formatLongPhoneNumber(Long rawPhone)
    {
        if(rawPhone==null || 0L==rawPhone)
            return "";
        
        
        try
        {
            String phoneString=rawPhone.toString();
            int len = phoneString.length();
            if(len<10)
            {
                return rawPhone.toString().replaceFirst("(\\d+)(\\d{4})", "$1-$2");
            }
            else if(len==10)
            {
                return rawPhone.toString().replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
            }
            else //if(len>10)
            {
                return rawPhone.toString().replaceFirst("(\\d+)(\\d{3})(\\d{3})(\\d{4})", "$1-($2) $3-$4");
            }
        }
        catch (Exception e)
        {
            return ""+rawPhone;
        }
    }
    
    

    /*public static void main(String[] args) throws Exception
    {
        String s = "FranÃ§ais";// Files is UTF-8 encoded, so this string is.
        System.out.println("s1:" + s);
        System.out.println("s2:" + iso1ToUtf8(s));
        System.out.println("s3:" + utf8ToIso1(s));
        System.out.println("s4:" + "testtesttest");
        printUtf("\u7686\u3055\u3093\u3001\u3053\u3093\u306b\u3061\u306f");
        printUtf("FranÃ§ais");
        printUtf("Ð¿Ñ€Ð¸Ð²ÐµÑ‚");
        
        Long phone = 4087370505L;

        phone = 945L;
        System.out.println("long phone: " + phone+"  formatted: "+formatLongPhoneNumber(phone));
        phone = 7891945L;
        System.out.println("long phone: " + phone+"  formatted: "+formatLongPhoneNumber(phone));
        phone = 4087370505L;
        System.out.println("long phone: " + phone+"  formatted: "+formatLongPhoneNumber(phone));
        phone = 33477930028L;
        System.out.println("long phone: " + phone+"  formatted: "+formatLongPhoneNumber(phone));
        phone = 4408007310658L;
        System.out.println("long phone: " + phone+"  formatted: "+formatLongPhoneNumber(phone));
        
        Date now = new Date();
        System.out.println("now:"+now);
        int year = MiscUtils.getYear(now);
        System.out.println("year:"+year);
        int month = MiscUtils.getMonth(now);
        System.out.println("month:"+month);
        int day = MiscUtils.getDayOfMonth(now);
        System.out.println(day);
        Date thisYearDec25 = MiscUtils.createdDayDate(year, 12, 25);
        System.out.println("thisYearDec25:"+thisYearDec25);
        Date thisFirstOfThisMonth = MiscUtils.createdDayDate(year, month, 1);
        System.out.println("thisFirstOfThisMonth:"+thisFirstOfThisMonth);
        Date twoYearsFromNow = MiscUtils.createdDayDate(year+2, month, day);
        System.out.println("twoYearsFromNow:"+twoYearsFromNow);
        
    }*/

    public static String streamToString(InputStream is) throws IOException
    {
        InputStreamReader r = new InputStreamReader(is);
        StringBuffer buf = new StringBuffer(20000);
        char[] subBuf = new char[5000];
        int ammountRead;
        while ((ammountRead = r.read(subBuf)) != -1)
        {
            buf.append(subBuf, 0, ammountRead);
        }
        r.close();
        return buf.toString();
    }

    /*
     * 
     * Steams everything from in to out until nothing is left
     * 
     * 
     * 
     * Closing and initialization is not done
     * 
     */
    public static void stream(InputStream in, OutputStream out) throws IOException
    {
        byte[] buf = new byte[10000];
        int ammountRead;
        while ((ammountRead = in.read(buf)) != -1)
        {
            out.write(buf, 0, ammountRead);
            // System.out.println("Just copied:["+new
            // String(buf,0,ammountRead)+"]");
        }
    }

    static public String getResourceAsString(String resName) throws Exception
    {
        ClassLoader cl = MiscUtils.class.getClassLoader();
        return getResourceAsString(resName, cl);
    }

    static public String getResourceAsString(String resName, ClassLoader cl) throws Exception
    {
        InputStream is = cl.getResourceAsStream(resName);
        if (is == null)
            throw new Exception("Could not load resource '" + resName + "'");
        return streamToString(is);
    }

    /*
     * 
     * Return the text between two substrings. In endpoint string do not exist
     * 
     * return null
     * 
     */
    public static String textBetween(String source, String fromString, String toString)
    {
        if (source == null)
            return null;
        int start = source.indexOf(fromString);
        if (start < 0)
            return null;
        start = start + fromString.length();
        int end = source.indexOf(toString, start);
        if (end < 0)
            return null;
        return source.substring(start, end);
    }

    /*
     * 
     * Maps null to a size of 0, otherwise return list.size();
     * 
     */
    public static int safeSize(AbstractList list)
    {
        if (list == null)
            return 0;
        return list.size();
    }

    public static int safeLen(String s)
    {
        if (s == null)
            return 0;
        return s.length();
    }

    // Fro integer values only (Long, Integer, Short)
    public static boolean zeroOrNull_int(Number n)
    {
        if (n == null)
            return true;
        return (n.intValue() == 0);
    }

    /*
     * 
     * -If the input is an ArrayList returns the input.
     * 
     * -If input is null return empty ArrayList
     * 
     * -If anything else, return a size one Arraylist with
     * 
     * input as the only element.
     * 
     * 
     * 
     */
    public static ArrayList objectToArrayList(Object input)
    {
        if (input == null)
            return new ArrayList(0);
        if (input instanceof ArrayList)
        {
            return (ArrayList) input;
        }
        else
        {
            ArrayList newList = new ArrayList(1);
            newList.add(input);
            return newList;
        }
    }

    /*
     * 
     * Returns a HashMap generated from a string array.
     * 
     * The string array must be of the form {key1,value1,key2,value2,...}
     * 
     */
    public static HashMap createMap(String[] keyValues)
    {
        if (keyValues == null || keyValues.length % 2 == 1)
            throw new RuntimeException("createMap requires an array with an even number of parameters.");
        HashMap newMap = new HashMap(keyValues.length * 3 + 5);
        for (int i = 0; i < keyValues.length; i += 2)
        {
            newMap.put(keyValues[i], keyValues[i + 1]);
        }
        return newMap;
    }

    public static boolean isTrue(Boolean b)
    {
        if (b == null)
            return false;
        return b.booleanValue();
    }

    public static String urlencode(String url)
    {
        if (url == null)
            return "";
        else if (url.trim().equals(""))
            return url;
        else return java.net.URLEncoder.encode(url.trim());
    }

    public static String stripAllWhiteSpace(String s)
    {
        char[] old = s.toCharArray();
        char[] temp = new char[old.length];
        int ti = 0;
        for (int i = 0; i < old.length; i++)
        {
            char oldChar = old[i];
            if (oldChar > ' ')
            {
                temp[ti] = oldChar;
                ti++;
            }
        }
        return new String(temp, 0, ti);
    }

    public static boolean containsAnyOf(String s, String anyChars)
    {
        char[] sa = s.toCharArray();
        char[] any = anyChars.toCharArray();
        int ti = 0;
        for (int i = 0; i < sa.length; i++)
        {
            for (int j = 0; j < any.length; j++)
            {
                if (sa[i] == any[j])
                    return true;
            }
        }
        return false;
    }

    static long lastTotalMem = 0;
    static long lastUsedMem = 0;
    static private boolean runNoteMemGG = false;
    static private boolean enableNoteMem = false;

    static public void noteMem(String tag)
    {
        if (!enableNoteMem)
            return;
        PrintWriter pw = new PrintWriter(System.out);
        noteMemUnconditional(tag, runNoteMemGG, pw);
        pw.flush();
    }

    static public void noteMemUnconditional(String tag, boolean runNMGG, PrintWriter out)
    {
        if (!runNMGG)
        {
            out.println("=\n\nAvailable memory at " + tag);
            out.println("    time:" + MiscUtils.timeStampNow());
            outMem(out);
            return;
        }
        out.println("\n\nAvailable memory (pre gc) at " + tag);
        out.println("    time:" + MiscUtils.timeStampNow());
        outMem(out);
        if (runNMGG)
            System.gc();
        out.println("\nAvailable memory (post gc) at " + tag);
        out.println("    time:" + MiscUtils.timeStampNow());
        outMem(out);
    }

    static private void outMem(PrintWriter out)
    {
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        long max = Runtime.getRuntime().maxMemory();
        long used = total - free;
        long deltaUsed = used - lastUsedMem;
        long deltaTotal = total - lastTotalMem;
        out.println("  " + padLong_space(used, 15) + " usedMemory");
        out.println("  " + padLong_space(free, 15) + " freeMemory");
        out.println("  " + padLong_space(total, 15) + " totalMemory");
        out.println("  " + padLong_space(max, 15) + " maxMemory");
        out.println("  " + padLong_space(deltaUsed, 15) + " deltaUsed");
        out.println("  " + padLong_space(deltaTotal, 15) + " deltaTotal");
        lastTotalMem = total;
        lastUsedMem = used;
        out.flush();
    }

    static public double percentTotalMemUsed()
    {
        long free_L = Runtime.getRuntime().freeMemory();
        long total_L = Runtime.getRuntime().totalMemory();
        long max_L = Runtime.getRuntime().maxMemory();
        long used_L = total_L - free_L;
        double used = (double) used_L;
        double max = max_L;
        return Math.round(10000 * used / max) / (double) 100.0;
    }

    static public double percentAllocatedMemUsed()
    {
        long free_L = Runtime.getRuntime().freeMemory();
        long total_L = Runtime.getRuntime().totalMemory();
        long max_L = Runtime.getRuntime().maxMemory();
        long used_L = total_L - free_L;
        double used = (double) used_L;
        double total = total_L;
        return Math.round(10000 * used / total) / (double) 100.0;
    }

    static public int memUsedInMegs()
    {
        long free_L = Runtime.getRuntime().freeMemory();
        long total_L = Runtime.getRuntime().totalMemory();
        long max_L = Runtime.getRuntime().maxMemory();
        long used_L = total_L - free_L;
        return (int) (used_L / 1024L / 1024L);
    }

    static public long getJvmUpTime()
    {
        long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
        return jvmUpTime;
    }

    static public Date getJvmStartTime()
    {
        long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(jvmStartTime);
    }
    /*
     * static public String getJvmInfo()
     * 
     * {
     * 
     * Object jvmBean = ManagementFactory.getRuntimeMXBean();
     * 
     * return Inspector.inspectBean_string(jvmBean);
     * 
     * }
     */

    private static ArrayList waste = new ArrayList();
    // Wastes appr bytesToWaste memory. Note precise at small values probably
    // wastes

    // at least 8 more bytes than claimed
    public static void wasteMem(int bytesToWaste)
    {
        noteMem("wasteMem.  About to waste " + bytesToWaste + " bytes of memory.");
        byte[] arr = new byte[bytesToWaste];
        waste.add(arr);
        noteMem("wasteMem.  Post wasted state.");
    }

    public static void infiniteWasteMem_geometric()
    {
        int bytesToWaste = 1;
        while (true)
        {
            wasteMem(bytesToWaste);
            bytesToWaste = bytesToWaste * 2;
        }
    }

    public static void infiniteWasteMem_linear()
    {
        int bytesToWaste = 50000000;
        while (true)
        {
            wasteMem(bytesToWaste);
        }
    }

    public static Iterator safeIterator(Collection col)
    {
        if (col != null)
            return col.iterator();
        return (new ArrayList(0)).iterator();
    }

    public static String safeCrop(String s, int maxlen)
    {
        if (s == null)
            return null;
        if (s.length() <= maxlen)
            return s;
        return s.substring(0, maxlen);
    }

    public static String safeCropAndToString(Object o, int maxlen)
    {
        if (o == null)
            return null;
        String s = o.toString();
        if (s.length() <= maxlen)
            return s;
        return s.substring(0, maxlen);
    }

    public static List asList(Object array)
    {
        if (array == null)
            return null;
        if (array instanceof Object[])
            return Arrays.asList((Object[]) array);
        else if (array instanceof int[])
        {
            int[] primArray = (int[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Integer(primArray[i]));
            return newList;
        }
        else if (array instanceof float[])
        {
            float[] primArray = (float[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Float(primArray[i]));
            return newList;
        }
        else if (array instanceof long[])
        {
            long[] primArray = (long[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Long(primArray[i]));
            return newList;
        }
        else if (array instanceof double[])
        {
            double[] primArray = (double[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Double(primArray[i]));
            return newList;
        }
        else if (array instanceof byte[])
        {
            byte[] primArray = (byte[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Byte(primArray[i]));
            return newList;
        }
        else if (array instanceof char[])
        {
            char[] primArray = (char[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Character(primArray[i]));
            return newList;
        }
        else if (array instanceof boolean[])
        {
            boolean[] primArray = (boolean[]) array;
            ArrayList newList = new ArrayList(primArray.length);
            for (int i = 0; i < primArray.length; i++)
                newList.add(new Boolean(primArray[i]));
            return newList;
        }
        else
        {
            System.out.println("Error, unsuppored array type:" + array.getClass());
            ArrayList newList = new ArrayList(1);
            newList.add(array);
            return newList;
        }
    }

    static public BigDecimal toBigDecimal(Object o)
    {
        if (o == null)
            return null;
        else if (o instanceof BigDecimal)
            return (BigDecimal) o;
        else if (o instanceof BigInteger)
            return new BigDecimal((BigInteger) o);
        else if (o instanceof Double)
            return new BigDecimal(((Double) o).doubleValue());
        else if (o instanceof Long)
            return new BigDecimal(((Long) o).longValue());
        else if (o instanceof Number)
            return new BigDecimal(((Number) o).doubleValue());
        else if (o instanceof String)
            return new BigDecimal((String) o);
        else
        {
            Exception e = new Exception("Error: Don't know how to convert " + o.getClass() + " to BigDecimal");
            e.printStackTrace();
            return null;
        }
    }

    static public String indentRows(CharSequence text, String indentStr)
    {
        if (text == null)
            return null;
        StringBuilder buf = new StringBuilder(1000 + text.length() * 2);
        buf.append(indentStr);
        int l = text.length();
        for (int i = 0; i < l; i++)
        {
            char c = text.charAt(i);
            buf.append(c);
            if (c == '\n')
                buf.append(indentStr);
        }
        return buf.toString();
    }

    public static String getCurrentMethod()
    {
        return getCurrentMethodNameFromThread(0);
    }

    public static String getCallingMethodName()
    {
        return getCurrentMethodNameFromThread(1);
    }

    public static String getCallingMethodName(int stackLevel)
    {
        return getCurrentMethodNameFromThread(stackLevel);
    }

    private static String getCurrentMethodNameFromThread(int stackLevel)
    {
        /*
         * 
         * 0 - dumpThreads
         * 
         * 1 - getStackTrace
         * 
         * 2 - thisMethod => getCurrentMethodNameFromThread
         * 
         * 3 - callingMethod => method calling thisMethod
         * 
         * 4 - method calling callingMethod
         * 
         */
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3 + stackLevel];
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        return className + "." + methodName;
    }

    public static String getCallingMethod(String thatStartsWith, String butDoesNotStartWith)
    {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement calling = null;
        for (StackTraceElement el : stack)
        {
            String cn = el.getClassName();
            if (cn.startsWith(thatStartsWith) && !cn.startsWith(butDoesNotStartWith))
            {
                calling = el;
                break;
            }
        }
        if (calling == null)
        {
            return "{no stack match}";
        }
        else
        {
            return "" + calling;
        }
    }

    public static String getCallingMethod(String thatStartsWith, String butDoesNotStartWith, int elseIndex)
    {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement calling = null;
        for (StackTraceElement el : stack)
        {
            String cn = el.getClassName();
            if (cn.startsWith(thatStartsWith) && !cn.startsWith(butDoesNotStartWith))
            {
                calling = el;
                break;
            }
        }
        if (calling == null)
        {
            if (stack.length > elseIndex)
            {
                calling = stack[elseIndex];
            }
            else
            {
                calling = stack[stack.length - 1];
            }
        }
        return "" + calling;
    }

    public static int charCount(String text, char... countChars)
    {
        int count = 0;
        char[] tChars = text.toCharArray();
        for (char t : tChars)
        {
            for (char c : countChars)
            {
                if (t == c)
                    count++;
            }
        }
        return count;
    }

    public static String stripChars(String text, char... countChars)
    {
        int count = 0;
        char[] tChars = text.toCharArray();
        char[] newChars = new char[tChars.length];
        int i = 0;
        for (char t : tChars)
        {
            boolean match = false;
            for (char c : countChars)
            {
                if (t == c)
                {
                    match = true;
                    break;
                }
            }
            if (match)
                continue;
            newChars[i] = t;
            i++;
        }
        return new String(newChars, 0, i);
    }
    
    

    public static String keepOnlyChars(String text, char... countChars)
    {
        int count = 0;
        char[] tChars = text.toCharArray();
        char[] newChars = new char[tChars.length];
        int i = 0;
        for (char t : tChars)
        {
            boolean match = false;
            for (char c : countChars)
            {
                if (t == c)
                {
                    match = true;
                    break;
                }
            }
            if (!match)
                continue;
            newChars[i] = t;
            i++;
        }
        return new String(newChars, 0, i);
    }

    public static String ipAddress()
    {
        try
        {
            InetAddress IP = InetAddress.getLocalHost();
            System.out.println("IP of my system is 1:= " + IP.getHostAddress());
            System.out.println("IP of my system is 2:= " + IP.getLocalHost());
            System.out.println("IP of my system is 3:= " + IP.getHostName());
            return "" + IP.getLocalHost();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            return "unknown";
        }
    }

    static public String endPart(String source, String divider)
    {
        int i = source.indexOf(divider);
        if (i < 0)
            return null;
        String lastPart = source.substring(i + divider.length());
        return lastPart;
    }

    static public String endPart_afterLastDiv(String source, String divider)
    {
        int i = source.lastIndexOf(divider);
        if (i < 0)
            return null;
        String lastPart = source.substring(i + divider.length());
        return lastPart;
    }

    public static String replaceAttributeInElement(String inHtml, String elementName, String attName,
            String replaceWith)
    {
        Pattern PATTERN_TABLE = Pattern.compile("<" + elementName + "(.*?)>(.*?)</" + elementName + ">",
                Pattern.DOTALL);
        Matcher matcher = PATTERN_TABLE.matcher(inHtml);
        StringBuffer newHtmlText = new StringBuffer();
        boolean hasChanged = false;
        while (matcher.find())
        {
            hasChanged = true;
            String attributesText = matcher.group(1);
            String bodyText = matcher.group(2);
            // System.out.println("\n attText3: \n\n\n"+attributesText);
            // System.out.println("\n bodyText3: \n\n\n"+bodyText);
            String newAttText = replaceAttribute(attributesText, attName, replaceWith);
            // System.out.println("\n newAttText4: \n\n\n"+newAttText);
            matcher.appendReplacement(newHtmlText, "<" + elementName + "" + newAttText + ">$2</" + elementName + ">");
        }
        if (hasChanged)
        {
            matcher.appendTail(newHtmlText);
            return newHtmlText.toString();
        }
        else
        {
            return inHtml;
        }
    }

    public static String replaceAttribute(String attributesText, String attName, String replaceWith)
    {
        replaceWith = " " + replaceWith;
        Pattern PATTERN_TABLE = Pattern.compile(" " + attName + "=\"(.*?)\"", Pattern.DOTALL);
        Matcher matcher = PATTERN_TABLE.matcher(attributesText);
        StringBuffer newText = new StringBuffer();
        boolean hasChanged = false;
        while (matcher.find())
        {
            hasChanged = true;
            String attText = matcher.group(1);
            // System.out.println("\n sumText: \n\n\n"+attText);
            matcher.appendReplacement(newText, replaceWith);
        }
        if (hasChanged)
        {
            matcher.appendTail(newText);
            return newText.toString();
        }
        else
        {
            return attributesText;
        }
    }
    
    
    public static int getYear(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }  
    
    public static int getMonth(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;  //Makes it 1 to 12 instead of 0 to 11.  Want ot mathc YYYY-MM-DD format
    }  
    
    public static int getDayOfMonth(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }  
    
    

    public static String springSafeEncoding_encode(String in)
    {
        int len = in.length();
        StringBuilder out = new StringBuilder(len * 5);
        for (int i = 0; i < len; i++)
        {
            char c = in.charAt(i);
            int oldAsc = (int) c;
            // out.append(oldAsc);

            if( oldAsc >= 65 && oldAsc <= 90 )
            {
                // uppercase letter
                out.append(c);
            }
            else if( oldAsc >= 97 && oldAsc <= 122 )
            {
                // lowercase letter
                out.append(c);
            }
            else if( oldAsc >= 48 && oldAsc <= 57 )
            {
                // numeric digit
                out.append(c);
            }
            else if( oldAsc == 95 )
            {
                // underscore, our escape character
                out.append("__");
            }
            else
            {
                // A letter to encode, since it will probably blow
                // up Spring picky PathVariable converter or even
                // its URL matching alg.
                out.append('_');
                out.append("" + oldAsc);
                out.append('_');
            }
        }

        return out.toString();

    }

    
    public static String springSafeEncoding_decode(String in)
    {
 
        char[] inChars = in.toCharArray();
        StringBuilder out = new StringBuilder( inChars.length );
        StringBuilder singleEnc = new StringBuilder( 4 );
        
        boolean isEnc=false;
        for(char c:inChars)
        {
            boolean wasEnc=isEnc;
            if(c=='_')
            {
                isEnc = !isEnc;
            }
            if( isEnc==false && wasEnc==false)
            {
                //Not between _ flag chars.  Not encoding, just keep the char
                out.append(c);
            }
            else if( isEnc==true && wasEnc==true)
            {
                //Another encoded char digit, add it to singleEnc for later int to char conversion
                singleEnc.append(c);
            }
            else if( isEnc==false && wasEnc==true)
            {
                try
                {
                    //Just hit the ending _ flag char, time to decode the ascii integer back to a char
                    if(singleEnc.length()==0)
                    {
                        out.append('_');
                    }
                    else
                    {
                        char d =  (char)Integer.parseInt(singleEnc.toString())  ;
                        out.append(d);
                    }
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    out.append("<Invalid Endoding Here>");
                }
            }
            else if( isEnc==true && wasEnc==false)
            {
                //Just hit the opening _ flag char, clean the digit buf
                singleEnc.setLength(0);
            }
        }
        
        return out.toString();
    }
    
    
    /*
     * Pads spaces on the right to deal with weird MDS char fields.
     */
    public static String charPad(String s, int length)
    {
        return charPad(s, length, ' ');
    }
    
    /*
     * Pads unicode #160 chars for HTML layout reasons
     */
    public static String charPadUnicode(String s, int length)
    {
        return charPad(s, length, '\u00A0');
    }
        
    /*
     * Pads spaces on the right with provided char
     */
    public static String charPad(String s, int length, char padChar)
    {
        char[] old;
        if(s!=null)
            old = s.toCharArray();
        else
            old = new char[0];
        int origLen=old.length;
        char[] newChars = new char[length];
        for(int i=0; i<length; i++)
        {
            if(i<origLen)
            {
                newChars[i]=old[i];
            }
            else
            {
                newChars[i]=padChar;
            }
        }
        String newStr=new String(newChars);
        return newStr;
    }
    

    
    /*
     * Pads spaces on the left with provided char.  Unlike charPadRight, this method doesn't crop.  Since it's mostly
     * used to pad zeros on numerics... if you have to crop, you have a problem elsewhere.
     */
    public static String charPadLeft(String s, int length, char padChar)
    {
        char[] old;
        if(s!=null)
            old = s.toCharArray();
        else
            old = new char[0];
        int origLen=old.length;
        
        int nCharsToPad = length - origLen;
        if(nCharsToPad<1)
            return s;

        char[] newChars = new char[length];

        for(int i=0; i<length; i++)
        {
            if(i>=nCharsToPad)
            {
                newChars[i]=old[i-nCharsToPad];
            }
            else
            {
                newChars[i]=padChar;
            }
        }
        String newStr=new String(newChars);
        return newStr;
    }
    
    /*
     * Pads zero on the left
     */
    public static String charPadZeros(String s, int length)
    {
        return charPadLeft(s, length, '0');
    }
      
    
    static public String millisecondsToEnglish(long msDelta)
    {        
        long rem=msDelta;
        boolean foundNonZero=false;
        
        long years = rem / (1000L * 60L * 60L * 24L * 365L);
        rem = rem % (1000L * 60L * 60L* 24L * 365L);

        long days = rem / (1000L * 60L * 60L * 24L);
        rem = rem % (1000L * 60L  * 60L * 24L);

        long hours = rem / (1000 * 60 * 60);
        rem = rem % (1000L * 60L * 60L);
        
        long minutes = rem / (1000L * 60L );
        rem = rem % (1000L * 60L);

        long seconds = rem / (1000L) ;
        rem = rem % (1000L);

        long ms = rem;
        StringBuilder sb = new StringBuilder(500);
        if(years>0L)
            foundNonZero=true;
        if(foundNonZero) sb.append(""+years+" years ");
        if(days>0L)
            foundNonZero=true;
        if(foundNonZero)sb.append(""+days+" days ");
        if(hours>0L)
            foundNonZero=true;
        if(foundNonZero)sb.append(""+hours+" hours ");
        if(minutes>0L)
            foundNonZero=true;
        if(foundNonZero)sb.append(""+minutes+" minutes ");
        if(seconds>0L)
            foundNonZero=true;
        if(foundNonZero)sb.append(""+seconds+" seconds ");
        sb.append(""+ms+" ms ");
        
        return sb.toString();
        
    }
    
    
    public static String delimitCollection(Collection c, String between)
    {
        StringBuffer sb  = new StringBuffer();
        boolean first=true;
        for(Object o: c)
        {
            if(!first)
                sb.append(between);                
            if(o!=null)
                sb.append(o.toString());
            first=false;
        }
        return sb.toString();
    }
    
    public static String delimitCollection(Collection c, String before, String after, String between)
    {
        StringBuffer sb  = new StringBuffer();
        boolean first=true;
        for(Object o: c)
        {
            if(!first)
                sb.append(between);                
            sb.append(before);
            if(o!=null)
                sb.append(o.toString());
            sb.append(after);
            first=false;
        }
        return sb.toString();
    }
    
 
    
    
    /*
     * Makes word text like:
     *   “Bob” is your uncle. l’élection
     * MDS Oracle Western db (WE8ISO8859P1) friendly:
     *   "Bob" is your uncle. l'élection
     *  
     */
    public static String makeOracleWesternSafe(String before)
    {
        return replaceSubstrings_strict(before   ,"‘","\'"   ,"’","\'"   ,"“","\""   ,"","\""   ,"”","\""   ,"…","..."   ,"℗","(P)"   ,"–","-"   ,"—","-");
    }
    
    
    
    public static Date nullifyOldDates(Date in)
    {
        if(in==null)
        {
            return null;
        }
        try
        {
            Date nullEquivalentDate = MiscUtils.stringToDay("1000-01-01");
            if( in.getTime() < nullEquivalentDate.getTime() )
            {
                return null;
            }
        } 
        catch (ParseException e)
        {
        }
        return in;
    }
    
    public static <A,B> TreeMap<A,B> keySortedMap(Map<A,B> inMap)
    {
        TreeMap<A,B> outMap = new TreeMap<A,B>(inMap);
        return outMap;
    }
    
    public static String keySortThenToString(Map inMap)
    {
        return toString( keySortedMap(inMap) );
    }
    
    
    public static String toString(Map inMap)
    {
        return toString(inMap,"<dl>","<dt>","</dt><dd>","</dd>\n","</dl?");
    }
    
    public static String toString(Map inMap,String listStart, String preKey, String postKey, String postValue, String listEnd)
    {
        StringBuilder sb = new StringBuilder(10000);
        TreeMap sortedMap = keySortedMap(inMap);
        sb.append(listStart);
        for(Object key:sortedMap.keySet())
        {
            Object value = sortedMap.get(key);
            sb.append(preKey);
            sb.append(key);
            sb.append(postKey);
            sb.append(value);
            sb.append(postValue);
        }
        sb.append(listEnd);
        return sb.toString();
    }    
    

    
    public static String largestClump(String source, String keepChars)
    {
        int largestClumpSize = 0;
        int largestClumpStart = -1;
        int currentClumpSize = 0;
        int currentClumpStart = -1;
        boolean nowInClump=false;
        
        char[] sourceArr = source.toCharArray();
        char[] keepCharsArr = keepChars.toCharArray();
        for(int i=0; i<sourceArr.length; i++)
        {
            char sc = sourceArr[i];
            
            boolean match = false;
            for (char kc : keepCharsArr)
            {
                if (sc == kc)
                {
                    match = true;
                    break;
                }
            }
            if(nowInClump)
            {
                if(match)
                {
                    currentClumpSize++;
                }
                else
                {
                    currentClumpStart= -1;
                    currentClumpSize= 0;
                }
            }
            else
            {
                if(match)
                {
                    currentClumpStart=i;
                    currentClumpSize=1;
                }
                else
                {
                    currentClumpStart= -1;
                    currentClumpSize= 0;
                }
            }
            nowInClump=match;
            if(nowInClump && currentClumpSize>largestClumpSize)
            {
                largestClumpSize=currentClumpSize;
                largestClumpStart=currentClumpStart;
            }
        }
        
        if(largestClumpSize<1)
            return "";
        return new String(sourceArr,largestClumpStart,largestClumpSize);
    }
    
    public static long extractPhoneNumber(String telephoneOrEmail)
    {
        String phoneStr = MiscUtils.largestClump(telephoneOrEmail,"1234567890 -()");
        if(MiscUtils.safeLen(phoneStr)>=7)
        {
            return MiscUtils.forceLong(phoneStr);
        }
        return 0;
    }
    
    public static long forceLong(String str)
    {
        str = MiscUtils.keepOnlyChars(str, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        if(str.length()>0)
            return Long.parseLong(str);
        return 0;
    }
    
    

    
    
    public static void main(String[] args) throws Exception
    {
        long t;
        t = System.currentTimeMillis();
        System.out.println( ""+t+": "+millisecondsToEnglish(t) );
        
        t=1000L * 60 * 60* 3;
        System.out.println( ""+t+": "+millisecondsToEnglish(t) );
        
        Date d1;
        Date d2;
        
        d1=MiscUtils.stringToDay("1945-01-01");
        d2=MiscUtils.stringToDay("1945-01-15");
        t=d2.getTime() - d1.getTime();
        System.out.println( ""+t+": "+millisecondsToEnglish(t) );
        
        d1=MiscUtils.stringToDay("1985-01-01");
        d2=MiscUtils.stringToDay("2000-01-01");
        t=d2.getTime() - d1.getTime();
        System.out.println( ""+t+": "+millisecondsToEnglish(t) );
        
        d1=MiscUtils.stringToDay("1945-01-01");
        d2=MiscUtils.stringToDay("2000-01-01");
        t=d2.getTime() - d1.getTime();
        System.out.println( ""+t+": "+millisecondsToEnglish(t) );
        
        ArrayList<Integer> longs = new ArrayList<Integer>();
        longs.add(3);
        longs.add(2);
        longs.add(1);
        System.out.println("\n\n Array:"+longs+" delimitCollection1: "+delimitCollection(longs,"<BR>\n>") );
        System.out.println("\n\n Array:"+longs+" delimitCollection2: "+delimitCollection(longs,"\n-","<Integer>","<BR>") );
        System.out.println("\n\n Array:"+longs+" delimitCollection2: "+delimitCollection(longs,"<P>","</P>","\n") );
        
        String wordStuff = "“Bob” is your uncle. l’élection © ℗ … …. A…  ";
        System.out.println("\n\n Word:"+wordStuff );
        System.out.println("\n\n makeOracleWesternSafe:"+makeOracleWesternSafe(wordStuff) );
        
        
        String clump="ABC123ABCD4";
        String keepers;
        
        keepers="ABCDE";
        System.out.println("\n Clumps for '"+clump+"' and '"+keepers+"' are '"+MiscUtils.largestClump(clump, keepers)+"'.");
        keepers="1234567890";
        System.out.println("\n Clumps for '"+clump+"' and '"+keepers+"' are '"+MiscUtils.largestClump(clump, keepers)+"'.");
        keepers="AB";
        System.out.println("\n Clumps for '"+clump+"' and '"+keepers+"' are '"+MiscUtils.largestClump(clump, keepers)+"'.");
        keepers="4";
        System.out.println("\n Clumps for '"+clump+"' and '"+keepers+"' are '"+MiscUtils.largestClump(clump, keepers)+"'.");
        
        String phonePlus;
        
        phonePlus="My phone is 1-800-555-1111 ext 6. ";
        System.out.println("\n Find phone in '"+phonePlus+"= '"+MiscUtils.extractPhoneNumber(phonePlus)+"'.");

        phonePlus="613-555-1111, pmurphy444@gmail.com";
        System.out.println("\n Find phone in '"+phonePlus+"= '"+MiscUtils.extractPhoneNumber(phonePlus)+"'.");

        phonePlus="1-(703) 23 555-666x123";
        System.out.println("\n Find phone in '"+phonePlus+"= '"+MiscUtils.extractPhoneNumber(phonePlus)+"'.");
        
        System.out.println("\n t1:"+ "551112223333".replaceFirst("(\\d+)(\\d{3})(\\d{3})(\\d{4})", "$1-($2) $3-$4"));
        System.out.println("\n t2:"+ "551112223333".replaceFirst("(\\d+)(\\d{3})(\\d{3})(\\d{4})", "$1"));
        System.out.println("\n t3:"+ "551112223333".replaceFirst("222(\\d{3})", "$1"));
        
        System.out.println("\n x1:"+extractPattern("Testing123Testing", "^([a-zA-Z]+)([0-9]+)(.*)", 2));
        System.out.println("\n x2:"+extractPattern("Testing123Testing", "^([a-zA-Z]+)([0-9]+)(.*)", 92));
        System.out.println("\n x3:"+extractPattern("Testing123Testing", "([0-9]+)", 1));
        System.out.println("\n x4:"+extractPattern("Testing123Testing", "g([0-9]+)", 1));
        System.out.println("\n x5:"+extractPattern("Testing123Testing", "Testing([0-9]+)", 1));
        System.out.println("\n x6:"+extractPattern("Testing123Testing", "Teszzzting([0-9]+)", 1));

        System.out.println("\n Extension1:"+extractPhoneExtension("111-222-444 X77 , re@bob.com"));
        System.out.println("\n Extension2:"+extractPhoneExtension("111-222-444 x 77 , re@bob.com"));
        System.out.println("\n Extension3:"+extractPhoneExtension("111-222-444 X....  77 , re@bob.com"));
        System.out.println("\n Extension4:"+extractPhoneExtension("111-222-444 ext.77 , re@bob.com"));
        System.out.println("\n Extension5:"+extractPhoneExtension("111-222-444 Ext77 , re@bob.com"));
        System.out.println("\n Extension6:"+extractPhoneExtension("111-222-444 EXT  77 , re@bob.com"));
        System.out.println("\n Extension7:"+extractPhoneExtension("111-222-444  ExTension 77 , re@bob.com"));
        System.out.println("\n Extension8:"+extractPhoneExtension("111-222-444  Extension77 , re@bob.com"));
        System.out.println("\n Extension9:"+extractPhoneExtension("111-222-444  Extension=77 , re@bob.com"));
        System.out.println("\n Extension10:"+extractPhoneExtension("111-222-444  Extension: 77 , re@bob.com"));
        
        
        System.out.println("\n Email extract1:"+extractEmail("111-222-444  Extension: 77 , re@bob.com"));
        System.out.println("\n Email extract2:"+extractEmail("111-222-444  Extension: 77 , re@bob.com   fdsa"));
        System.out.println("\n Email extract3:"+extractEmail("111-222-444  Extension: 77 , re@bob.com:   fdsa"));
        System.out.println("\n Email extract4:"+extractEmail("111-222-444  Extension: 77 , (pmurphy444_test@yahoo.nz) "));

        System.out.println("\n Email postal1:"+extractPostal("111-222-444  Extension: 77 , K1N 1B7 "));
        System.out.println("\n Email postal2:"+extractPostal("111-222-444  Extension: 77 , k1n 1b7 "));
        System.out.println("\n Email postal3:"+extractPostal("111-222-444  Extension: 77 , K1N1B7 "));
        System.out.println("\n Email postal4:"+extractPostal("111-222-444  Extension: 77 , k1n1b7 "));
        System.out.println("\n Email postal5:"+extractPostal("111- k 1 n 1v1  Extension: 77 , k1n1b7 "));
        System.out.println("\n Email postal6:"+extractPostal("111- k 1 n 1v1  Extension:fdsa "));
        System.out.println("\n Email postal5:"+extractPostal(""));
        System.out.println("\n Email postal5:"+extractPostal(null));
        
        testSubstrings();


    }    
    

    
    public static String extractPattern(String source, String regexPattern, int groupNumber)
    {
        Pattern p = Pattern.compile(regexPattern);
        return extractPattern(source, p, groupNumber);
    }    
    
    
    public static String extractPattern(String source, Pattern p, int groupNumber) 
    {
        // create matcher for pattern p and given string
        Matcher m = p.matcher(source);

        // if an occurrence if a pattern was found in a given string...
        if (m.find()) 
        {
            try
            {
                return m.group(groupNumber);
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
      
    }
    
    
    public static final Pattern[] extPatterns =  {Pattern.compile("x([\\ \\.\\-\\:\\=]*)([0-9]+)"),Pattern.compile("ext([\\ \\.\\-\\:\\=]*)([0-9]+)"),Pattern.compile("extension([\\ \\.\\-\\:\\=]*)([0-9]+)") };
    
    public static long extractPhoneExtension(String source)
    {
        source = source.toLowerCase();
        
        String str = null;
        for(Pattern p: extPatterns)
        {            
            str =  extractPattern(source, p, 2);
            if(MiscUtils.safeLen(str)>0)
            {
                return Long.parseLong(str);
            }
        }
                
        return 0;
    }
       
    
    public static final Pattern emailPattern =  Pattern.compile("[A-Za-z0-9\\.\\_\\%\\+\\-]+\\@[A-Za-z0-9\\.\\-]+\\.[A-Za-z]+");

    public static String extractEmail(String source)
    {
        source = source.toLowerCase();
        return extractPattern(source, emailPattern, 0);
    }
    
    
    public static final Pattern postalPattern =  Pattern.compile("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ] ?[0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]");

    public static String extractPostal(String source)
    {
        if(source==null) return null;
        source = source.toUpperCase();
        String postal = extractPattern(source, postalPattern, 0);
        if( MiscUtils.safeLen(postal) ==6 )
        {
            postal = postal.substring(0,3)+" "+ postal.substring(3,6);
        }
        return postal;
    }
    
    
    
    /* 
     * Replaces substring values.  replacePairs is an array of substring pairs.  The second string may be null to indicate that string should be stripped.
     * 
     * Optimized to be efficient, especially when most input are single character replacements.
     * 
     * Don't use if it is important to not replace previously replaces values.
     */
    public static String replaceSubstrings(String source, String... replacePairs)
    {
        if(replacePairs==null || replacePairs.length == 0)
        {
            return source;
        }
        
        if(replacePairs.length%2 != 0)
        {
            throw new RuntimeException("replacePairs must be pairs of values. The odd size of "+replacePairs.length+" is invalid.");
        }
        int len=replacePairs.length;
        Character[] charPairs =new Character[len];
        int charPairInx=0;
        
        for(int i = 0; i<replacePairs.length; i=i+2)
        {
            String from=replacePairs[i];
            String to = replacePairs[i+1];
            int fromLen = from.length();
            int toLen = MiscUtils.safeLen(to);
            if(fromLen==1 && toLen==1)
            {
                charPairs[charPairInx] = from.charAt(0);
                charPairInx++;
                charPairs[charPairInx] = to.charAt(0);
                charPairInx++;
            }
            else if(fromLen==1 && toLen==0)
            {
                charPairs[charPairInx] = from.charAt(0);
                charPairInx++;
                charPairs[charPairInx] = null;
                charPairInx++;
            }
            else
            {
                //Size 2 or greater String values, just use string replace
                source = source.replace(from, to);
            }
        }
        
        Character[] usedCharPairs =new Character[charPairInx];
        System.arraycopy(charPairs, 0, usedCharPairs, 0, usedCharPairs.length); 
        return replaceChars(source, usedCharPairs);
    }
    
    /* 
     * Replaces char values.  replacePairs is an array of char pairs.  The second char may be null to indicate that char should be stripped.
     */
    public static String replaceChars(String source, Character... replacePairs)
    {
        if(replacePairs==null || replacePairs.length == 0)
        {
            return source;
        }
        
        if(replacePairs.length%2 != 0)
        {
            throw new RuntimeException("replaceChars must be pairs of char values. The odd size of "+replacePairs.length+" is invalid.");
        }
        int len=replacePairs.length;
        int numberOfPairs= len / 2;
        
        
        char[] sourceChars = source.toCharArray();
        char[] destChars = new char[sourceChars.length];
        int destInx = 0;
        for (char sc : sourceChars)
        {
            char destChar = sc;
            boolean strip = false;
            for(int i = 0; i<replacePairs.length; i=i+2)
            {
                Character from = replacePairs[i];
                Character to = replacePairs[i+1];
                if (sc == from)
                {
                    if(to==null)
                    {
                        strip=true;
                    }
                    else
                    {
                        destChar=to;
                    }
                    break;
                }
            }
            if(strip)
                continue;
            destChars[destInx] = destChar;
            destInx++;
        }
        return new String(destChars, 0, destInx);
        
        
        
        
    }
    
    /*
     * Version to be used when it is important not to replace previously replaced values
     */
    public static String replaceSubstrings_strict(String source, String... replacePairs)
    {
        if(replacePairs==null || replacePairs.length == 0)
        {
            return source;
        }
        
        if(replacePairs.length%2 != 0)
        {
            throw new RuntimeException("replaceChars must be pairs of char values. The odd size of "+replacePairs.length+" is invalid.");
        }
        int len=replacePairs.length;
        int numberOfPairs= len / 2;
        
        char[][] fromStrings = new char[numberOfPairs][];
        char[][] toStrings = new char[numberOfPairs][];
        int charPairInx=0;
        StringBuilder sb = new StringBuilder(source.length()*2+20);
        
        for(int i = 0; i<replacePairs.length; i=i+2)
        {
            String from=replacePairs[i];
            String to = replacePairs[i+1];
            if( MiscUtils.isEmpty(from) )
                continue;
            fromStrings[charPairInx] = stringToCharArray(from);
            toStrings[charPairInx] = stringToCharArray(to);
            charPairInx++;            
        }
        int numberOfValidPairs= charPairInx;
        
        char[] sourceChars = source.toCharArray();
        for(int scIdx=0; scIdx<sourceChars.length; scIdx++)
        {
            boolean replacedMatch=false;
            for(int i=0; i<numberOfValidPairs; i++)
            {
                java.util.Arrays.equals(sourceChars, sourceChars);
                char[] fromString = fromStrings[i];
                if( charArrayEquals(sourceChars, scIdx, fromString) )
                {
                    replacedMatch=true;
                    sb.append(toStrings[i]);
                    scIdx=scIdx+fromString.length - 1;
                    break;
                }
            }
            if(!replacedMatch)
            {
                sb.append(sourceChars[scIdx]);
            }
        }
        return sb.toString();
    }
    
    public static char[] stringToCharArray(String s)
    {
        if(s==null)
            return new char[0];
        return s.toCharArray();
    }
    
    public static boolean charArrayEquals(char[] sourceChars, int sourceOffset, char[] substring)
    {
        if(substring== null ||sourceChars==null)
            return false;
        int sLen = sourceChars.length;
        int subLen = substring.length;
        for(int i=0; i<subLen; i++)
        {
            //If we ran out of source
            if(i+sourceOffset >= sLen)
            {
                return false;
            }
            if(sourceChars[i+sourceOffset] != substring[i])
            {
                return false;
            }
        }
        return true;
    }
     
     
    
    public static void testSubstrings() throws Exception
    {
        String in;
        String out;
        
        
        in="This is a test.";
        out = MiscUtils.replaceChars(in, 'i','I','t','T');
        System.out.println("replaceChars Test 1:  in="+in+" out="+out);

        in="This is a test.";
        out = MiscUtils.replaceChars(in, 'i',null,'t','T');
        System.out.println("replaceChars Test 2:  in="+in+" out="+out);
        
        in="“Bob” is your uncle. l’élection © ℗ … …. A…";
        out = MiscUtils.replaceChars(in, '“','"',  '”','"',  '’', '\'',  '©', null,  '℗', 'P');
        System.out.println("replaceChars Test 3:  in="+in+" out="+out);
        
        //Independance test will pass
        in="This is a test.";
        out = MiscUtils.replaceChars(in  ,'i','e'  ,'e','i');
        System.out.println("replaceChars Test 4:  in="+in+" out="+out);
        
        in="This is a test.";
        out = MiscUtils.replaceChars(in  ,'e','i'  ,'i','e');
        System.out.println("replaceChars Test 5:  in="+in+" out="+out);
        

        
        in="This is a test.";
        out = MiscUtils.replaceSubstrings(in, "i","ii"  ,"t","T");
        System.out.println("replaceSubstrings Test 1:  in="+in+" out="+out);

        in="This is a test.";
        out = MiscUtils.replaceSubstrings(in, "i","ii"  ,"t","T"  ,"e",null  ,"a","A");
        System.out.println("replaceSubstrings Test 2:  in="+in+" out="+out);

        in="“Bob” is your uncle. l’élection © ℗ … …. A…";
        out = MiscUtils.replaceSubstrings(in,  "“","\"",  "”","\"",  "’","'",  "©",null,  "℗",null,  "…","...");
        System.out.println("replaceSubstrings Test 3:  in="+in+" out="+out);
        
        //These overlapping strings could be a problem, if we don't want the to strings replaced themselves...
        //Independance test will fail
        in="TThhiiss iiss aa tteesstt.";
        out = MiscUtils.replaceSubstrings(in, "ii","ee"  ,"ee","ii");
        System.out.println("replaceSubstrings Test 4:  in="+in+" out="+out);

        in="TThhiiss iiss aa tteesstt.";
        out = MiscUtils.replaceSubstrings(in, "ee","ii"  ,"ii","ee");
        System.out.println("replaceSubstrings Test 5:  in="+in+" out="+out);
        
        in="This is a test.";
        out = MiscUtils.replaceSubstrings_strict(in, "i","ii"  ,"t","T");
        System.out.println("replaceSubstrings_strict Test 1:  in="+in+" out="+out);

        in="This is a test.";
        out = MiscUtils.replaceSubstrings_strict(in, " is "," is not "  ,"test","purple people eater");
        System.out.println("replaceSubstrings_strict Test 2:  in="+in+" out="+out);

        in="This is a test.";
        out = MiscUtils.replaceSubstrings_strict(in, "This","These"   ,"is","are"  ,"test.","cat.");
        System.out.println("replaceSubstrings_strict Test 3:  in="+in+" out="+out);

        //Independance test will pass
        in="TThhiiss iiss aa tteesstt.";
        out = MiscUtils.replaceSubstrings_strict(in, "ii","ee"  ,"ee","ii");
        System.out.println("replaceSubstrings_strict Test 4:  in="+in+" out="+out);

        in="TThhiiss iiss aa tteesstt.";
        out = MiscUtils.replaceSubstrings_strict(in, "ee","ii"  ,"ii","ee");
        System.out.println("replaceSubstrings_strict Test 5:  in="+in+" out="+out);

        in="aba";
        out = MiscUtils.replaceSubstrings_strict(in, "ab","abba"  ,"a","c");
        System.out.println("replaceSubstrings_strict Test 6:  in="+in+" out="+out);

        in="aba";
        out = MiscUtils.replaceSubstrings_strict(in  ,"a","c", "ab","abba");
        System.out.println("replaceSubstrings_strict Test 7:  in="+in+" out="+out);

    }
    

    

     
    
}
