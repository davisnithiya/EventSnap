package com.eventsnap.face.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public void sendEmailWithPhotos(String recipientEmail, List<byte[]> matchedPhotos)
			throws MessagingException, IOException {
		// Compose the email content
		String emailContent = "<html><body><h1>Your Betrothal Memories: Captured Moments with Diniya and Frijo</h1>"
				+ "<p>Hi,</p>"
				+ "<p>We're thrilled to share with you the beautiful moments captured at our betrothal celebration.</p>"
				+ "<p>Please find your cherished photos attached in a zip file.</p>"
				+ "<p>Best regards,<br>Diniya & Frijo</p></body></html>";

		// Create a properties object and set mail.smtp properties for Gmail SMTP server
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// Create a Session object with authentication
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("tipotales@gmail.com", "ystwjnbcdqincrpj"); 
																							
			}
		});

		// Create a multipart message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress("tipotales@gmail.com")); 
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
		message.setSubject("Your Moments: Diniya and Frijo's Betrothal ");

		// Create the email body part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(emailContent, "text/html");

		// Create the zip file with matched photos
		byte[] zipFileBytes = createZipFile(matchedPhotos);
		DataSource dataSource = new ByteArrayDataSource(zipFileBytes, "application/zip");

		// Create the attachment part
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.setDataHandler(new DataHandler(dataSource));
		attachmentPart.setFileName("matched_photos.zip");

		// Create multipart message and set its parts
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		multipart.addBodyPart(attachmentPart);

		// Set the multipart message to the email
		message.setContent(multipart);

		// Send the email
		Transport.send(message);
	}

	private byte[] createZipFile(List<byte[]> matchedPhotos) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			int counter = 1;
			for (byte[] photo : matchedPhotos) {
				ZipEntry entry = new ZipEntry("photo_" + counter++ + ".jpg");
				zipOutputStream.putNextEntry(entry);
				zipOutputStream.write(photo);
				zipOutputStream.closeEntry();
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	// Custom DataSource implementation for in-memory byte arrays
	private static class ByteArrayDataSource implements DataSource {
		private final byte[] data;
		private final String contentType;

		public ByteArrayDataSource(byte[] data, String contentType) {
			this.data = data;
			this.contentType = contentType;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public String getName() {
			return "ByteArrayDataSource";
		}
	}
}
