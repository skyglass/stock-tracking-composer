package skyglass.composer.stock.test.helper;

import java.util.function.Consumer;

import skyglass.composer.stock.dto.UserDTO;
import skyglass.composer.stock.persistence.UserApi;
import skyglass.composer.stock.persistence.UserEntity;
import skyglass.composer.stock.test.bean.MockHelper;
import skyglass.composer.stock.test.config.TestDataConstants;

public class UserLocalTestHelper {
	private static UserLocalTestHelper INSTANCE;

	private UserApi userApi;

	public static UserLocalTestHelper getInstance() {
		return INSTANCE;
	}

	public static UserLocalTestHelper create(UserApi userApi) {
		if (INSTANCE == null) {
			INSTANCE = new UserLocalTestHelper(userApi);
		}
		return INSTANCE;
	}

	private UserLocalTestHelper(UserApi userApi) {
		this.userApi = userApi;
	}

	public UserDTO createUser(String username) {
		return createUser(username, null);
	}

	public UserDTO createUser(String username, Consumer<UserDTO> consumer) {
		UserDTO dto = createUserDto(username, consumer);
		return createUserFromDto(dto);
	}

	public UserDTO createUserFromDto(UserDTO dto) {
		return userApi.createUser(dto);
	}

	public UserDTO getUser(String username) {
		return userApi.getUserInfoByName(username);
	}

	public UserDTO getCurrentUser() {
		UserEntity user = MockHelper.getCurrentUser();
		if (user != null) {
			return userApi.getUserInfoByName(user.getName());
		}
		return null;
	}

	public UserDTO getTestUser1() {
		return userApi.getUserInfoByName(TestDataConstants.getTestUser1().getUsername());
	}

	private UserDTO createUserDto(String username, Consumer<UserDTO> consumer) {
		UserDTO user = new UserDTO();
		user.setEmail("test@email.com");
		user.setFirstName("First");
		user.setLastName("Last");
		user.setUsername(username);
		if (consumer != null) {
			consumer.accept(user);
		}
		return user;
	}

}
