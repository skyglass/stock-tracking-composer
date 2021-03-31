package skyglass.composer.query.builder;

import static com.google.common.collect.Streams.zip;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Sets;

import skyglass.composer.query.filter.ColumnFilter;
import skyglass.composer.query.filter.DateColumnFilter;
import skyglass.composer.query.filter.NumberColumnFilter;
import skyglass.composer.query.filter.SetColumnFilter;
import skyglass.composer.query.request.AggFunc;
import skyglass.composer.query.request.ColumnId;
import skyglass.composer.query.request.ColumnIdHelper;
import skyglass.composer.query.request.ColumnType;
import skyglass.composer.query.request.ColumnVO;
import skyglass.composer.query.request.CustomGetRowsRequest;
import skyglass.composer.query.request.FilterHelper;
import skyglass.composer.query.request.FilterModel;
import skyglass.composer.query.request.FilterType;
import skyglass.composer.query.request.QueryContext;
import skyglass.composer.query.request.SortModel;
import skyglass.composer.query.request.TableType;
import skyglass.composer.stock.exceptions.BusinessRuleValidationException;
import skyglass.composer.utils.query.NativeQueryUtil;
import skyglass.composer.utils.query.QueryFunctions;

/**
 * Builds Custom SQL queries from CustomGetRowsRequest.
 */
public class CustomQueryBuilder {

	private List<String> groupKeys;

	private boolean isGrouping;

	private List<ColumnVO> valueColumns;

	private List<FilterModel> filterModel;

	private List<SortModel> sortModel;

	private int startRow, endRow;

	private List<ColumnId> rowGroupCols;

	private List<ColumnId> pivotCols;

	private Map<ColumnId, List<String>> pivotValues;

	private boolean isPivotMode;

	private QueryResultDefinition queryResultDefinition;

	private QueryContext queryContext;

	private CustomQueryRegistry queryRegistry;

	private boolean isJpa = false;

	private Map<ColumnId, String> customColumns;

	public CustomQueryBuilder(QueryResultDefinition queryResult, QueryContext queryContext, CustomGetRowsRequest request, Map<ColumnId, List<String>> pivotValues, boolean isJpa) {
		this.queryResultDefinition = queryResult;
		this.queryContext = queryContext;
		this.isJpa = isJpa;

		this.valueColumns = request.getValueCols();
		this.groupKeys = request.getGroupKeys();
		this.pivotCols = request.getPivotCols();
		this.pivotValues = pivotValues;
		this.isPivotMode = request.isPivotMode() && !pivotValues.isEmpty();
		this.rowGroupCols = getRowGroupsToInclude(concat(request.getRowGroupCols().stream(),
				concat(request.getValueCols().stream(), request.getSortModel().stream().map(s -> s.getColId()))
						.filter(c -> c.getAggFunc() == AggFunc.none)
						.map(c -> c.getId())).collect(toList()));
		this.isGrouping = rowGroupCols.size() > 0;
		this.filterModel = CollectionUtils.isEmpty(request.getFilterModel()) ? new ArrayList<>() : request.getFilterModel();
		addPeriodFilter(ColumnId.createdAt);
		this.sortModel = getSortModelToInclude(CollectionUtils.isEmpty(request.getSortModel()) ? new ArrayList<>() : request.getSortModel());
		this.startRow = request.getStartRow();
		this.endRow = request.getEndRow();
		this.queryRegistry = new CustomQueryRegistry(queryResult.getTable(), valueColumns, filterModel, sortModel, groupKeys, rowGroupCols, pivotCols, pivotValues);
		this.customColumns = request.getCustomColumns();
	}

	public CustomQueryBuilder(QueryResultDefinition queryResult, QueryContext queryContext, CustomGetRowsRequest request, Map<ColumnId, List<String>> pivotValues) {
		this(queryResult, queryContext, request, pivotValues, false);
	}

	public Map<ColumnId, List<String>> getPivotValues() {
		return pivotValues;
	}

	public String createTotalCountSql() {
		return createSql(true);
	}

	public String createSql() {
		return createSql(false);
	}

	public String createSql(boolean isTotalCount) {
		return (isTotalCount ? selectTotalCountSql() : selectSql()) + fromSql()
				+ queryRegistry.createSql() + whereSql() + (isTotalCount ? "" : groupBySql() + havingSql() + orderBySql() + limitSql());
	}

