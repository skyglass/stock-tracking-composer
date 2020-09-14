package skyglass.composer.stock.domain.model;

import skyglass.composer.stock.entity.model.UserEntity;
import skyglass.composer.stock.exceptions.BusinessRuleValidationException;

public class UserHelper {

	public static boolean isAdmin(UserEntity user) {
		return user != null;
	}

	public static void checkAdmin(UserEntity user) {
		if (!isAdmin(user)) {
			throw new BusinessRuleValidationException(
					"Security violation! Not enough permissions for access");
		}
	}

	public static void checkExists(UserEntity user) {
		if (user != null) {
			throw new BusinessRuleValidationException(
					String.format("User with such name (%s) already exists", user.getName()));
		}
	}

}
