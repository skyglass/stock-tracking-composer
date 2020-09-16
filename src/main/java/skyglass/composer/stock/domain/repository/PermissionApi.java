package skyglass.composer.stock.domain.repository;

import skyglass.composer.stock.entity.model.UserEntity;

public interface PermissionApi {

	public String getUsernameFromContext();

	public UserEntity getUserFromContext();

	public UserEntity getUser(String userId);

	public void checkAdmin();

	public UserEntity findByName(String name);

}
