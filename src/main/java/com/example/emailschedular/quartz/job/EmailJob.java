package com.example.emailschedular.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class EmailJob extends QuartzJobBean {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public EmailJob(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        List<String> recipientEmailList = List.of(jobDataMap.getString("email").split(","));
        recipientEmailList.forEach((recipientEmail) ->
                sendEmail(mailProperties.getUsername(), recipientEmail, subject, body)
        );
    }

    private void sendEmail(String fromEmail, String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
            log.info("Sending email to: {}", toEmail);
            mailSender.send(message);
        } catch (Exception error) {
            log.error("Error while sending email", error);
        }
    }
}
