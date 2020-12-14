package skyglass.composer.test.config;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import skyglass.composer.security.SecurityBeanConfiguration;
import skyglass.composer.stock.StockBeanConfiguration;
import skyglass.composer.stock.test.bean.ComposerTestBeanConfiguration;

@Configuration
@EnableTransactionManagement
//@ComponentScan(basePackages = { "skyglass.composer.local.repository", "skyglass.composer.local.bean", "skyglass.composer.bean",
//		"skyglass.composer.security.service", "skyglass.composer.security.repository", "skyglass.composer.component" })
//@EnableJpaRepositories(basePackages = { "skyglass.composer.local.repository", "skyglass.composer.repository.jpa" })
@Import({ SecurityBeanConfiguration.class, StockBeanConfiguration.class,
		ComposerTestBeanConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
public class CommonJpaConfig {

	@Autowired
	private Environment env;

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setThreadNamePrefix("SpringAsyncThread-");
		executor.initialize();

		return executor;
	}

	@Bean
	public DataSource dataSource() throws SQLException {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(env.getProperty("jdbc.user"));
		dataSource.setPassword(env.getProperty("jdbc.pass"));

		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] {
				"skyglass.composer.stock.entity.model",
				"skyglass.composer.security.entity.model" });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaProperties(additionalProperties());

		em.setPersistenceUnitName("platform");
		em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		em.setSharedCacheMode(SharedCacheMode.NONE);
		em.afterPropertiesSet();
		em.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

		return em;
	}

	@Bean
	public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	protected Properties additionalProperties() {
		final Properties hibernateProperties = new Properties();
		//eclipseLinkProperties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, "commit");
		//eclipseLinkProperties.put(PersistenceUnitProperties.DDL_GENERATION, "none");
		//eclipseLinkProperties.put(PersistenceUnitProperties.LOGGING_LEVEL, "SEVERE");
		//eclipseLinkProperties.put(PersistenceUnitProperties.WEAVING, "static");
		//eclipseLinkProperties.put(PersistenceUnitProperties.QUERY_CACHE, "false");

		return hibernateProperties;
	}
}
