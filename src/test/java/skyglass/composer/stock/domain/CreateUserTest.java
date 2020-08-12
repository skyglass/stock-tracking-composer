package skyglass.composer.stock.domain;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.dto.UserDTO;
import skyglass.composer.stock.persistence.JpaUserService;
import skyglass.composer.stock.test.helper.UserLocalTestHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class CreateUserTest extends AbstractBaseTest {

	@Autowired
	private JpaUserService userService;

	private UserLocalTestHelper userTestHelper;

	@Before
	public void init() {
		userTestHelper = UserLocalTestHelper.create(userService);
	}

	@Test
	public void testCreateUser() throws IOException {
		UserDTO result = userTestHelper.createUser("P42");
		result = userTestHelper.getUser(result.getUsername());
		Assert.assertEquals("P42", result.getUsername());
	}

}
