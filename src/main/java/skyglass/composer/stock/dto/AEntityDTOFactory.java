package skyglass.composer.stock.dto;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.stock.domain.IdObject;
import skyglass.composer.stock.persistence.entity.AEntity;

public class AEntityDTOFactory {

	public static <T extends IdObject> List<String> provideUuidsFromReferences(Collection<T> entities) {
		return provideUuidsFromReferences(entities, null);
	}

	public static <T extends IdObject> List<String> provideUuidsFromReferences(Collection<T> entities,
			Predicate<T> filter) {
		if (entities == null) {
			return null;
		}

		Stream<T> stream = entities.stream();
		if (filter != null) {
			stream = stream.filter(filter);
		}

		return stream.filter(e -> e != null && StringUtils.isNotBlank(e.getUuid())).map(e -> e.getUuid()).distinct().collect(Collectors.toList());
	}

	public static String provideUuidFromReference(IdObject entity) {
		if (entity != null) {
			return entity.getUuid();
		}

		return null;
	}

	public static <DTO extends AEntityDTO, E extends AEntity> DTO createVeryBasicDto(E entity,
			Supplier<DTO> constructor) {
		if (entity == null || constructor == null) {
			return null;
		}

		DTO dto = constructor.get();
		dto.setUuid(entity.getUuid());

		return dto;
	}

}
