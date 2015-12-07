package ch.ethz.iks.r_osgi.sample.service;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.osgi.framework.BundleContext;

import ch.ethz.iks.r_osgi.sample.api.ServiceInterface;

/**
 * just a simple sample service plays around with strings
 */
public final class ServiceImpl implements ServiceInterface {
	
	public BundleContext context;
	
	public ServiceImpl(BundleContext context) {
		this.context = context;
	}

	/**
	 * echo service, echos <code>count</code> times the message.
	 */
	public String echoService(final String message, final Integer count) {
		StringBuffer buffer = new StringBuffer();
		final int c = count.intValue();
		for (int i = 0; i < c; i++) {
			buffer.append(message);
			if (i < c - 1) {
				buffer.append(" | ");
			}
		}
		return buffer.toString();
	}

	/**
	 * reverse service, returns the reversed message.
	 */
	public String reverseService(String message) {
		throw new RuntimeException("reverse is handled by the smart proxy");
	}

	public void local() {
		System.out.println("Server: local called");
		throw new RuntimeException("Local cannot be called remotely");
	}

	public void zero() {
		System.out.println("Server: zero called.");
	}

	public boolean equalsRemote(Object other) {
		return equals(other);
	}

	public void printRemote(int i, float f) {
		System.out.println("i is " + i);
		System.out.println("f is " + f);
	}

	public boolean verifyBlock(byte[] data, int i, int j, int k) {
		System.out.println("GOT CALLED WITH " + new String(data));
		return true;
	}

	public String[][] checkDoubleArray(String str, int x, int y) {
		final String[][] result = new String[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				result[i][j] = str;
			}
		}
		return result;
	}

	public String[] checkArray(String str, int x) {
		final String[] result = new String[x];
		for (int a = 0; a < x; a++) {
			result[a] = str;
		}

		return result;
	}

	public byte[] echoByteArray1(byte[] bytes) {
		System.out.println(new String(bytes));
		return bytes;
	}

	public byte[][] echoByteArray2(byte[][] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			System.out.println(new String(bytes[i]));
		}
		return bytes;
	}
	
	public int getMatInterpretation(final byte[] matData, int rows, int cols, int type)
	{
		final Mat m = buildMat(matData,rows,cols,type);
		System.out.println(m);
		
		File f = context.getBundle().getDataFile("filename.jpg");
		System.out.println(f.getAbsolutePath());
		boolean bool = Highgui.imwrite(f.getAbsolutePath(), m);
		System.out.println(bool);
		
//		Writer writer = null;
//		
//		try {
//		
//			writer = new BufferedWriter(new OutputStreamWriter(
//			      new FileOutputStream(f)));
//			writer.write(new String(matData));
//		    
//		} catch (IOException ex) {
//		  // report
//		ex.printStackTrace();
//		System.out.println("error");
//		} finally {
//		   try {writer.close();} catch (Exception ex) {
//			   ex.printStackTrace();
//				System.out.println("error");
//			}
//		}
		
		return 1;
	}
	
	private Mat buildMat(byte[] bytes, int rows, int cols, int type) {
		Mat mat = new Mat(rows,cols,type);
    	mat.put(0, 0,bytes);
		
    	return mat;
	}
	
	public static BufferedImage mat2Img(Mat in)
    {
        BufferedImage out;
        System.out.println("proc1");
        byte[] data = new byte[320 * 240 * (int)in.elemSize()];
        int type;
        in.get(0, 0, data);
        System.out.println("proc2");

        if(in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;
        System.out.println("proc3");
        out = new BufferedImage(320, 240, type);
        System.out.println("proc4");
        out.getRaster().setDataElements(0, 0, 320, 240, data);
        return out;
    } 
}