package skyglass.composer.stock.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.persistence.entity.UserEntity;
import skyglass.composer.stock.persistence.repository.UserRepository;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class UserRepositoryTest extends AbstractBaseTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void findUserByName() throws Exception {
		UserEntity user = userRepository.findByName("USER1");
		assertTrue(user != null);
		assertEquals("USER1", user.getUsername());
		assertEquals("02655648-7238-48e5-a36e-45025559b219", user.getUuid());
	}

}
