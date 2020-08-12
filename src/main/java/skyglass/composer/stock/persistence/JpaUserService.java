package skyglass.composer.stock.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import skyglass.composer.stock.domain.CrudAction;
import skyglass.composer.stock.domain.ExtUserDTO;
import skyglass.composer.stock.dto.UserDTO;
import skyglass.composer.stock.dto.UserDTOFactory;
import skyglass.composer.stock.exceptions.ClientException;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.exceptions.NotAllowedException;
import skyglass.composer.stock.exceptions.NotNullableException;
import skyglass.composer.stock.exceptions.NotNullableNorEmptyException;
import skyglass.composer.stock.exceptions.PermissionDeniedException;

@Service
public class JpaUserService implements UserApi {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDTO getUserInfoByName(String username) {
		if (StringUtils.isBlank(username)) {
			throw new NotNullableNorEmptyException("User ID");
		}
		ExtUserDTO extUser = null;
		try {
			extUser = getUserFromExt(username);
		} catch (IOException e) {
			//ignore
		}
		if (extUser == null) {
			throw new ClientException(HttpStatus.NOT_FOUND, "User with ID '" + username + "' could not be found");
		}

		UserEntity user = userRepository.findByName(extUser.getId());
		if (user == null) {
			throw new NotAccessibleException(UserEntity.class, extUser.getId());
		}

		UserDTO dto = UserDTOFactory.createUserDTO(user, extUser);
		if (dto == null) {
			throw new ClientException("Could not create user dto");
		}
		return dto;
	}

	@Override
	public UserDTO updateUser(UserDTO dto) {
		if (dto == null) {
			throw new NotNullableException(UserDTO.class);
		}

		if (StringUtils.isBlank(dto.getUuid())) {
			throw new NotNullableNorEmptyException("User UUID");
		}

		UserEntity entity = userRepository.findByName(dto.getUsername());
		UserEntity response = userRepository.update(UserDTOFactory.createUser(entity, dto));
		ExtUserDTO extUser = userRepository.getUserFromExt(response.getName());
		if (extUser == null) {
			throw new ClientException("User could not be updated");
		}

		UserDTO responseDTO = UserDTOFactory.createUserDTO(response, extUser);
		if (responseDTO == null) {
			throw new ClientException("Could not create user dto");
		}
		return responseDTO;
	}

	@Override
	public UserDTO deleteUser(String userUuid) {
		if (StringUtils.isBlank(userUuid)) {
			throw new NotNullableNorEmptyException("User UUID");
		}

		UserEntity user = userRepository.findByUuid(userUuid);
		if (user == null) {
			throw new NotAccessibleException(UserEntity.class, userUuid);
		}
		UserDTO dto = getUserDTO(user.getName());
		userRepository.deleteUser(user);
		return dto;
	}

	public UserEntity findByUuid(String uuid) {
		if (!userRepository.isCurrentUserAdmin()) {
			throw new NotAllowedException(UserEntity.class, CrudAction.READ);
		}

		return userRepository.findByUuid(uuid);
	}

	public UserEntity findUser(String userNameOrId) {
		if (!userRepository.isCurrentUserAdmin()) {
			throw new NotAllowedException(UserEntity.class, CrudAction.READ);
		}

		return userRepository.findUser(userNameOrId);
	}

	public ExtUserDTO getUserFromExt(String name) throws IOException, IllegalArgumentException, ClientException {
		if (!userRepository.isCurrentUserAdmin()) {
			throw new NotAllowedException(UserEntity.class, CrudAction.READ);
		}

		return userRepository.getUserFromExt(name);
	}

	@NotNull
	@Override
	public UserDTO createUser(UserDTO userDto) {
		if (userDto == null) {
			throw new NotNullableException(UserDTO.class);
		}

		if (!userRepository.isCurrentUserAdmin()) {
			throw new NotAllowedException(userDto.getUuid(), CrudAction.CREATE);
		}

		if (StringUtils.isBlank(userDto.getEmail())) {
			throw new NotNullableNorEmptyException(UserDTO.class, "E-mail");
		}

		if (StringUtils.isBlank(userDto.getFirstName())) {
			throw new NotNullableNorEmptyException(UserDTO.class, "Firstname");
		}

		if (StringUtils.isBlank(userDto.getFirstName())) {
			throw new NotNullableNorEmptyException(UserDTO.class, "Lastname");
		}

		UserEntity entity = userRepository.findByName(userDto.getUsername());
		UserEntity response = userRepository.create(UserDTOFactory.createUser(entity, userDto));
		ExtUserDTO extUser = userRepository.getUserFromExt(response.getName());
		UserDTO responseDTO = UserDTOFactory.createUserDTO(response, extUser);
		if (responseDTO == null) {
			throw new ClientException("Could not create user dto");
		}

		return responseDTO;
	}

