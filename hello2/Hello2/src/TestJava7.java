import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import enc.misc.PrintStreamStore;
 
public class TestJava7
{
    
    public static PrintStream out = System.out;
    
    
    
    public static void stringSwitch()
    {
        String name = "bob";
        out.println("name=" + name);
 
        switch (name)
        {
 
        case "bob":
 
            out.println("Bob Johnson?");
 
            break;
 
        default:
            out.println("Who?");
 
            break;
 
        }
    }
    
    public static void  multiCatch(String s)
    {
        try
        {
            //String s=null;
            if(s==null)
                throw new NoSuchMethodException("Bad bad bad");
            if(s!=null)
                throw new NumberFormatException("Bad number");
        }
        catch (NoSuchMethodException|NumberFormatException e)
        {
           out.println("Test error:"+e);
        }
    }
    
    
    public static void echoFileWithNewResourceHandling(String fileName)
    {
        try (FileInputStream fin = new FileInputStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            )
        {
            while (br.ready())
            {
                String line1 = br.readLine();
                out.println(line1);
            }
        }
        catch (FileNotFoundException ex)
        {
            out.println("File '"+fileName+"' is not found");
        }
        catch (IOException ex)
        {
            out.println("Can't read the file");
        }
    }
    
    
    public static void printPathInfo(String pathName)
    {

        Path path = Paths.get(pathName);

        out.println("Number of Nodes: " + path.getNameCount());

        out.println("File Name: " + path.getFileName());
        out.println("Default toString: " + path);

        out.println("File Root: " + path.getRoot());

        out.println("File Parent: " + path.getParent());


    }
    
    
    

    public static void test()
    {
        stringSwitch();
        
        HashMap<String,Integer> h1 = new HashMap<>();
        Map<String,Integer> h2 = new HashMap<>();
        Map<Integer,List<String>> h3 = new HashMap<>();
        
        multiCatch(null);
        multiCatch("ggg");
        
       // echoFileWithNewResourceHandling("/home/hcuser/pats/sql/mds_internal.sql");
       // echoFileWithNewResourceHandling("C:/not_a_real_file.ini");
        
        Map<Long,String> testHash = new LinkedHashMap<>();
        testHash.put(1L, "One");
        testHash.put(2L, "Two");
        out.println("testHash:"+testHash);
        
        int oneMillion = 1_000_000;
        out.println("oneMillion:"+oneMillion);
        
        printPathInfo("c:/Windows/System32");
        
        
        // Formatting strings
        out.printf("%1$s, %2$s,  and  %3$s %n", "ABC", "DEF", "XYZ");
        out.printf("%3$s, %2$s,  and  %1$s %n", "ABC", "DEF", "XYZ");

        // Formatting numbers
        out.printf("%1$4d, %2$4d, %3$4d %n", 1, 10, 100);
        out.printf("%1$4d, %2$4d, %3$4d %n", 10, 100, 1000);
        out.printf("%1$-4d, %2$-4d,  %3$-4d %n", 1, 10, 100);
        out.printf("%1$-4d, %2$-4d,  %3$-4d %n", 10, 100, 1000);

        // Formatting date and time
        Date dt = new Date();
        out.printf("Today is  %tD  %n", dt);
        out.printf("Today is  %tF  %n", dt);
        out.printf("Today is  %tc  %n", dt);
        
        
        StringBuilder sb = new StringBuilder();
        // Send all output to the Appendable object sb
        Formatter formatter = new Formatter(sb, Locale.US);

        // Explicit argument indices may be used to re-order output.
        formatter.format("%4$2s %3$2s %2$2s %1$2s", "a", "b", "c", "d");
        sb.append("\n\n");
        // -> " d  c  b  a"

        // Optional locale as the first argument can be used to get
        // locale-specific formatting of numbers.  The precision and width can be
        // given to round and align the value.
        formatter.format(Locale.FRANCE, "e = %+10.4f", Math.E);
        sb.append("\n\n");
        // -> "e =    +2,7183"

        // The '(' numeric flag may be used to format negative numbers with
        // parentheses rather than a minus sign.  Group separators are
        // automatically inserted.
        formatter.format("Amount gained or lost since last statement: $ %(,.2f",  123.456f);
        sb.append("\n\n");
        // -> "Amount gained or lost since last statement: $ (6,217.58)"
        out.println("sb:"+sb);
        
        // Writes a formatted string to out.
        out.format("Local time: %tT \n", Calendar.getInstance());
        out.format("Local time2: %tT \n", dt);
        out.format("Today is  %tF  %n \n",dt);
        
        Calendar c = new GregorianCalendar(1995, 0, 23);
        String s = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);
        out.println("s:"+s);
        out.format("Duke's Birthday2:  %tF  %n \n",c);
    }

    public static void main(String[] args)
    {
        PrintStreamStore psStore = new PrintStreamStore();
        out = psStore.getPrintStream();
        test();
        out = System.out;
        System.out.println("String test result:"+psStore.extractString());
        System.out.println("\n\n*******************\n");
        test();
    }
 
}
 
 
