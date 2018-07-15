package zeus.network.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

public class CompressUtil {
//	static Deflater deflater;
//	static Inflater inflater;
	static DeflaterOutputStream dos;
	//DeflaterInputStream dis;
//	static InflaterInputStream iis;
	static InflaterOutputStream ios;
	
	static ByteArrayOutputStream bos;
	
//	static ByteArrayInputStream bis;
//	
//	static ZipOutputStream zos;
//	static ZipInputStream zis;
	
	 
    public static void test1(String srcString){
    	byte[] src = srcString.getBytes();
    	System.out.println("src.length " + src.length);
    	
    	byte[] cp = compress1(src);
    	System.out.println("cp.length " + cp.length);
    	
    	byte[] decp = decompress1(cp);
    	
    	System.out.println("decp.length " + decp.length);
    	
    	System.out.println("decompressed --> " + new String(decp));
    }
    
    public static void test2(String srcString){
    	byte[] src = srcString.getBytes();
    	System.out.println("src.length " + src.length);
    	
    	byte[] cp = compress2(src);
    	System.out.println("cp.length " + cp.length);
    	
    	byte[] decp = decompress2(cp);
    	
    	System.out.println("decp.length " + decp.length);
    	
    	System.out.println("decompressed --> " + new String(decp));
    }
    
    public static void test3(String srcString){
    	byte[] src = srcString.getBytes();
    	System.out.println("src.length " + src.length);
    	
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	compress3(src,bos);
    	byte[] cp = bos.toByteArray();
    	
    	System.out.println("cp.length " + cp.length);
    	
    	ByteArrayInputStream bis = new ByteArrayInputStream(cp);
    	byte[] decp = decompress3(bis);
    	
    	System.out.println("decp.length " + decp.length);
    	
    	System.out.println("decompressed --> " + new String(decp));
    }
    
    public static void test4(String srcString){
    	byte[] src = srcString.getBytes();
    	System.out.println("src.length " + src.length);
    	
    	ByteArrayInputStream bis = new ByteArrayInputStream(src);
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	try {
			compress4(bis, bos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	byte[] cp = bos.toByteArray();
    	
    	System.out.println("cp.length " + cp.length);
    	
    	try {
//    		bis.reset();
//    		bos.reset();
        	bis = new ByteArrayInputStream(cp);
        	bos = new ByteArrayOutputStream();
			decompress4(bis, bos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	byte[] decp = bos.toByteArray();
    	System.out.println("decp.length " + decp.length);
    	
    	System.out.println("decompressed --> " + new String(decp));
    }
    
    public static byte[] compress1(byte[] srcdata) {  
        try {  
            bos = new ByteArrayOutputStream();  
            dos = new DeflaterOutputStream(bos);  
            dos.write(srcdata);  
            dos.finish();
            dos.flush();
            //dos.close();
            return bos.toByteArray(); 
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
    
    public static byte[] decompress1(byte[] encdata) {  
        try {  
            bos = new ByteArrayOutputStream();  
            ios = new InflaterOutputStream(bos);  
            ios.write(encdata);
            ios.finish();
            ios.flush();
            ios.close();
            return bos.toByteArray();
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        return null;  
    }  
    
    
    public static byte[] compress2(byte[] data) {  
        byte[] output = new byte[0];  
  
        Deflater compresser = new Deflater(); 
        
        compresser.reset();  
        compresser.setInput(data);  
        compresser.finish();  
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);  
        try {  
            byte[] buf = new byte[1024];  
            while (!compresser.finished()) {  
                int i = compresser.deflate(buf);  
                bos.write(buf, 0, i);  
            }  
            output = bos.toByteArray();  
        } catch (Exception e) {  
            output = data;  
            e.printStackTrace();  
        } finally {  
            try {  
                bos.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        compresser.end();  
        return output;  
    }  
    
    public static byte[] decompress2(byte[] data) {  
        byte[] output = new byte[0];  
  
        Inflater decompresser = new Inflater();  
        decompresser.reset();  
        decompresser.setInput(data);  
  
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);  
        try {  
            byte[] buf = new byte[1024];  
            while (!decompresser.finished()) {  
                int i = decompresser.inflate(buf);  
                o.write(buf, 0, i);  
            }  
            output = o.toByteArray();  
        } catch (Exception e) {  
            output = data;  
            e.printStackTrace();  
        } finally {  
            try {  
                o.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        decompresser.end();  
        return output;  
    }  
    
 
    public static void compress3(byte[] data, OutputStream os) {  
        DeflaterOutputStream dos = new DeflaterOutputStream(os);  
        try {  
            dos.write(data, 0, data.length);  
  
            dos.finish();  
  
            dos.flush();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
    
    public static byte[] decompress3(InputStream is) {  
        InflaterInputStream iis = new InflaterInputStream(is);  
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);  
        try {  
            int i = 1024;  
            byte[] buf = new byte[i];  
  
            while ((i = iis.read(buf, 0, i)) > 0) {  
                o.write(buf, 0, i);  
            }  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return o.toByteArray();  
    } 
    
    public static void compress4(InputStream is, OutputStream os)  
            throws Exception {  
      
        GZIPOutputStream gos = new GZIPOutputStream(os);  
      
        int count;  
        byte data[] = new byte[64];  
        while ((count = is.read(data, 0, 64)) != -1) {  
            gos.write(data, 0, count);  
        }  
      
        gos.finish();  
      
        gos.flush();  
        gos.close();  
    }  
    
    public static void decompress4(InputStream is, OutputStream os)  
            throws Exception {  
      
        GZIPInputStream gis = new GZIPInputStream(is);  
      
        int count;  
        byte data[] = new byte[64];  
        while ((count = gis.read(data, 0, 64)) != -1) {  
            os.write(data, 0, count);  
        }  
      
        gis.close();  
    }  
}
