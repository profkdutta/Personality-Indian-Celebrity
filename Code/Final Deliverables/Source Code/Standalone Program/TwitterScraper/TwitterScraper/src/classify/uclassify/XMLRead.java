package classify.uclassify;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XMLRead {
	public HashMap<String,String> getContents(String path){
		try{
			HashMap<String, String> uclass=new LinkedHashMap<String,String>();
			Document dom;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom=db.parse(path);
			Element doc=dom.getDocumentElement();
			NodeList list=doc.getElementsByTagName("classify");
			for(int i=0;i<list.getLength();i++){
				Node c = list.item(i);
				Element cele = (Element)c;
				NodeList classification = cele.getElementsByTagName("classification");
				Element e1 = (Element)classification.item(0);
				NodeList classes=e1.getElementsByTagName("class");
				Element class1=(Element)classes.item(0);
				Element class2=(Element)classes.item(1);
				uclass.put(class1.getAttribute("className"),class1.getAttribute("p"));
				uclass.put(class2.getAttribute("className"),class2.getAttribute("p"));
			}
			return uclass;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}
}
