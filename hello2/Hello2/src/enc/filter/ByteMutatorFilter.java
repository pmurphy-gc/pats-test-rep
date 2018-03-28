package enc.filter;

 

import java.io.IOException;

 

public abstract class ByteMutatorFilter extends Filter

{

 

    public ByteMutatorFilter(Filter inputFilter)

    {

        super(inputFilter);

    }

    

    public int read(byte[] buf, int off, int len) throws IOException

    {

 

        int ammountRead = this.inputFilter.read(buf,off,len);

        if(ammountRead>0)

        {

            for(int i=off; i<off+ammountRead; i++)

            {

                buf[i]=changeByte(buf[i]);

            }

        }

        return ammountRead;

    }

    

    abstract protected byte changeByte(byte in);

 

    

}