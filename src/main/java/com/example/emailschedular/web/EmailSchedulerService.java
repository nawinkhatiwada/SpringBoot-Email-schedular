package com.example.emailschedular.web;

import com.example.emailschedular.payload.EmailRequest;
import com.example.emailschedular.payload.EmailResponse;
import com.example.emailschedular.quartz.job.EmailJob;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class EmailSchedulerService {
    private final EmailSchedulerRepository repository;
    private final Scheduler scheduler;

    public EmailSchedulerService(EmailSchedulerRepository repository,
                                 Scheduler scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    public EmailResponse scheduleEmail(EmailScheduleEntity emailRequest) throws SchedulerException {
        ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
        if (dateTime.isBefore(ZonedDateTime.now())) {
            return EmailResponse.builder().success(false).message("Date time must be after current time.").build();
        }
        EmailScheduleEntity savedJob = repository.save(emailRequest);
        JobDetail jobDetail = buildJobDetail(savedJob);
        Trigger trigger = buildTrigger(jobDetail, dateTime);
        scheduler.scheduleJob(jobDetail, trigger);

        EmailResponse emailResponse = EmailResponse.builder()
                .success(true)
                .jobId(jobDetail.getKey().getName())
                .jobGroup(jobDetail.getKey().getGroup())
                .message("Email scheduled successfully!")
                .build();
        return emailResponse;
    }

    private JobDetail buildJobDetail(EmailScheduleEntity emailRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap).storeDurably().build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime triggerAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail).
                withIdentity(jobDetail.getKey().getName(), "email-trigger")
                .withDescription("Send email trigger.")
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .startAt(Date.from(triggerAt.toInstant()))
                .build();
    }
}
