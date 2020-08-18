package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.persistence.entity.UserEntity;

public interface PermissionApi {

	public String getUsernameFromContext();

	public UserEntity getUserFromContext();

	public UserEntity getUser(String userId);

	public void checkAdmin();

	public UserEntity findByName(String name);

}
