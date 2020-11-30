package skyglass.composer.query.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.utils.query.QueryFunctions;

public enum ColumnId {

	stockFrom(TableType.StockMessage, ColumnType.String), //anti-formatter
	stockTo(TableType.StockMessage, ColumnType.String), //
	amount(TableType.StockMessage, ColumnType.DoubleInt), //
	createdAt(TableType.StockMessage, ColumnType.DateTime), //
	year("createdAt", TableType.StockMessage, ColumnType.Integer, (queryContext, path) -> QueryFunctions.year(path, queryContext.getOffsetSeconds(), queryContext.getDatabaseType())), //
	month("createdAt", TableType.StockMessage, ColumnType.Integer, (queryContext, path) -> QueryFunctions.month(path, queryContext.getOffsetSeconds(), queryContext.getDatabaseType())), //
	week("createdAt", TableType.StockMessage, ColumnType.Integer, (queryContext, path) -> QueryFunctions.week(path, queryContext.getOffsetSeconds(), queryContext.getDatabaseType())), //
	dayOfMonth("createdAt", TableType.StockMessage, ColumnType.Integer, (queryContext, path) -> QueryFunctions.dayOfMonth(path, queryContext.getOffsetSeconds(), queryContext.getDatabaseType()));

	private static final List<String> lowerCaseValues;

	static {
		lowerCaseValues = Arrays.asList(values()).stream().map(v -> v.toString().toLowerCase()).collect(Collectors.toList());
	}

	private final String path;

	private final Function<QueryContext, String> pathSupplier;

	private TableType tableType;

	private String tableAlias;

	private ColumnType columnType;

	private ColumnId(TableType tableType, ColumnType columnType) {
		this(null, tableType.getTableAlias(), tableType, columnType, null);
	}

	private ColumnId(String field, TableType tableType, ColumnType columnType) {
		this(field, tableType.getTableAlias(), tableType, columnType, null);
	}

	private ColumnId(String field, String tableAlias, TableType tableType, ColumnType columnType) {
		this(field, tableAlias, tableType, columnType, null);
	}

	private ColumnId(TableType tableType, ColumnType columnType, BiFunction<QueryContext, String, String> pathSupplier) {
		this(null, tableType.getTableAlias(), tableType, columnType, pathSupplier);
	}

	private ColumnId(String field, TableType tableType, ColumnType columnType, BiFunction<QueryContext, String, String> pathSupplier) {
		this(field, tableType.getTableAlias(), tableType, columnType, pathSupplier);
	}

	private ColumnId(String field, String tableAlias, TableType tableType, ColumnType columnType, BiFunction<QueryContext, String, String> pathSupplier) {
		this.path = tableAlias + "." + (field == null ? this.toString() : field);
		this.tableAlias = tableAlias;
		this.tableType = tableType;
		this.columnType = columnType;
		this.pathSupplier = pathSupplier == null ? null : databaseType -> pathSupplier.apply(databaseType, path);
	}

	public String getAlias() {
		return this.toString();
	}

	public String getPath(QueryContext queryContext) {
		if (this.pathSupplier != null) {
			return this.pathSupplier.apply(queryContext);
		}
		return path;
	}

	public TableType getTableType() {
		return tableType;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public static List<Map<String, String>> transformMapList(List<Map<Object, Object>> original,
			QueryContext queryContext, Map<String, String> translationMap) throws IllegalStateException {
		return original.stream().map(m -> transformMap((Map<Object, Object>) m, queryContext, translationMap))
				.collect(Collectors.toList());
	}

	private static Map<String, String> transformMap(Map<Object, Object> original, QueryContext queryContext,
			Map<String, String> translationMap) {
		return original.entrySet().stream()
				.map(e -> convert(e, queryContext, translationMap))
				.collect(Collectors.toMap(e -> e.getLeft(), e -> e.getRight(),
						(v1, v2) -> v1));
	}

	private static Pair<String, String> convert(Map.Entry<Object, Object> entry, QueryContext queryContext,
			Map<String, String> translationMap) {
		String originalKey = translationMap.get(entry.getKey().toString().trim().toLowerCase());
		String originalKeyToLowerCase = originalKey.toLowerCase();
		String originalKeyToLowerCaseSuffix = originalKeyToLowerCase.contains("_")
				? originalKeyToLowerCase.substring(originalKeyToLowerCase.lastIndexOf("_") + 1)
				: null;
		String originalValue = entry.getValue() == null ? "" : entry.getValue().toString().trim();
		if (originalKeyToLowerCaseSuffix != null && lowerCaseValues.contains(originalKeyToLowerCaseSuffix)) {
			for (ColumnId column : Arrays.asList(values())) {
				if (Objects.equals(column.toString().toLowerCase(), originalKeyToLowerCaseSuffix)) {
					return Pair.of(originalKey, column.getColumnType().format(originalValue, queryContext));
				}
			}
		} else if (lowerCaseValues.contains(originalKeyToLowerCase)) {
			for (ColumnId column : Arrays.asList(values())) {
				if (Objects.equals(column.toString().toLowerCase(), originalKeyToLowerCase)) {
					return Pair.of(column.toString(), column.getColumnType().format(originalValue, queryContext));
				}
			}
		}
		return Pair.of(originalKey, originalValue);
	}

}
