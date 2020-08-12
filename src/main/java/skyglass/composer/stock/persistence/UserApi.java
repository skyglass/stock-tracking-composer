package skyglass.composer.stock.persistence;

import skyglass.composer.stock.dto.UserDTO;

public interface UserApi {

	UserDTO getUserInfoByName(String username);

	UserDTO updateUser(UserDTO dto);

	UserDTO createUser(UserDTO dto);

	UserDTO deleteUser(String userUuid);

}
