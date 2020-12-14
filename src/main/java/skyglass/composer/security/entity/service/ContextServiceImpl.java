package skyglass.composer.security.entity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.security.domain.factory.ContextFactory;
import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.security.entity.repository.ContextRepository;

@Component
class ContextServiceImpl implements ContextService {

	@Autowired
	private ContextRepository contextRepository;

	@Autowired
	private ContextFactory contextFactory;

	@Override
	public Iterable<Context> getAll() {
		return contextRepository.findAll().stream()
				.map(e -> contextFactory.object(e))
				.collect(Collectors.toList());
	}

	@Override
	public Context getByUuid(String uuid) {
		ContextEntity entity = this.contextRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return contextFactory.object(entity);
	}

	@Override
	public Context create(Context context) {
		return contextFactory.object(contextRepository.create(
				contextFactory.entity(context)));
	}

	@Override
	public List<Context> find(Context parent) {
		return contextFactory.objectList(contextRepository.find(parent));
	}

	@Override
	public List<Context> findAll(Context parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
