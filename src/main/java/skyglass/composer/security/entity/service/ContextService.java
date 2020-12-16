package skyglass.composer.security.entity.service;

import java.util.List;

import skyglass.composer.security.domain.model.Context;

public interface ContextService {

	Iterable<Context> getAll();

	Context getByUuid(String uuid);

	Context create(Context context);

	void delete(Context context);

	List<Context> find(Context parent);

	List<Context> findAll(Context parent);

	Context findByName(Context parent, String name);

}
