package classify.uclassify;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;


public class Uclassify extends Thread{
	HashMap<String, String> res=new LinkedHashMap<String,String>();
	String text;
	public HashMap<String,String> getParam(){
		return res;	
	}
	public Uclassify(String text){
		this.text=text;
	}
	public void run(){
		try{
			XMLGen t=new XMLGen();
			String encoded= Base64.getEncoder().encodeToString(text.getBytes());
			String fname=t.genXML(encoded);
			URL url = new URL("http://api.uclassify.com");    
		    URLConnection uconn = url.openConnection();
			uconn.setRequestProperty("Content-Type", "text/xml");
			uconn.setDoInput(true);
			uconn.setDoOutput(true);
			HttpURLConnection conn = (HttpURLConnection) uconn;
			conn.connect();	
			DataOutputStream wr = new DataOutputStream (uconn.getOutputStream());
			wr.writeBytes(FileUtils.readFileToString(new File("UCRequests/"+fname), "UTF-8"));
			wr.close();
		    InputStream is = uconn.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder();
		    String line;
		    while((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append("\n");
		    }
		    conn.disconnect();
		    String path="UCResponses/"+fname.replace('Q','E');
		    PrintWriter out = new PrintWriter(path);
		    out.print(response.toString());
		    out.close();
		    this.res=new XMLRead().getContents(path);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

