package skyglass.composer.stock.domain;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.domain.dto.UserDTO;
import skyglass.composer.stock.entity.service.UserServiceImpl;
import skyglass.composer.stock.test.helper.UserTestHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class CreateUserTest extends AbstractBaseTest {

	@Autowired
	private UserServiceImpl userService;

	private UserTestHelper userTestHelper;

	@Before
	public void init() {
		userTestHelper = UserTestHelper.create(userService);
	}

	@Test
	public void testCreateUser() throws IOException {
		UserDTO result = userTestHelper.createUser("P42");
		result = userTestHelper.getUser(result.getUsername());
		Assert.assertEquals("P42", result.getUsername());
	}

}
