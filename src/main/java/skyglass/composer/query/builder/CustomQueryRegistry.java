package skyglass.composer.query.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Streams;

import skyglass.composer.query.request.ColumnId;
import skyglass.composer.query.request.ColumnType;
import skyglass.composer.query.request.ColumnVO;
import skyglass.composer.query.request.FilterModel;
import skyglass.composer.query.request.SortModel;
import skyglass.composer.query.request.TableType;

public class CustomQueryRegistry {

	private static final String PIVOT_ID = "pivot";

	private static final String PIVOT_ALIAS_ID = "pivotalias";

	private Set<ColumnId> bindedColumns = new TreeSet<>();

	private Map<ColumnId, String> groupKeysMap = new HashMap<>();

	private Set<TableType> bindedTables = new TreeSet<>();

	private Map<String, String> paramKeyValueMap = new HashMap<>();

	private Map<String, String> paramValueKeyMap = new HashMap<>();

	private Map<String, String> pivotParamKeyValueMap = new HashMap<>();

	private Map<String, String> pivotParamValueKeyMap = new HashMap<>();

	private int selectAliasIndex = 1;

	private Map<String, String> selectParamKeyValueMap = new HashMap<>();

	private Map<String, String> selectParamValueKeyMap = new HashMap<>();

	private List<Pair<CustomQueryPart, TableType[]>> bindedTableParts = new ArrayList<>();

	private List<Pair<CustomQueryPart, ColumnId[]>> bindedColumnParts = new ArrayList<>();

	private List<Pair<CustomQueryPart, Pair<TableType, ColumnId[]>>> bindedTableColumnParts = new ArrayList<>();

	private List<CustomQueryPart> orderedParts = new ArrayList<>();

	private Set<TableType> duplicateFlags;

	private TableType mainTableType;

	public CustomQueryRegistry(TableType mainTableType, List<ColumnVO> valueColumns, List<FilterModel> filterModel,
			List<SortModel> sortModel, List<String> groupKeys, List<ColumnId> rowGroupCols, List<ColumnId> pivotCols, Map<ColumnId, List<String>> pivotValues) {
		this.mainTableType = mainTableType;
		valueColumns.stream().forEach(v -> {
			bindedColumns.add(v.getId());
			bindedTables.add(v.getId().getTableType());
		});
		filterModel.stream().forEach(v -> {
			bindedColumns.add(v.getColId().getId());
			bindedTables.add(v.getColId().getId().getTableType());
		});
		sortModel.stream().forEach(v -> {
			bindedColumns.add(v.getColId().getId());
			bindedTables.add(v.getColId().getId().getTableType());
		});

		rowGroupCols.stream().forEach(v -> {
			bindedColumns.add(v);
			bindedTables.add(v.getTableType());
		});
		Streams.zip(groupKeys.stream(), rowGroupCols.stream(), (key, group) -> groupKeysMap.put(group, key)).collect(Collectors.toList());

		Streams.concat(pivotCols.stream(), pivotValues.keySet().stream()).forEach(v -> {
			bindedColumns.add(v);
			bindedTables.add(v.getTableType());
		});
		for (ColumnId columnId : bindedColumns) {
			int index = 1;
			String groupKeyValue = groupKeysMap.get(columnId);
			if (groupKeyValue != null) {
				String key = columnId.getAlias() + Integer.toString(index);
				index++;
				paramKeyValueMap.put(key, groupKeyValue);
				paramValueKeyMap.put(groupKeyValue, key);
			}
			for (FilterModel entry : filterModel) {
				if (entry.getColId().getId() == columnId) {
					for (String value : entry.getFilter().getValues()) {
						String key = columnId.getAlias() + Integer.toString(index);
						index++;
						paramKeyValueMap.put(key, value);
						paramValueKeyMap.put(value, key);
					}
				}
			}
			List<String> pivotValuesList = pivotValues.get(columnId);
			if (CollectionUtils.isNotEmpty(pivotValuesList)) {
				String pivotColumnAlias = PIVOT_ID + columnId.getAlias().substring(0, 1).toUpperCase() + columnId.getAlias().substring(1);
				int pivotIndex = 1;
				for (String value : pivotValuesList) {
					String key = pivotColumnAlias + Integer.toString(pivotIndex);
					pivotIndex++;
					pivotParamKeyValueMap.put(key, value);
					pivotParamValueKeyMap.put(value, key);
				}
			}

		}
	}

	public CustomQueryPart bindSql(String sql, TableType tableType, ColumnId... columnIds) {
		return bindTableColumnSql(sql, false, tableType, columnIds);
	}

	public CustomQueryPart bindDuplicateSql(String sql, TableType tableType, ColumnId... columnIds) {
		return bindTableColumnSql(sql, true, tableType, columnIds);
	}

	public CustomQueryPart bindTableSql(String sql, TableType... tableType) {
		return bindTableSql(sql, false, tableType);
	}

	public CustomQueryPart bindDuplicateTableSql(String sql, TableType... tableType) {
		return bindTableSql(sql, true, tableType);
	}

	public CustomQueryPart bindDuplicateSql(String sql, TableType... tableType) {
		return bindTableSql(sql, true, tableType);
	}

	public CustomQueryPart bindSql(String sql, ColumnId... columnId) {
		return bindColumnSql(sql, false, columnId);
	}

