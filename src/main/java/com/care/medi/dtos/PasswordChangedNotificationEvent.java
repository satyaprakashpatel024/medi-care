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
public class PasswordChangedNotificationEvent implements Serializable {
    private String toEmail;

    @Override
    public String toString() {
        return "PasswordChangedNotificationEvent{toEmail='%s'}"
                .formatted(Helpers.maskEmail(toEmail));
    }
}
