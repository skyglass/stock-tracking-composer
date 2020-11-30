package skyglass.composer.query.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class CustomQueryPart {

	private boolean sqlResultHasDuplicates;

	private List<Pair<String, Object>> parameters = new ArrayList<>();

	private String sql;

	public CustomQueryPart(String sql, boolean sqlResultHasDuplicates) {
		this.sql = sql;
		this.sqlResultHasDuplicates = sqlResultHasDuplicates;
	}

	public CustomQueryPart setParameter(String name, Object value) {
		parameters.add(Pair.of(name, value));
		return this;
	}

	public List<Pair<String, Object>> getParameters() {
		return parameters;
	}

	public boolean isSqlResultHasDuplicates() {
		return sqlResultHasDuplicates;
	}

	public String getSql() {
		return sql;
	}

}
