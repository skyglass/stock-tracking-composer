package skyglass.composer.query.builder;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Sets;

import skyglass.composer.query.request.ColumnId;
import skyglass.composer.query.request.ColumnType;
import skyglass.composer.query.request.ColumnVO;
import skyglass.composer.query.request.CustomGetRowsRequest;
import skyglass.composer.query.response.CustomGetRowsResponse;
import skyglass.composer.query.response.CustomPivotValuesResponse;

public class CustomResponseBuilder {

	public static CustomGetRowsResponse createResponse(
			CustomGetRowsRequest request,
			int totalCount,
			List<Map<String, String>> rows,
			Map<ColumnId, List<String>> pivotValues,
			Map<ColumnId, ColumnType> columnFormats) {

		List<ColumnVO> valueColumns = request.getValueCols();

		return new CustomGetRowsResponse(rows, totalCount, getSecondaryColumns(pivotValues, valueColumns), columnFormats);
	}

	public static CustomPivotValuesResponse createPivotValuesResponse(List<String> rows, int totalCount) {
		return new CustomPivotValuesResponse(rows, totalCount);
	}

	private static List<String> getSecondaryColumns(Map<ColumnId, List<String>> pivotValues, List<ColumnVO> valueColumns) {

		// create pairs of pivot col and pivot value i.e. (DEALTYPE,Financial), (BIDTYPE,Sell)...
		List<Set<Pair<String, String>>> pivotPairs = pivotValues.entrySet().stream()
				.map(e -> e.getValue().stream()
						.map(pivotValue -> Pair.of(e.getKey().getAlias(), pivotValue))
						.collect(toCollection(LinkedHashSet::new)))
				.collect(toList());

		// create cartesian product of pivot and value columns i.e. Financial_Sell_CURRENTVALUE, Physical_Buy_CURRENTVALUE...
		return Sets.cartesianProduct(pivotPairs)
				.stream()
				.flatMap(pairs -> {
					// collect pivot cols, i.e. Financial_Sell
					String pivotCol = pairs.stream()
							.map(Pair::getRight)
							.collect(joining("_"));

					// append value cols, i.e. Financial_Sell_CURRENTVALUE, Financial_Sell_PREVIOUSVALUE
					return valueColumns.stream()
							.map(valueCol -> pivotCol + "_" + valueCol.getId().getAlias());
				})
				.collect(toList());
	}
}
