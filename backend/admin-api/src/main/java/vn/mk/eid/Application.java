package vn.mk.eid;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author liukeshao
 * @date 2018/7/16 09:54
 */
@RequiredArgsConstructor
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@EnableJpaRepositories(basePackages = "vn.mk.eid.common.dao.repository")
@EntityScan(basePackages = "vn.mk.eid.common.dao.entity")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
