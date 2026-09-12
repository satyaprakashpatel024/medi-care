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
public class OtpNotificationEvent implements Serializable {
    private String toEmail;
    private String otp;

    @Override
    public String toString() {
        return "OtpNotificationEvent{toEmail='%s', otp='%s'}"
                .formatted(Helpers.maskEmail(toEmail), Helpers.maskOtp(otp));
    }
}
