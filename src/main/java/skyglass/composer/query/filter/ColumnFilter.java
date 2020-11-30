package skyglass.composer.query.filter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "filterType")
@JsonSubTypes({
		@JsonSubTypes.Type(value = NumberColumnFilter.class, name = "number"),
		@JsonSubTypes.Type(value = SetColumnFilter.class, name = "set") })
public abstract class ColumnFilter {
	private String filterType;

	private List<String> values;

	public ColumnFilter(List<Object> values) {
		this.values = values == null ? Collections.emptyList()
				: values.stream().filter(v -> isNotEmpty(v))
						.map(v -> v.toString()).collect(Collectors.toList());
	}

	public String getFilterType() {
		return filterType;
	}

	public List<String> getValues() {
		return values;
	}

	private boolean isNotEmpty(Object value) {
		if (value == null) {
			return false;
		}
		return StringUtils.isNotBlank(value.toString());
	}
}
