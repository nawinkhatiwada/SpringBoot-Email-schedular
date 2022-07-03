package com.example.emailschedular.web;

import com.example.emailschedular.payload.EmailRequest;
import com.example.emailschedular.payload.EmailResponse;
import com.example.emailschedular.quartz.job.EmailJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class EmailSchedulerController {

    private final Scheduler scheduler;
    private final EmailSchedulerService service;

    public EmailSchedulerController(Scheduler scheduler, EmailSchedulerService service) {
        this.scheduler = scheduler;
        this.service = service;
    }

    @PostMapping("schedule/email")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailScheduleEntity emailRequest) {
        try {
           var response = service.scheduleEmail(emailRequest);
            return ResponseEntity.ok(response);
        } catch (Exception error) {
            log.error("Error while scheduling email", error);
            EmailResponse emailResponse = EmailResponse.builder().success(false).message("Error while scheduling email").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }
    }

    @GetMapping("get")
    public ResponseEntity<String> getApiTest() {
        return ResponseEntity.ok("Get API test pass");
    }

    /*private JobDetail buildJobDetail(EmailRequest emailRequest) {
        // todo loop and create seperate job for each email

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap).storeDurably().build();
    }*/

   /* private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime triggerAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail).
                withIdentity(jobDetail.getKey().getName(), "email-trigger")
                .withDescription("Send email trigger.")
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .startAt(Date.from(triggerAt.toInstant()))
                .build();

    }*/
}
