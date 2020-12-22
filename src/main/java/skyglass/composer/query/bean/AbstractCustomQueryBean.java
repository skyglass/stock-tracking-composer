package skyglass.composer.query.bean;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.model.Currency;
import skyglass.composer.common.model.Language;
import skyglass.composer.common.model.UserSettings;
import skyglass.composer.common.repository.EntityBeanUtil;
import skyglass.composer.query.builder.CustomQueryBuilder;
import skyglass.composer.query.builder.CustomResponseBuilder;
import skyglass.composer.query.builder.QueryResultDefinition;
import skyglass.composer.query.request.ColumnId;
import skyglass.composer.query.request.CustomGetRowsRequest;
import skyglass.composer.query.request.QueryContext;
import skyglass.composer.query.response.CustomGetRowsResponse;
import skyglass.composer.query.response.CustomPivotValuesResponse;
import skyglass.composer.security.entity.model.UserEntity;
import skyglass.composer.stock.entity.model.EntityUtil;

@Repository
@Transactional
public abstract class AbstractCustomQueryBean {

	protected abstract void setCustomQuery(CustomQueryBuilder queryBuilder);

	protected abstract QueryResultDefinition getQueryResultDefinition();

	protected abstract UserEntity getUser();

	@Autowired
	protected EntityBeanUtil entityBeanUtil;

	public CustomGetRowsResponse getData(CustomGetRowsRequest request, QueryResultDefinition queryResultDefinition, String timezone, Language language,
			Date from, Date to, Currency currency) {
		QueryContext queryContext = new QueryContext(getSettings(getUser()), entityBeanUtil.getDatabaseType(), timezone, language, from, to, currency);
		Map<ColumnId, List<String>> pivotValues = getPivotValues(request, queryContext);
		CustomQueryBuilder queryBuilder = new CustomQueryBuilder(queryResultDefinition, queryContext, request, pivotValues, false);
		return getData(request, queryBuilder);
	}

	public CustomPivotValuesResponse getPivotValuesData(CustomGetRowsRequest originalRequest, QueryResultDefinition queryResultDefinition,
			Date from, Date to) {
		QueryContext queryContext = new QueryContext(getSettings(getUser()), entityBeanUtil.getDatabaseType(), null, null, from, to, null);
		CustomGetRowsRequest request = CustomGetRowsRequest.getPivotValuesRequest(originalRequest);
		CustomQueryBuilder queryBuilder = new CustomQueryBuilder(queryResultDefinition, queryContext, request, null, false);
		return getPivotValuesData(request, queryBuilder);
	}

	public CustomGetRowsResponse getData(CustomGetRowsRequest request, String timezone, Language language, Date from, Date to,
			Currency currency) {
		CustomQueryBuilder queryBuilder = getQueryBuilder(request, timezone, language, from, to, currency);
		return getData(request, queryBuilder);
	}

	public CustomPivotValuesResponse getPivotValuesData(CustomGetRowsRequest originalRequest, Date from, Date to) {
		CustomGetRowsRequest request = CustomGetRowsRequest.getPivotValuesRequest(originalRequest);
		CustomQueryBuilder queryBuilder = getQueryBuilder(request, null, null, from, to, null);
		return getPivotValuesData(request, queryBuilder);
	}

	protected CustomQueryBuilder getQueryBuilder(CustomGetRowsRequest request, String timezone, Language language,
			Date from, Date to, Currency currency) {
		QueryContext queryContext = new QueryContext(getSettings(getUser()), entityBeanUtil.getDatabaseType(),
				timezone, language, from, to, currency);
		Map<ColumnId, List<String>> pivotValues = getPivotValues(request, queryContext);
		return getQueryBuilder(getQueryResultDefinition(), queryContext, request, pivotValues, false);
	}

