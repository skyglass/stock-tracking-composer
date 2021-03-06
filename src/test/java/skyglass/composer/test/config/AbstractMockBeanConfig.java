package skyglass.composer.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import skyglass.composer.security.entity.repository.PermissionBean;
import skyglass.composer.security.entity.repository.UserRepository;

public class AbstractMockBeanConfig {

	@Bean
	@Primary
	public PermissionBean permissionBeanSpy(PermissionBean permissionBean) {
		return Mockito.spy(PermissionBean.class);
	}

	@Bean
	@Primary
	@DependsOn({ "permissionBean" })
	public UserRepository userRepositorySpy(UserRepository userRepository) {
		return Mockito.spy(UserRepository.class);
	}

}
