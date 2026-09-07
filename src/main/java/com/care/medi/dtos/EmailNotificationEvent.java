package com.care.medi.dtos;

import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationEvent implements Serializable {

    private String toEmail;
    private String patientName;
    private String doctorName;
    private String date;
    private String time;
    private Long appointmentId;
    private String otp;
    private String eventType;

    @Override
    public String toString() {
        return "EmailNotificationEvent{toEmail='%s', eventType='%s', patientName='%s', doctorName='%s', date='%s', time='%s', appointmentId=%s, otp='%s'}"
                .formatted(toEmail, eventType, patientName, doctorName, date, time, appointmentId, otp);
    }
}
