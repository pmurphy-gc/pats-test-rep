import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import enc.misc.PrintStreamStore;

public class TestJava8
{
    public static PrintStream out = System.out;
    

    
    
    public static void streamTest()
    {

        List<Integer> myList = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            myList.add(i);

        // sequential stream
        Stream<Integer> sequentialStream = myList.stream();

        // parallel stream
        Stream<Integer> parallelStream = myList.parallelStream();

        // using lambda with Stream API, filter example
        Stream<Integer> highNums = parallelStream.filter(p -> p > 90);
        // using lambda in forEach
        highNums.forEach(p -> out.println("High Nums parallel=" + p));

        Stream<Integer> highNumsSeq = sequentialStream.filter(p -> p > 90);
        highNumsSeq.forEach(p -> out.println("High Nums sequential=" + p));

    }
    
    
    static int remainderContinious(int dividend, int divisor)
    {
        divisor=Math.abs(divisor);
        if(dividend>=0)
        {
            return dividend % divisor;
        }
        else
        {
            //out.println("\n\ndividend:"+dividend);
            //out.println("Math.abs(dividend):"+Math.abs(dividend));
            //out.println("divisor:"+divisor);
            //out.println("f:"+(Math.abs(dividend) % divisor));
            int r = dividend % divisor;
            if(r!=0)
                r= divisor + r;
            return r;
        }
    }
    
    static void testRemainder()
    {
        for(int i = -20; i<21; i++)
        {
            int ru = Integer.remainderUnsigned(i, 3);
            int rs = i % 3;
            out.println("i="+i+"  ru="+ru+"  rs="+rs+" mr="+remainderContinious(i,3) +" mrn="+remainderContinious(i,-3));
        }        
    }
    
    
    static public void test()
    {
        //int a1 = 0xFFFF_FFFF;
        int a1 = 0b01111111_11111111_11111111_11111111;
        out.println("a1        to string:"+a1);
        out.println("a1        MAX_VALUE:"+Integer.MAX_VALUE);
        out.println("a1 parseUnsignedInt:"+Integer.toUnsignedString(a1));
        out.println("a1      toHexString:"+Integer.toHexString(a1));
        out.println("a1   toBinaryString:"+Integer.toBinaryString(a1));
        
        a1 = 0b11111111111111111111111111111111;
        out.println("a1        to string:"+a1);
        out.println("a1        MAX_VALUE:"+Integer.MAX_VALUE);
        out.println("a1        MAX_VALUE:"+Integer.MAX_VALUE);
        out.println("a1 parseUnsignedInt:"+Integer.toUnsignedString(a1));
        out.println("a1      toHexString:"+Integer.toHexString(a1));
        out.println("a1   toBinaryString:"+Integer.toBinaryString(a1));
        
        long b1 = 0b1111111111111111111111111111111111111111111111111111111111111111L;
        out.println("b1        to string:"+b1);
        out.println("b1 parseUnsignedInt:"+Long.toUnsignedString(b1));
        out.println("b1   Long.MAX_VALUE:"+Long.MAX_VALUE);
        
        
        testRemainder();
        
        //java.time.LocalTime = new java.time.LocalTime();
        
        streamTest();
 
        
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