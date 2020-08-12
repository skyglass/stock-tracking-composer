package skyglass.composer.stock.test.reset;

import org.springframework.test.context.ContextConfiguration;

import skyglass.composer.stock.test.config.CommonJpaConfig;
import skyglass.composer.stock.test.config.H2JpaConfig;
import skyglass.composer.stock.test.config.MockBeanConfig;
import skyglass.composer.stock.test.config.PsqlJpaConfig;

@ContextConfiguration(classes = { CommonJpaConfig.class, H2JpaConfig.class, PsqlJpaConfig.class, MockBeanConfig.class })
public abstract class AbstractBaseTest extends AbstractSuperBaseTest {

}