	public String createPivotValuesSql(boolean isTotalCount) {
		ColumnId pivotCol = CollectionUtils.isEmpty(pivotCols) ? null : pivotCols.get(0);
		if (pivotCol == null) {
			throw new BusinessRuleValidationException("pivot values request doesn't provide any columns");
		}
		return ColumnIdHelper.getCustomPivotValuesSql(this, pivotCol, (isTotalCount ? selectPivotValueTotalCountSql(
				pivotCol.getPath(queryContext)) : selectPivotValuesSql(pivotCol.getPath(queryContext)))
				+ fromSql() + queryRegistry.createSql() + pivotValuesWhereSql())
				+ (isTotalCount ? "" : pivotValuesOrderBySql() + limitSql());
	}

	public QueryContext getQueryContext() {
		return queryContext;
	}

	public String getCustomColumnLowerCase(ColumnId columnId) {
		String result = customColumns.get(columnId);
		if (StringUtils.isBlank(result)) {
			return "";
		}
		return NativeQueryUtil.getEncoded(result.toLowerCase());
	}

	public void setParamKeyValues(Query query) {
		queryRegistry.setParamKeyValues(query, isPivotMode);
	}

	public void setFilterParamKeyValues(Query query) {
		queryRegistry.setParamKeyValues(query, false);
	}

	public CustomQueryPart bindTables(String sql, TableType... tableType) {
		return queryRegistry.bindTableSql(sql, tableType);
	}

	public CustomQueryPart bindDuplicateTables(String sql, TableType... tableType) {
		return queryRegistry.bindDuplicateTableSql(sql, tableType);
	}

	public CustomQueryPart bindColumns(String sql, ColumnId... columnId) {
		return queryRegistry.bindSql(sql, columnId);
	}

	public CustomQueryPart bindDuplicateColumns(String sql, ColumnId... columnId) {
		return queryRegistry.bindSql(sql, columnId);
	}

	public CustomQueryPart bindTableAndColumns(String sql, TableType tableType, ColumnId... columnIds) {
		return queryRegistry.bindSql(sql, tableType, columnIds);
	}

	public CustomQueryPart bindDuplicateTableAndColumns(String sql, TableType tableType, ColumnId... columnIds) {
		return queryRegistry.bindDuplicateSql(sql, tableType, columnIds);
	}

	public Map<String, String> getSelectAliasTranslationMap() {
		return queryRegistry.getSelectAliasTranslationMap();
	}

	public Map<ColumnId, ColumnType> getColumnTypeMap() {
		return queryRegistry.getColumnTypeMap();
	}

	public void addPeriodFilter(ColumnId columnId) {
		filterModel.add(FilterHelper.createDateFilter(columnId, queryContext.getFrom(), queryContext.getTo()));
	}

	private String selectTotalCountSql() {
		return "SELECT COUNT(" + (isGrouping ? (QueryFunctions.distinct(join(", ", getRowGroupPaths()), queryContext.getDatabaseType())) : "1") + ")";
	}

	private String selectPivotValueTotalCountSql(String pivotCol) {
		return "SELECT COUNT(" + QueryFunctions.distinct(pivotCol, queryContext.getDatabaseType()) + ")";
	}

	private String selectSql() {
		List<String> selectCols;
		Stream<String> rowGroupSelectCols = rowGroupCols.stream()
				.map(valueCol -> valueCol.getPath(queryContext) + " as " + queryRegistry.getSelectParamKey(valueCol.getAlias()));
		if (isPivotMode) {
			selectCols = concat(rowGroupSelectCols, extractPivotStatements()).collect(toList());
		} else {
			Stream<String> valueCols = valueColumns.stream()
					.map(valueCol -> agg(valueCol) + " as " + queryRegistry.getSelectParamKey(valueCol.getId().getAlias()));

			selectCols = concat(rowGroupSelectCols, valueCols).collect(toList());
		}

		if (CollectionUtils.isEmpty(selectCols)) {
			throw new BusinessRuleValidationException("Please, provide at least one select column");
		}

		return "SELECT " + join(", ", selectCols);
	}

