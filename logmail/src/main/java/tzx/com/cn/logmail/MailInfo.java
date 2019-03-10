package tzx.com.cn.logmail;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * 邮箱信息
 */
public class MailInfo {
    //邮箱服务器地址
    private String mailServerHost;
    //邮箱服务器端口
    private String mailServerPort;
    //发信邮箱
    private String fromAddress;
    //目的邮箱
    private String toAddress;
    //发信邮箱账号
    private String username;
    //发信邮箱密码
    private String passwd;
    //标题
    private String subject;
    //内容
    private String content;
    //附件
    private List<File> attachFileNames;
    private boolean isValidate = false;
    //加密协议
    private EncryProtocol protocol;


    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<File> getAttachFileNames() {
        return attachFileNames;
    }

    public void setAttachFileNames(List<File> attachFileNames) {
        this.attachFileNames = attachFileNames;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isValidate() {
        return isValidate;
    }

    public void setValidate(boolean validate) {
        isValidate = validate;
    }

    public EncryProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(EncryProtocol protocol) {
        this.protocol = protocol;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        if (mailServerHost != null) properties.setProperty("mail.smtp.host", mailServerHost);
        if (mailServerPort != null) properties.setProperty("mail.smtp.port", mailServerPort);
        properties.setProperty("mail.smtp.auth", isValidate ? "true" : "false");
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        if (protocol != null && protocol == EncryProtocol.SSL) {
            properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.setProperty("mail.smtp.socketFactory.port", mailServerPort);
        }

        if (protocol!=null&&protocol==EncryProtocol.TLS){
            properties.put("mail.smtp.starttls.enable", "true");
        }

        return properties;
    }

    public enum EncryProtocol {
        SSL,TLS
    }
}
