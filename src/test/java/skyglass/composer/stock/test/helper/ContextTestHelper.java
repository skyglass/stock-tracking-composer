package skyglass.composer.stock.test.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.service.ContextService;

public class ContextTestHelper {

	private ContextService contextApi;

	public static ContextTestHelper create(ContextService contextApi) {
		return new ContextTestHelper(contextApi);
	}

	private ContextTestHelper(ContextService contextApi) {
		this.contextApi = contextApi;
	}

	public Context create(String name, Owner owner) {
		return create(name, owner, null);
	}

	@SuppressWarnings("unchecked")
	public List<Context> create(Owner owner, Context parent, String... names) {
		return contextApi.createOrUpdate(
				parent == null ? null : parent.getUuid(),
				createDtos(owner, parent, Stream.of(names)
						.map(n -> createPair(n)).collect(Collectors.toList()).toArray(new Pair[0])));
	}

	@SafeVarargs
	public final List<Context> createOrUpdate(Owner owner, Context parent, Pair<String, String>... uuidNames) {
		return contextApi.createOrUpdate(
				parent == null ? null : parent.getUuid(),
				createDtos(owner, parent, uuidNames));
	}

	public Context create(String name, Owner owner, Context parent) {
		return contextApi.create(
				parent == null ? null : parent.getUuid(),
				createDto(name, owner, parent, null));
	}

	@SafeVarargs
	private final Context[] createDtos(Owner owner, Context parent, Pair<String, String>... uuidNames) {
		List<Context> result = new ArrayList<>();
		for (Pair<String, String> pair : uuidNames) {
			Context dto = new Context(pair.getLeft(), pair.getRight(), owner, parent);
			result.add(dto);
		}
		return result.toArray(new Context[0]);
	}

	private Context createDto(String name, Owner owner, Context parent, Consumer<Context> consumer) {
		Context dto = new Context(null, name, owner, parent);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

	private Pair<String, String> createPair(String name) {
		return Pair.of(null, name);
	}

}
