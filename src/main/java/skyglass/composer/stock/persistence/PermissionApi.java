package skyglass.composer.stock.persistence;

public interface PermissionApi {

	public String getUsernameFromContext();

	public UserEntity getUserFromContext();

	public UserEntity getUser(String userId);

	public void checkAdmin();

	public UserEntity findByName(String name);

}