	protected CustomGetRowsResponse getData(CustomGetRowsRequest request, CustomQueryBuilder queryBuilder) {
		// generate sql
		setCustomQuery(queryBuilder);
		String sql = queryBuilder.createSql();

		// query db for rows
		Query nativeQuery = entityBeanUtil.createNativeQuery(sql);
		queryBuilder.setParamKeyValues(nativeQuery);
		List<Map<String, String>> rows = ColumnId.transformMapList(
				EntityUtil.getMapListResultSafely(nativeQuery), queryBuilder.getQueryContext(),
				queryBuilder.getSelectAliasTranslationMap());

		// generate total count sql
		String totalCountSql = queryBuilder.createTotalCountSql();

		// query db for total count
		Query totalCountQuery = entityBeanUtil.createNativeQuery(totalCountSql);
		queryBuilder.setFilterParamKeyValues(totalCountQuery);
		Long totalCount = (Long) EntityUtil.getSingleResultSafely(totalCountQuery);

		// create response with our results
		return CustomResponseBuilder.createResponse(request, totalCount.intValue(), rows, queryBuilder.getPivotValues(),
				queryBuilder.getColumnTypeMap());
	}

	protected CustomPivotValuesResponse getPivotValuesData(CustomGetRowsRequest request, CustomQueryBuilder queryBuilder) {
		// generate sql
		setCustomQuery(queryBuilder);
		String sql = queryBuilder.createPivotValuesSql(false);

		// query db for rows
		Query nativeQuery = entityBeanUtil.createNativeQuery(sql);
		queryBuilder.setFilterParamKeyValues(nativeQuery);
		@SuppressWarnings("unchecked")
		List<String> rows = (List<String>) EntityUtil.getListResultSafely(nativeQuery);

		// generate total count sql
		String totalCountSql = queryBuilder.createPivotValuesSql(true);

		// query db for total count
		Query totalCountQuery = entityBeanUtil.createNativeQuery(totalCountSql);
		queryBuilder.setFilterParamKeyValues(totalCountQuery);
		Long totalCount = (Long) EntityUtil.getSingleResultSafely(totalCountQuery);

		// create response with our results
		return CustomResponseBuilder.createPivotValuesResponse(rows, totalCount.intValue());
	}

	protected Map<ColumnId, List<String>> getPivotValues(CustomGetRowsRequest originalRequest, QueryContext queryContext) {
		return originalRequest.getPivotCols().stream()
				.collect(Collectors.toMap(pivotCol -> pivotCol,
						c -> this.getPivotValues(originalRequest, c, queryContext), (a, b) -> a, LinkedHashMap::new));
	}

	private List<String> getPivotValues(CustomGetRowsRequest originalRequest, ColumnId pivotColumn, QueryContext queryContext) {
		CustomGetRowsRequest request = CustomGetRowsRequest.getPivotValuesRequest(originalRequest, pivotColumn);
		CustomQueryBuilder queryBuilder = getQueryBuilder(getQueryResultDefinition(), queryContext, request);
		setCustomQuery(queryBuilder);
		String sql = queryBuilder.createPivotValuesSql(false);

		// query db for rows
		Query nativeQuery = entityBeanUtil.createNativeQuery(sql);
		queryBuilder.setFilterParamKeyValues(nativeQuery);
		@SuppressWarnings("unchecked")
		List<String> rows = (List<String>) EntityUtil.getListResultSafely(nativeQuery);
		return rows;
	}

	protected CustomQueryBuilder getQueryBuilder(QueryResultDefinition queryResultDefinition, QueryContext queryContext, CustomGetRowsRequest request) {
		return new CustomQueryBuilder(queryResultDefinition, queryContext, request, Collections.emptyMap(), false);
	}

	protected CustomQueryBuilder getQueryBuilder(QueryResultDefinition queryResultDefinition, QueryContext queryContext,
			CustomGetRowsRequest request, Map<ColumnId, List<String>> pivotValues) {
		return new CustomQueryBuilder(queryResultDefinition, queryContext, request, pivotValues, false);
	}

	protected CustomQueryBuilder getQueryBuilder(QueryResultDefinition queryResultDefinition,
			QueryContext queryContext, CustomGetRowsRequest request, Map<ColumnId, List<String>> pivotValues, boolean isJpa) {
		return new CustomQueryBuilder(queryResultDefinition, queryContext, request, pivotValues, isJpa);
	}

	private UserSettings getSettings(UserEntity user) {
		return null;
	}

}
