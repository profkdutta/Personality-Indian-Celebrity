package classify.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class TriggerMail {
	public static void main(String[] args){
		final String names[]={"chetan_bhagat","Javedakhtarjadu","rahulkanwal"};
		final String from="justbsai@gmail.com";
		final String password="Borra@4727";
		final String uname="justbsai";
		final String subject="Classification Report";
		BufferedReader read=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Do you want to receive a mail ? Y/N");
		try{	
		String ch=read.readLine();
		if(ch.equalsIgnoreCase("Y")){
			System.out.println("Enter Name ?");
			String name=read.readLine();
			System.out.println("Enter Email Id ?");
			String to=read.readLine();
		
			String host = "smtp.gmail.com";
	
		    Properties props = new Properties();
		    props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "587");
	
		    Session session = Session.getInstance(props,
		    		new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		               return new PasswordAuthentication(uname, password);
		            }
			});	
		    Message message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(from));
		    message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
		    message.setSubject(subject);
		    
		    Multipart multipart = new MimeMultipart("mixed");
		    BodyPart messageBody = new MimeBodyPart();
	        messageBody.setContent("<p><strong>Dear, "+name+"</strong></p>"
	        		+ "<p>Thanks for using our service.We look forward to seeing you again.</p>"
	        		+ "<p>Please find the attachment for the report title on the text you have submitted.</p>"
	        		+ "<p>We wish you all the best.</p>"
	        		+ "<p>Thanks,</p>"
	        		+ "<p>Spark Team</p>","text/html");
	        multipart.addBodyPart(messageBody);
	       
	        for(String thand:names){
	        	messageBody = new MimeBodyPart();
	        	DataSource source = new FileDataSource("Reports/Report "+thand+".txt");
		        messageBody.setDataHandler(new DataHandler(source));
		        messageBody.setFileName(thand+" Classification.txt");	
		        multipart.addBodyPart(messageBody);
	        }       
	        message.setContent(multipart);
		    Transport.send(message);
	
	      }else{
				System.exit(0);
	      }}catch (Exception e) {
	    	  e.printStackTrace();
	      }
	}
}
