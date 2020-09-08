package skyglass.composer.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.persistence.entity.ItemEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Item extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

	public static Item mapEntity(ItemEntity entity) {
		return new Item(entity.getUuid(), entity.getName());

	}

	public static ItemEntity map(Item entity) {
		return new ItemEntity(entity.getUuid(), entity.getName());

	}

}
