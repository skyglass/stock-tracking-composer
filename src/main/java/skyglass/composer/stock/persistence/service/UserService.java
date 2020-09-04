package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.dto.UserDTO;

public interface UserService {

	UserDTO getUserInfoByName(String username);

	UserDTO updateUser(UserDTO dto);

	UserDTO createUser(UserDTO dto);

	UserDTO deleteUser(String userUuid);

}
