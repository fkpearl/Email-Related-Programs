
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Properties;

public class ReadEmailAndResetByCode {

	@Test
    public void verifyConfirmationMail() throws InterruptedException {
    	String verificationCode = null;
    	
		WebDriver driver;
		ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
		driver= new ChromeDriver(options);
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();
		driver.get("https://www.amazon.in/");
		
		driver.findElement(By.xpath("//span[text()='Hello, sign in']")).click();
		driver.findElement(By.xpath("//span[contains(text(),'Need help')]")).click();
		driver.findElement(By.id("auth-fpp-link-bottom")).click();
		String username = "Enter your email";
		String password ="Enter the app password";
		driver.findElement(By.id("ap_email")).sendKeys(username);
		driver.findElement(By.id("continue")).click();
		Thread.sleep(5000);
		

        // IMAP server properties
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        try {
            // Initialize the session with provided properties
            Session emailSession = Session.getDefaultInstance(properties);

            // Create the IMAP store object and connect with the server
            Store store = emailSession.getStore();
            store.connect(username, password);

            // Refresh & Open the inbox folder
            Folder inbox = refreshInbox(store);
            
            // Fetch the emails 
            Message[] messages =  inbox.getMessages();
            
            for (Message message : messages) {
                if (message.getSubject().contains("amazon.in: Password recovery")) {
                    
            	System.out.println("---------------------------------");
                System.out.println("Email Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom());

            	// Extract the email body
                String emailBody = "";
                if (message.isMimeType("text/plain")) {
                    emailBody = message.getContent().toString();
                } else if (message.isMimeType("multipart/*")) {
                    MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                    emailBody = getTextFromMimeMultipart(mimeMultipart);
                    
                }
                String[] words = emailBody.split("verification code is:");
                String[] words2 = words[1].split(" ");
                verificationCode = words2[0].trim();
                }

                
            }
            
            // Close the folder and store
            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        WebElement abc = driver.findElement(By.id("input-box-otp"));
        
        Actions action = new Actions(driver);
        action.moveToElement(abc).click().sendKeys(verificationCode).sendKeys(Keys.TAB).sendKeys(Keys.TAB).sendKeys(Keys.ENTER).build().perform();
        
        Assert.assertEquals(driver.findElement(By.xpath("//div[@class='a-box']//h1")).getText(), "Create new password");
        
        driver.quit();
    }

    // Extract the text rom multipart content (for emails with HTML or attachments)
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent());
            }
        }
        return result.toString();
    }

    private static Folder refreshInbox(Store store) throws Exception {
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        return inbox;
    }
    
    
}
