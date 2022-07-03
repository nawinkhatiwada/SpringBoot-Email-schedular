package com.example.emailschedular.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailSchedulerRepository extends JpaRepository<EmailScheduleEntity, Long> {

}
