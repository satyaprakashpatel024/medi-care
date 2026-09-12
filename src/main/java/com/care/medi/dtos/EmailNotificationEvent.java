package com.care.medi.dtos;

import com.care.medi.utils.Helpers;
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

    public EmailNotificationEvent(String toEmail, String patientName, String doctorName, String date, String time, Long appointmentId) {
        this.toEmail = toEmail;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.appointmentId = appointmentId;
    }

    @Override
    public String toString() {
        return "EmailNotificationEvent{toEmail='%s', patientName='%s', doctorName='%s', date='%s', time='%s', appointmentId=%s}"
                .formatted(Helpers.maskEmail(toEmail), Helpers.maskName(patientName), Helpers.maskName(doctorName), date, time, appointmentId);
    }
}
