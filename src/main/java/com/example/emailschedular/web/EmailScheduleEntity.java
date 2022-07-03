package com.example.emailschedular.web;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Data
@EqualsAndHashCode
public class EmailScheduleEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "email is required.")
    @Valid
    private String email;

    @NotBlank(message = "subject is required.")
    private String subject;

    @NotBlank(message = "body is required.")
    private String body;

    @NotNull(message = "dateTime is required.")
    private LocalDateTime dateTime;

    @NotNull(message = "timeZone is required.")
    private ZoneId timeZone;
}