	private String selectPivotValuesSql(String pivotCol) {
		return "SELECT " + QueryFunctions.distinct(pivotCol, queryContext.getDatabaseType());
	}

	private String fromSql() {
		return format(" FROM %s %s", queryResultDefinition.getTableName(), queryResultDefinition.getTableAlias());
	}

	private String whereSql() {
		return whereSql(false);
	}

	private String havingSql() {
		return whereSql(true);
	}

	private String pivotValuesWhereSql() {
		return pivotValuesWhereSql(false);
	}

	private String whereSql(boolean having) {
		String whereFilters = (having ? getFilters(having) : concat(getGroupKeyColumns(), getFilters(having)))
				.filter(v -> StringUtils.isNotBlank(v)).collect(joining(" AND "));

		return whereFilters.isEmpty() ? ""
				: format(" %s %s", having ? "HAVING" : "WHERE", whereFilters);
	}

	private String pivotValuesWhereSql(boolean having) {
		String whereFilters = getFilters(having).filter(v -> StringUtils.isNotBlank(v))
				.collect(joining(" AND "));
		return whereFilters.isEmpty() ? ""
				: format(" %s %s", having ? "HAVING" : "WHERE", whereFilters);
	}

	private String groupBySql() {
		return isGrouping ? " GROUP BY " + join(", ", getRowGroupPaths()) : "";
	}

	private String orderBySql() {
		Function<SortModel, String> orderByMapper = model -> agg(model.getColId()) + " " + model.getSort().getType();

		List<String> orderByCols = sortModel.stream()
				.map(orderByMapper)
				.collect(toList());

		return orderByCols.isEmpty() ? "" : " ORDER BY " + join(",", orderByCols);
	}

	private String pivotValuesOrderBySql() {
		Function<SortModel, String> orderByMapper = model -> agg(model.getColId()) + " " + model.getSort().getType();

		List<String> orderByCols = sortModel.stream()
				.map(orderByMapper)
				.collect(toList());

		return " ORDER BY " + (orderByCols.isEmpty() ? (pivotCols.get(0).getPath(queryContext) + " ASC") : join(",", orderByCols));
	}

	private String limitSql() {
		return limitSql(startRow, endRow);
	}

	private Stream<String> getFilters(boolean having) {
		Function<FilterModel, String> applyFilters = entry -> {
			String columnName = agg(entry.getColId());
			ColumnFilter filter = entry.getFilter();

			if (filter instanceof SetColumnFilter) {
				return setFilter().apply(columnName, (SetColumnFilter) filter);
			}

			if (filter instanceof NumberColumnFilter) {
				return numberFilter().apply(columnName, (NumberColumnFilter) filter);
			}

			if (filter instanceof DateColumnFilter) {
				return dateFilter().apply(columnName, (DateColumnFilter) filter);
			}

			return "";
		};

		return filterModel.stream()
				.filter(having ? e -> e.getColId().getAggFunc() != AggFunc.none
						: e -> e.getColId().getAggFunc() == AggFunc.none)
				.map(applyFilters);
	}

	private BiFunction<String, SetColumnFilter, String> setFilter() {
		return (String columnName, SetColumnFilter filter) -> columnName + (filter.getValues().isEmpty() ? " IN ('')" : " IN " + asParamString(filter.getValues()));
	}

	private BiFunction<String, NumberColumnFilter, String> numberFilter() {
		return (String columnName, NumberColumnFilter filter) -> {
			Integer filterValue = filter.getFilter();
			FilterType filterType = filter.getType();
			String operator = filterType.getOperator();

			return columnName + (filterType == FilterType.inRange ? " BETWEEN " + filterValue + " AND " + filter.getFilterTo() : " " + operator + " " + filterValue);
		};
	}

	private BiFunction<String, DateColumnFilter, String> dateFilter() {
		return (String columnName, DateColumnFilter filter) -> {
			Date filterValue = filter.getFilter();
			FilterType filterType = filter.getType();
			String operator = filterType.getOperator();

			return ColumnIdHelper.getPeriodSql(columnName, operator, filterValue, filter.getFilterTo());

		};
	}

