package skyglass.composer.stock.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import skyglass.composer.stock.domain.ExtUserDTO;
import skyglass.composer.stock.persistence.UserEntity;

public class UserDTOFactory extends AEntityDTOFactory {
	public static UserDTO createUserDTO(UserEntity user) {
		UserDTO dto = new UserDTO();
		dto.setUuid(user.getUuid());
		dto.setUsername(user.getName());
		return dto;
	}

	public static List<UserDTO> createUserDTOs(Collection<UserEntity> users) {
		List<UserDTO> userDTOs = new ArrayList<>();
		if (users != null) {
			for (UserEntity user : users) {
				userDTOs.add(createUserDTO(user));
			}
		}
		return userDTOs;
	}

	@NotNull
	public static List<UserDTO> createUserDTOs(List<ExtUserDTO> extUsers, List<UserEntity> dbUsers) {
		if (extUsers == null || extUsers.isEmpty() || dbUsers == null || dbUsers.isEmpty()) {
			return Collections.emptyList();
		}

		List<UserDTO> dtos = new ArrayList<>();
		for (ExtUserDTO extUser : extUsers) {
			for (UserEntity user : dbUsers) {
				if (user != null && user.getName() != null) {
					if (user.getName().equals(extUser.getId())) {
						dtos.add(createUserDTO(user, extUser));
						break;
					}
				}
			}
		}
		return dtos;
	}

	@NotNull
	public static List<UserDTO> createUserDTOs(List<UserEntity> dbUsers, Function<UserEntity, ExtUserDTO> extUserDTOProvider) {
		if (extUserDTOProvider == null || dbUsers == null || dbUsers.isEmpty()) {
			return Collections.emptyList();
		}

		List<UserDTO> dtos = new ArrayList<>();
		for (UserEntity user : dbUsers) {
			if (user != null) {
				ExtUserDTO extUserDto = extUserDTOProvider.apply(user);
				if (extUserDto != null) {
					dtos.add(createUserDTO(user, extUserDto));
				}
			}
		}

		return dtos;
	}

	public static UserDTO createUserDTO(UserEntity user, ExtUserDTO extUserDTO) {
		if (user == null || extUserDTO == null) {
			return null;
		}
		UserDTO dto = new UserDTO();
		dto.setUuid(user.getUuid());
		dto.setUsername(user.getName());

		if (extUserDTO.getName() != null) {
			dto.setFirstName(extUserDTO.getName().getGivenName());
			dto.setLastName(extUserDTO.getName().getFamilyName());
		}
		return dto;
	}

	public static UserEntity createUser(UserEntity user, UserDTO dto) {

		user.setName(dto.getUsername());
		user.setEmail(dto.getEmail());

		return user;
	}

}
