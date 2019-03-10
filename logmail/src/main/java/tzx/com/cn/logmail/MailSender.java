package tzx.com.cn.logmail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MailSender {


    public int senderMail(MailInfo mailInfo) {
        MyAuth myAuth = null;
        if (mailInfo.isValidate()) {
            myAuth = new MyAuth(mailInfo.getUsername(), mailInfo.getPasswd());
        }

        Session mailSessiom = Session.getDefaultInstance(mailInfo.getProperties(), myAuth);
        Transport transport=null;
        try {
            Message mailMsg = new MimeMessage(mailSessiom);
            Address from = new InternetAddress(mailInfo.getFromAddress());
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMsg.setFrom(from);
            mailMsg.setRecipient(Message.RecipientType.TO, to);
            mailMsg.setSubject(mailInfo.getSubject());
            mailMsg.setSentDate(new Date());
            Multipart multipart = new MimeMultipart();
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(mailInfo.getContent(), "text/html;charset=utf-8");

            multipart.addBodyPart(bodyPart);
            List<BodyPart> bodyParts = createDataHandler(mailInfo);
            if (bodyParts != null && bodyParts.size() > 0) {
                for (BodyPart b : bodyParts) {
                    multipart.addBodyPart(b);
                }
            }

            mailMsg.setContent(multipart);

            mailMsg.saveChanges();
            transport=mailSessiom.getTransport();
            transport.connect();
            Transport.send(mailMsg,mailMsg.getAllRecipients());
        } catch (UnsupportedEncodingException e) {
            return CHRSET_ERROR;
        } catch (AddressException e) {
            return ADDRESS_ERROR;
        } catch (MessagingException e) {
            e.printStackTrace();
            return MESSAGING_ERROR;
        }finally {
            if (transport!=null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return MESSAGING_ERROR;
                }
            }
        }

        return MESSAGING_OK;
    }

    private List<BodyPart> createDataHandler(MailInfo mailInfo) throws MessagingException, UnsupportedEncodingException {
        if (mailInfo.getAttachFileNames() != null) {
            List<BodyPart> bodyParts = new ArrayList<>();
            DataSource dataSource;
            BodyPart bodyPart;
            for (File s : mailInfo.getAttachFileNames()) {
                dataSource = createDataSource(s);
                if (dataSource != null) {
                    bodyPart = new MimeBodyPart();
                    bodyPart.setDataHandler(new DataHandler(dataSource));
                    bodyPart.setFileName(MimeUtility.decodeText(dataSource.getName()));
                    bodyParts.add(bodyPart);
                }
            }
            return bodyParts;
        }
        return null;
    }

    private DataSource createDataSource(File file) {
        if (file.exists() && file.isFile()) {
            return new FileDataSource(file);
        }
        return null;
    }

    public static final int MESSAGING_ERROR = 0x110;
    public static final int ADDRESS_ERROR = 0x111;
    public static final int CHRSET_ERROR = 0x112;
    public static final int MESSAGING_OK = 0x0;

}
