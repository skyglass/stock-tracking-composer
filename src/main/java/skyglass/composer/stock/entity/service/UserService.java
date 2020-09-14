package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.dto.UserDTO;

public interface UserService {

	UserDTO getUserInfoByName(String username);

	UserDTO updateUser(UserDTO dto);

	UserDTO createUser(UserDTO dto);

	UserDTO deleteUser(String userUuid);

}