	private Stream<String> extractPivotStatements() {

		// create pairs of pivot col and pivot value i.e. (DEALTYPE,Financial), (BIDTYPE,Sell)...
		List<Set<Pair<String, String>>> pivotPairs = pivotValues.entrySet().stream()
				.map(e -> e.getValue().stream()
						.map(pivotValue -> Pair.of(e.getKey().getPath(queryContext), pivotValue))
						.collect(toCollection(LinkedHashSet::new)))
				.collect(toList());

		// create a cartesian product of decode statements for all pivot and value columns combinations
		// i.e. sum(DECODE(DEALTYPE, 'Financial', DECODE(BIDTYPE, 'Sell', CURRENTVALUE)))
		return Sets.cartesianProduct(pivotPairs)
				.stream()
				.flatMap(pairs -> {
					String pivotColStr = pairs.stream()
							.map(pivotCol -> pivotCol.getRight())
							.collect(joining("_"));

					String decodeStr = pairs.stream()
							.map(pair -> "CASE WHEN " + pair.getLeft() + " = " + getParamChar() + queryRegistry.getPivotParamKey(pair.getRight()) + " THEN ")
							.collect(joining(""));

					String closingBrackets = IntStream
							.range(0, pairs.size())
							.mapToObj(i -> " END")
							.collect(joining(""));

					return valueColumns.stream()
							.map(valueCol -> agg(valueCol, decodeStr + valueCol.getId().getPath(queryContext) +
									closingBrackets) + " as " + queryRegistry.getPivotSelectParamKey(pivotColStr + "_" + valueCol.getId().getAlias()));
				});
	}

	private List<ColumnId> getRowGroupsToInclude(List<ColumnId> rowGroupCols) {
		List<ColumnId> result = new ArrayList<>(new LinkedHashSet<>(rowGroupCols.stream().collect(Collectors.toList())));
		if (!isPivotMode || result.size() <= groupKeys.size() + 1) {
			return result;
		}
		//Current implementation of ag-Grid on UI provides all group columns for pivot mode
		//But we should only return one level deeper for pivot mode
		//Therefore, we should take into account groupKeys size to understand on which level we are now
		return result.subList(0, groupKeys.size() + 1);
	}

	private List<SortModel> getSortModelToInclude(List<SortModel> sortModel) {
		if (!isPivotMode || sortModel.size() <= groupKeys.size() + 1) {
			return sortModel;
		}
		//Current implementation of ag-Grid on UI provides all sort columns for pivot mode
		//But we should only return one level deeper for pivot mode
		//Therefore, we should take into account groupKeys size to understand on which level we are now
		return sortModel.subList(0, groupKeys.size() + 1);
	}

	private Stream<String> getGroupKeyColumns() {
		return zip(groupKeys.stream(), rowGroupCols.stream(), (key, group) -> group.getPath(queryContext) + " = " + getParamChar() + queryRegistry.getParamKey(key));
	}

	private List<String> getRowGroupPaths() {
		return rowGroupCols.stream()
				.map(c -> c.getPath(queryContext))
				.collect(toList());
	}

	private String asParamString(List<String> l) {
		return "(" + l.stream().map(s -> getParamChar() + queryRegistry.getParamKey(s)).collect(joining(", ")) + ")";
	}

	public static String limitSql(int startRow, int endRow) {
		return endRow < 0 ? "" : (" LIMIT " + (endRow - startRow + 1) + " OFFSET " + startRow);
	}

	private String agg(ColumnVO columnId) {
		return columnId.getAggFunc() == AggFunc.none ? columnId.getId().getPath(queryContext)
				: String.format("%s(%s)", columnId.getAggFunc().getFunc(), columnId.getId().getPath(queryContext));
	}

	private String agg(ColumnVO columnId, String expression) {
		return columnId.getAggFunc() == AggFunc.none ? expression
				: (columnId.getId().getColumnType() == ColumnType.String
						? stringToNumeric(columnId.getAggFunc().getFunc(), expression)
						: String.format("%s(%s)", columnId.getAggFunc().getFunc(), expression));
	}

	private String getParamChar() {
		return isJpa ? ":" : "?";
	}

	private String stringToNumeric(String func, String expression) {
		return String.format("%s(%s)",
				func, QueryFunctions.stringToNumeric(expression, queryContext.getDatabaseType()));
	}
}
