package vn.mk.eid.common.util;

import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.exception.ServiceException;
import vn.mk.eid.common.security.DeviceInfo;

public class CurrentDevice {

    private static ThreadLocal<DeviceInfo> sessionThreadLocal = new ThreadLocal<>();

    public static void setInfo(DeviceInfo deviceInfo) {
        if (sessionThreadLocal == null) {
            sessionThreadLocal = new ThreadLocal<>();
        }
        sessionThreadLocal.set(deviceInfo);
    }

    public static void clear() {
        sessionThreadLocal.remove();
    }


    public static DeviceInfo getDeviceInfo() {
        DeviceInfo user = sessionThreadLocal.get();
        if (user == null) {
            throw new ServiceException(ResultCode.EXPIRED_TOKEN);
        }
        return user;
    }
}
