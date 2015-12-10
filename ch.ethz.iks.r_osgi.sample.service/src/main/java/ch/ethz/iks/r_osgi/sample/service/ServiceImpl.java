package ch.ethz.iks.r_osgi.sample.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.osgi.framework.BundleContext;

import ch.ethz.iks.r_osgi.sample.api.ServiceInterface;

/**
 * just a simple sample service plays around with strings
 */
public final class ServiceImpl implements ServiceInterface {
	
	public BundleContext context;
	public Socket client;
	
	public ServiceImpl(BundleContext context, Socket client) {
		this.context = context;
		this.client = client;
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
	
	public String getMatInterpretation(final byte[] matData, int rows, int cols, int type)
	{
		final Mat m = buildMat(matData,rows,cols,type);
		System.out.println(m);
		String response = "Error";
		
		UUID i = UUID.randomUUID();
		String filePath = executeCommand("pwd").trim()+"/image_"+i.toString()+".jpg";
		
		if(Highgui.imwrite(filePath, m)) {
			try {
				BufferedOutputStream output = new BufferedOutputStream(client.getOutputStream());
				output.write(filePath.getBytes());
				output.flush();
				
				InputStreamReader reader = new InputStreamReader(client.getInputStream());
				int data = reader.read();
				StringBuilder b = new StringBuilder();
				while(data != -1){
				    char theChar = (char) data;
				    if(theChar == ';') break;
				    b.append(theChar);
				    data = reader.read();
				}
				response = b.toString();
			
			} catch(Exception e) { System.out.println("Erro: " + e.getMessage()); }
		}
		
		System.out.println(response);
		return new String(response);
	}
	
	private Mat buildMat(byte[] bytes, int rows, int cols, int type) {
		Mat mat = new Mat(rows,cols,type);
    	mat.put(0, 0,bytes);
		
    	return mat;
	}
	
	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		return output.toString();

	}
}