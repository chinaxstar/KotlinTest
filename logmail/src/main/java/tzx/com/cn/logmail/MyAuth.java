package tzx.com.cn.logmail;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 邮箱验证
 */
public class MyAuth extends Authenticator {
    private String username;
    private String passwd;

    public MyAuth(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, passwd);
    }
}
