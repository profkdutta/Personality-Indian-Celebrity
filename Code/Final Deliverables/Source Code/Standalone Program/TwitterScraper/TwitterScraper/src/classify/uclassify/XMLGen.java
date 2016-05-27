package classify.uclassify;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLGen {

	public String genXML(String encoded) {

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		// root elements
		Document doc = docBuilder.newDocument();
		Element uclassify = doc.createElement("uclassify");
		Attr xmlns=doc.createAttribute("xmlns");
		xmlns.setValue("http://api.uclassify.com/1/RequestSchema");
		uclassify.setAttributeNode(xmlns);
		Attr version=doc.createAttribute("version");
		version.setValue("1.01");
		uclassify.setAttributeNode(version);
		doc.appendChild(uclassify);

		// staff elements
		Element texts= doc.createElement("texts");
		uclassify.appendChild(texts);

		Element textBase64 = doc.createElement("textBase64");
		texts.appendChild(textBase64);
		
		Attr id= doc.createAttribute("id");
		id.setValue("text_1");
		textBase64.setAttributeNode(id);
		textBase64.appendChild(doc.createTextNode(encoded));
		
		Element readCalls =doc.createElement("readCalls");
		uclassify.appendChild(readCalls);
		Attr readApiKey=doc.createAttribute("readApiKey");
		readApiKey.setValue("d39gXR52YW2h");
		readCalls.setAttributeNode(readApiKey);
		String classifiers[]={"myers briggs judging function","myers briggs attitude","myers briggs lifestyle","myers briggs perceiving function"};
		for(int i=1;i<=4;i++){
			Element classify =doc.createElement("classify");
			readCalls.appendChild(classify);
			Attr id1=doc.createAttribute("id");
			id1.setValue("call_"+i);
			classify.setAttributeNode(id1);
			Attr username=doc.createAttribute("username");
			username.setValue("prfekt");
			classify.setAttributeNode(username);
			Attr classifierName=doc.createAttribute("classifierName");
			classifierName.setValue(classifiers[i-1]);
			classify.setAttributeNode(classifierName);
			Attr textId=doc.createAttribute("textId");
			textId.setValue("text_1");
			classify.setAttributeNode(textId);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		String fname="UCRQ"+System.currentTimeMillis()+".xml";
		File xmlfile=new File("UCRequests/"+fname);
		StreamResult result = new StreamResult(xmlfile.getPath());
		transformer.transform(source, result);
		return fname;

	  } catch (Exception e) {
		e.printStackTrace();
		return null;
	  }
	}
}