	public CustomQueryPart bindDuplicateSql(String sql, ColumnId... columnId) {
		return bindColumnSql(sql, true, columnId);
	}

	private CustomQueryPart bindTableSql(String sql, boolean sqlResultHasDuplicates, TableType... tableType) {
		CustomQueryPart queryPart = new CustomQueryPart(sql, sqlResultHasDuplicates);
		bindedTableParts.add(Pair.of(queryPart, tableType));
		orderedParts.add(queryPart);
		return queryPart;
	}

	private CustomQueryPart bindColumnSql(String sql, boolean sqlResultHasDuplicates, ColumnId... columnId) {
		CustomQueryPart queryPart = new CustomQueryPart(sql, sqlResultHasDuplicates);
		bindedColumnParts.add(Pair.of(queryPart, columnId));
		orderedParts.add(queryPart);
		return queryPart;
	}

	private CustomQueryPart bindTableColumnSql(String sql, boolean sqlResultHasDuplicates, TableType tableType, ColumnId... columnId) {
		CustomQueryPart queryPart = new CustomQueryPart(sql, sqlResultHasDuplicates);
		bindedTableColumnParts.add(Pair.of(queryPart, Pair.of(tableType, columnId)));
		orderedParts.add(queryPart);
		return queryPart;
	}

	public boolean shouldPreventDuplicates() {
		List<CustomQueryPart> queryParts = resolveQueryParts();
		if (queryParts.size() == 0) {
			return false;
		}
		for (CustomQueryPart queryPart : queryParts) {
			if (queryPart.isSqlResultHasDuplicates()) {
				return true;
			}
		}
		return false;
	}

	private List<CustomQueryPart> resolveQueryParts() {
		return Streams.concat(resolveTableParts().stream(),
				resolveColumnParts().stream(),
				resolveTableColumnParts().stream()).collect(Collectors.toList());
	}

	private List<CustomQueryPart> resolveTableParts() {
		List<CustomQueryPart> result = new ArrayList<>();
		for (Pair<CustomQueryPart, TableType[]> pair : bindedTableParts) {
			for (TableType tableType : pair.getRight()) {
				if (tableType == mainTableType || bindedTables.contains(tableType)) {
					result.add(pair.getLeft());
					break;
				}
			}
		}
		return result;
	}

	private List<CustomQueryPart> resolveColumnParts() {
		List<CustomQueryPart> result = new ArrayList<>();
		for (Pair<CustomQueryPart, ColumnId[]> pair : bindedColumnParts) {
			for (ColumnId columnId : pair.getRight()) {
				if (bindedColumns.contains(columnId)) {
					result.add(pair.getLeft());
					break;
				}
			}
		}
		return result;
	}

	private List<CustomQueryPart> resolveTableColumnParts() {
		List<CustomQueryPart> result = new ArrayList<>();
		for (Pair<CustomQueryPart, Pair<TableType, ColumnId[]>> pair : bindedTableColumnParts) {
			if (pair.getRight().getLeft() == mainTableType || bindedTables.contains(pair.getRight().getLeft())) {
				result.add(pair.getLeft());
			} else {
				for (ColumnId columnId : pair.getRight().getRight()) {
					if (bindedColumns.contains(columnId)) {
						result.add(pair.getLeft());
						break;
					}
				}
			}
		}
		return result;
	}

	public String createSql() {
		List<CustomQueryPart> bindedParts = resolveQueryParts();
		String result = "";
		if (CollectionUtils.isNotEmpty(bindedParts)) {
			for (CustomQueryPart orderedPart : orderedParts) {
				if (bindedParts.contains(orderedPart)) {
					result = result + " " + orderedPart.getSql();
				}
			}
		}
		return result;
	}

	public String getParamKey(String value) {
		return paramValueKeyMap.get(value);
	}

	public String getPivotParamKey(String value) {
		return pivotParamValueKeyMap.get(value);
	}

	public String getPivotSelectParamKey(String value) {
		String key = selectParamValueKeyMap.get(value);
		if (key == null) {
			key = PIVOT_ALIAS_ID + Integer.toString(selectAliasIndex);
			selectParamKeyValueMap.put(key, value);
			selectAliasIndex++;
		}
		return key;
	}

	public String getSelectParamKey(String value) {
		String key = selectParamValueKeyMap.get(value);
		if (key == null) {
			key = value.toLowerCase();
			selectParamKeyValueMap.put(key, key);
		}
		return key;
	}

	public Map<String, String> getSelectAliasTranslationMap() {
		return selectParamKeyValueMap;
	}

	public Map<ColumnId, ColumnType> getColumnTypeMap() {
		return bindedColumns.stream().collect(Collectors.toMap(
				Function.identity(), ColumnId::getColumnType, (v1, v2) -> v1));
	}

	public void setParamKeyValues(Query query, boolean pivotValuesMode) {
		if (pivotValuesMode) {
			pivotParamKeyValueMap.entrySet().stream().forEach(
					e -> query.setParameter(e.getKey(), e.getValue()));
		}

		paramKeyValueMap.entrySet().stream().forEach(
				e -> query.setParameter(e.getKey(), e.getValue()));

		resolveQueryParts().stream().flatMap(e -> e.getParameters().stream())
				.forEach(e -> query.setParameter(e.getLeft(), e.getRight()));
	}

}
