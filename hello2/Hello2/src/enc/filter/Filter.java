package enc.filter;

 

import java.io.IOException;

import java.io.OutputStream;

 

public abstract class Filter

{

    int readBufferSize=10000;

    OutputStream out;

    

    

    Filter inputFilter;

 

    public Filter()

    {

        

    }

    

    public Filter(Filter inputFilter)

    {

        this.inputFilter = inputFilter;

    }

    

    public void setOut(OutputStream out)

    {

        this.out = out;

    }

 

    public int read(byte[] b) throws IOException 

    {

        return read(b, 0, b.length);

    }

    

    public abstract int read(byte[] b, int off, int len) throws IOException;

    

    public void stream() throws IOException

    {

        if(out==null)

            throw new IOException("This filter may not stream since it has not had it's output set via the setOut method.");

        byte[] buf = new byte[readBufferSize];

 

        int ammountRead;

        while ((ammountRead = read(buf)) != -1)

        {

            out.write(buf, 0, ammountRead);

            System.out.println("("+this.getClass()+")Just copied:[" + new String(buf, 0, ammountRead) + "]");

        }

    }

    

}

 

 
