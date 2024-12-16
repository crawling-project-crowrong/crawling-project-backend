package itcast.mail.application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import itcast.mail.dto.request.EmailSender;
import itcast.mail.dto.request.SendMailRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Mock
    private EmailSender emailSender;

    @Test
    @DisplayName("올바르게 요청을 하면 메일이 전송된다.")
    void mail_send_success_test() {
        // given
        final SendMailRequest sendMailRequest = new SendMailRequest(
                List.of("seonjun0906@gmail.com"),
                "안녕하세요1",
                "안녕하세요2"
        );

        final SendEmailRequest sendEmailRequest = mock(SendEmailRequest.class);
        when(emailSender.from(sendMailRequest)).thenReturn(sendEmailRequest);

        // when
        mailService.send(sendMailRequest);

        // then
        verify(emailSender).from(sendMailRequest);
        verify(amazonSimpleEmailService).sendEmail(sendEmailRequest);
    }
}