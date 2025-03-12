import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.Test;

public class SendSimpleEmail {
	
	@Test
	public void sendEmailWithOnlyText() {

		final String senderEmail = "Enter your email";
		final String appPassword = "Enter the app password";
		final String recipientEmail = "Enter recipient email";
		
		// SMTP server properties .. this will be different for gmail, yahoo etc. Below
		// is for gmail
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "587");

		// Create a session with authentication
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, appPassword);
			}
		});
		session.setDebug(true);

		try {
			// Create email message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject("Simple Text Test Email");
			message.setText("Hello, this is a test email sent from Java using Gmail!");

			// Send email
			Transport.send(message);
			System.out.println("Email sent successfully");
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
