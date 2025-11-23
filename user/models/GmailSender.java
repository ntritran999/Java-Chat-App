package user.models;
 import java.io.FileReader;
 import java.io.IOException;
 // https://github.com/BornToGeek1/youtube/tree/master/java/gmail-sender
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

// angus-activation, jakarta.activation, jakarta.mail library
public class GmailSender {
	private static String EMAIL_FROM = "nanghiep23@clc.fitus.edu.vn";
	private static String EMAIL_TO = null;
	private static String APP_PASSWORD = null;
	
	public static void sendMail(String emailTo, String msg) throws Exception {
        Properties prop = new Properties();
        try(FileReader fr = new FileReader("gmail.properties")){
            prop.load(fr);
            APP_PASSWORD = prop.getProperty("GMAIL_PASSWORD");
        }catch(IOException e){
            System.out.println(e);
        }
        EMAIL_TO = emailTo.trim();
		Message message = new MimeMessage(getEmailSession());
		message.setFrom(new InternetAddress(EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO));
		message.setSubject("Reset password account");
		message.setText(msg);
		Transport.send(message);
	}
	
	private static Session getEmailSession() {
		return Session.getInstance(getGmailProperties(), new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(EMAIL_FROM, APP_PASSWORD);
		    }
		});
	}
	
	private static Properties getGmailProperties() {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		return prop;
	}
}