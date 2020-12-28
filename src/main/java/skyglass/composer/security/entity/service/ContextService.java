package skyglass.composer.security.entity.service;

import java.util.List;

import skyglass.composer.security.domain.model.Context;

public interface ContextService {

	List<Context> findAll();

	Context getByUuid(String uuid);

	Context create(String parentUuid, Context context);

	List<Context> createOrUpdate(String parentUuid, Context... contexts);

	List<Context> move(String parentUuidFrom, String parentUuidTo, Context... contexts);

	List<Context> copy(String parentUuidFrom, String parentUuidTo, Context... contexts);

	void delete(String contextUuid);

	void deleteAll(String... contextUuids);

	List<Context> find(String parentUuid);

	List<Context> findAll(String parentUuid);

	Context findByName(String parentUuid, String name);

}
