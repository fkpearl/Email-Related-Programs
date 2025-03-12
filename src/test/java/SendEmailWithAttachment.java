import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.testng.annotations.Test;

public class SendEmailWithAttachment {
	@Test
	public void sendEmailWithAttachment() {

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
			message.setSubject("Test Email from Java with attachment");

			// * *Email body part* *
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText("Hello, this is a test email with an attachment.");

			// **Attachment part* *
			MimeBodyPart attachmentPart = new MimeBodyPart();
			String filePath = System.getProperty("user.dir") + "/src/test/resources/Testfile.txt";
			attachmentPart.attachFile(new File(filePath));

			// * *Combine email body and attachment* *
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(textPart);
			multipart.addBodyPart(attachmentPart);
			message.setContent(multipart);

			// Send email
			Transport.send(message);
			System.out.println("Email sent successfully with attachment!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