	@NotNull
	public UserDTO getUserInfoByUserId(String userUuid)
			throws IOException, IllegalArgumentException, ClientException {
		if (StringUtils.isBlank(userUuid)) {
			throw new NotNullableNorEmptyException("User UUID");
		}

		UserEntity user = userRepository.findByUuid(userUuid);
		if (user == null) {
			throw new NotAccessibleException(UserEntity.class, userUuid);
		}

		ExtUserDTO extUser = userRepository.getUserFromExt(user.getName());
		if (extUser == null) {
			throw new ClientException(HttpStatus.NOT_FOUND, "User with ID '" + userUuid + "' could not be found");
		}

		return UserDTOFactory.createUserDTO(user, extUser);
	}

	public UserDTO getUserDTO(String username) {
		if (StringUtils.isBlank(username)) {
			throw new NotNullableNorEmptyException("User Name");
		}
		return userRepository.getUserByExtID(username);

	}

	public UserDTO getUserDTOSecure(String username) {
		UserDTO result = getUserDTO(username);
		if (result == null) {
			throw new PermissionDeniedException(UserEntity.class, username);
		}
		return result;
	}

	@NotNull
	public List<UserDTO> getUserDTOs(Collection<String> usernames) {
		if (usernames == null) {
			return new ArrayList<>();
		}

		return usernames.stream().map(username -> getUserDTO(username)).filter(dto -> dto != null).collect(Collectors.toList());
	}

	public Map<String, UserDTO> getUsernameDTOMap(Collection<String> usernames) {
		return getUserDTOMap(usernames).entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}

	public Map<String, UserDTO> getUserDTOMap(Collection<String> usernames) {
		Map<String, UserDTO> userDtos = new HashMap<>();
		for (String username : usernames) {
			if (StringUtils.isNotBlank(username) && !userDtos.containsKey(username)) {
				UserEntity user = userRepository.findByName(username);
				if (user != null) {
					ExtUserDTO extUser = userRepository.getUserFromExt(username);
					if (extUser != null) {
						userDtos.put(user.getName(), UserDTOFactory.createUserDTO(user, extUser));
					}
				}
			}
		}
		return userDtos;
	}

	public List<UserDTO> getUserDTOs(List<UserEntity> users) {
		List<ExtUserDTO> extUsers = Collections.emptyList();
		return getUserDTOs(users, extUsers);
	}

	@NotNull
	public List<UserDTO> getUserDTOs(List<UserEntity> users, List<ExtUserDTO> extUsers) {
		if (CollectionUtils.isEmpty(users)) {
			return new ArrayList<>();
		}

		Map<String, ExtUserDTO> nameToExtUser = extUsers.stream().collect(Collectors.toMap(ExtUserDTO::getId, x -> x, (u1, u2) -> u1));
		List<UserDTO> userDtos = new ArrayList<>();
		for (UserEntity user : users) {
			ExtUserDTO extUser = nameToExtUser.get(user.getName());
			if (extUser == null) {
				try {
					extUser = userRepository.getUserFromExt(user.getName());
				} catch (Exception e) {
					extUser = null;
				}
			}
			UserDTO dto = UserDTOFactory.createUserDTO(user, extUser);
			if (dto != null) {
				userDtos.add(dto);
			}
		}

		return userDtos;
	}

	public String getUsername(UserDTO userDto) {
		String username = null;

		if (userDto != null) {
			username = userDto.getUsername();

			if (StringUtils.isBlank(username)) {
				username = getUsername(userDto.getUuid());
			}
		}

		return username;
	}

	protected String getUsername(String userUuid) {
		String username = null;

		if (StringUtils.isNotBlank(userUuid)) {
			UserEntity r = userRepository.findByUuid(userUuid);
			if (r != null) {
				username = r.getName();
			}
		}

		return username;
	}

	@NotNull
	public List<String> getUsernames(Collection<? extends UserDTO> userDtos) {
		if (userDtos == null) {
			return new ArrayList<>();
		}

		return userDtos.stream().map(userDto -> getUsername(userDto)).filter(username -> StringUtils.isNotBlank(username)).collect(Collectors.toList());
	}

}
