package enc.misc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class PrintStreamStore
{
    private ByteArrayOutputStream baos;
    private PrintStream printStream;
    
    
    
    public PrintStream getPrintStream()
    {
        return printStream;
    }


    public PrintStreamStore()
    {
        try
        {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos, true, "utf-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Encode error?",e);
        }
    }

    
    public String extractString()
    {
        String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        printStream.close();
        return content;
    }
    

}
