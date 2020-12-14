package skyglass.composer.security.entity.repository;

import skyglass.composer.security.entity.model.UserEntity;

public interface PermissionApi {

	public String getUsernameFromContext();

	public UserEntity getUserFromContext();

	public UserEntity getUser(String userId);

	public void checkAdmin();

	public UserEntity findByName(String name);

}
