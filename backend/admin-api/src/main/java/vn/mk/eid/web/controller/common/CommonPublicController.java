package vn.mk.eid.web.controller.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.entities.misc.SystemInfo;

@RestController
@RequestMapping("/api/public/common")
public class CommonPublicController {

    @Value("${build.date: #{null}}")
    String buildDate;
    @Value("${build.version: #{null}}")
    String buildVersion;
    @GetMapping("/systemInfo")
    public ServiceResult<SystemInfo> getSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setVersion(buildVersion);
        systemInfo.setName("MK ABIS");
        systemInfo.setBuildDate(buildDate);
        return ServiceResult.ok(systemInfo);
    }



}
