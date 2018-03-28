

package enc.filter;

 

import java.io.File;

import java.io.FileInputStream;

import java.io.IOException;

import java.io.OutputStream;

 

public class FileFilter extends Filter

{

    FileInputStream fis;

 

    public FileFilter(File f) throws Exception

    {

        fis = new FileInputStream(f);

        

    }

    

    public int read(byte[] buf, int off, int len) throws IOException

    {

        System.out.println("About to read from file...");

        return fis.read(buf,off,len);

    }

    

 

    

}

 
