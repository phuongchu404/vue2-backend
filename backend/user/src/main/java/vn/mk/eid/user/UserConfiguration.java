package vn.mk.eid.user;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import vn.mk.eid.common.annotation.EnableServiceExceptionHandler;
import vn.mk.eid.common.dao.repository.PermissionRepository;
import vn.mk.eid.common.dao.repository.PermissionRoleRepository;
import vn.mk.eid.user.cache.PermissionCache;

@Configuration
@EnableTransactionManagement
@EnableServiceExceptionHandler
@ComponentScan(basePackages = "vn.mk.eid.user")
@EnableConfigurationProperties(LoginProperties.class)
public class UserConfiguration {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("channel_update_permissions"));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(PermissionCache permissionCache) {
        return new MessageListenerAdapter(permissionCache);
    }

    @Bean
    PermissionCache permissionCache(PermissionRoleRepository permissionRoleRepository, PermissionRepository permissionRepository) {
        return new PermissionCache(permissionRoleRepository, permissionRepository);
    }
}
