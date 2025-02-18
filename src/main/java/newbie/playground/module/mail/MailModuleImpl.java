package newbie.playground.module.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;

@Log4j2
@Service
public class MailModuleImpl {

    private final JavaMailSender javaMailSender;

    private InternetAddress ourAddress;

    public MailModuleImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;

        try {
            this.ourAddress = new InternetAddress("oneachoice@playground.com", "Playground");
        } catch (UnsupportedEncodingException e) {
            log.info("인터넷 메일 주소 생성에 실패하였습니다.");
        }
    }

    public void sendMimeMail(String subject, String text, String... recipients) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        for (String recipient : recipients) {
            try {
                mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
            } catch (MessagingException e) {
                log.info("수신자를 추가하는 데 실패하였습니다. [{}]", recipient);
            }
        }

        try {
            mimeMessage.setSubject(subject);
            mimeMessage.setSentDate(Date.valueOf(LocalDate.now()));
            mimeMessage.setText(text, "UTF-8", "html");
            mimeMessage.setFrom(ourAddress);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("메세지 전송 실패");

            throw new MessagingException("메세지 전송 실패", e);
        }
    }
}
