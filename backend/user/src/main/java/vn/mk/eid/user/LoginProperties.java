package vn.mk.eid.user;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("login")
public class LoginProperties {
    private static Integer maxConsPassFaulty = 3;
    private static Long consPassFaultyBlock = 1800L;
    private static Integer maxConsOtpFaulty = 3;
    private static Long consOtpFaultyBlock = 1800L;
    private static Boolean needTwoStep = true;
    private static Integer twoStep = 1;
    private static Long timeExistOtp = 300L;
    private static Long timeSentOtp = 180L;
    private static Integer lenghtOpt = 6;
    private static String templateCreateUser = "template-add-user";
    private static String templateCreateUserAndLink = "template-add-user-and-link";
    private static String templateOtp = "template-otp";
    private static String templateOtpResetPassword = "template-otp-reset-password";
    private static String templateResetPassword = "template-reset-password";
    private static String titleOtp = "Mã OTP";
    private static String titleCreateUser = "Thông Tin Đăng Nhập";
    private static String titleResetPassword = "Thay Đổi Mật Khẩu";
    private static String hostUrl = "host.url";
    private static String secret = "MKgroup1999@@MKHiteck2024";

    public static String getTitleResetPassword() {
        return titleResetPassword;
    }

    public static String getSecret() {
        return secret;
    }

    public static String getTemplateResetPassword() {
        return templateResetPassword;
    }

    public static Integer getTwoStep() {
        return twoStep;
    }

    public static String getTemplateCreateUserAndLink() {
        return templateCreateUserAndLink;
    }

    public static String getHostUrl() {
        return hostUrl;
    }

    public static String getTitleCreateUser() {
        return titleCreateUser;
    }

    public static String getTitleOtp() {
        return titleOtp;
    }

    public static String getTemplateCreateUser() {
        return templateCreateUser;
    }

    public static String getTemplateOtp() {
        return templateOtp;
    }

    public static Integer getLenghtOpt() {
        return lenghtOpt;
    }

    public static Integer getMaxConsOtpFaulty() {
        return maxConsOtpFaulty;
    }

    public static Long getConsPassFaultyBlock() {
        return consPassFaultyBlock;
    }

    public static Integer getMaxConsPassFaulty() {
        return maxConsPassFaulty;
    }

    public static Long getConsOtpFaultyBlock() {
        return consOtpFaultyBlock;
    }

    public static Boolean getNeedTwoStep() {
        return needTwoStep;
    }

    public static Long getTimeExistOtp() {
        return timeExistOtp;
    }

    public static Integer getVerificationWindow(Integer verificationWindow) {
        return verificationWindow;
    }

    public static Long getTimeSentOtp() {
        return timeSentOtp;
    }

    public static String getTemplateOtpResetPassword() {
        return templateOtpResetPassword;
    }
}
