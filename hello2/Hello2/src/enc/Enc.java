package enc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import enc.filter.FileFilter;
import enc.filter.Filter;
import enc.filter.LazyEncFilter;
import enc.misc.MiscUtils;

public class Enc
{
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
            System.out.println("Just copied:[" + new String(buf, 0, ammountRead) + "]");
        }
    }

    public static void copy(String fileNameFrom, String fileNameTo)
    {
        File fileFrom = new File(fileNameFrom);
        File fileTo = new File(fileNameTo);
        try (InputStream in = new FileInputStream(fileFrom); OutputStream out = new FileOutputStream(fileTo);)
        {
            stream(in, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String... args) throws Exception
    {
        for (String arg : args)
        {
            System.out.println("arg:" + arg);
            String[] nameAndValue = MiscUtils.splitAtFirst(arg, ':');
            String paramName = nameAndValue[0];
            String paramValue = null;
            if (nameAndValue.length > 1)
                paramValue = nameAndValue[1];
            System.out.println("paramName" + paramName);
            System.out.println("paramValue:" + paramValue);
        }
        // copy("C:/00data/0test/MiscUtils.java","C:/00data/0test/test1.out.txt");
        Filter testFilter = new LazyEncFilter(new FileFilter(new File("/home/hcuser/pats/oldcode/sample_cdn.dat")));
        testFilter.setOut(new FileOutputStream(new File("/home/hcuser/pats/oldcode/sample_cdn.zip")));
        testFilter.stream();
    }
}
