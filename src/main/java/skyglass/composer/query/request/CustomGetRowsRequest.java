package skyglass.composer.query.request;

import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class CustomGetRowsRequest implements Serializable {

	private static final long serialVersionUID = -2102694861611712393L;

	private int startRow, endRow;

	// row group columns
	private List<ColumnId> rowGroupCols;

	// value columns
	private List<ColumnVO> valueCols;

	// pivot columns
	private List<ColumnId> pivotCols;

	// true if pivot mode is one, otherwise false
	private boolean pivotMode;

	// what groups the user is viewing
	private List<String> groupKeys;

	// if filtering, what the filter model is
	private List<FilterModel> filterModel;

	// if sorting, what the sort model is
	private List<SortModel> sortModel;

	public CustomGetRowsRequest() {
		this.rowGroupCols = emptyList();
		this.valueCols = emptyList();
		this.pivotCols = emptyList();
		this.groupKeys = emptyList();
		this.filterModel = emptyList();
		this.sortModel = emptyList();
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public List<ColumnId> getRowGroupCols() {
		return rowGroupCols;
	}

	public void setRowGroupCols(List<ColumnId> rowGroupCols) {
		this.rowGroupCols = rowGroupCols;
	}

	public List<ColumnVO> getValueCols() {
		return valueCols;
	}

	public void setValueCols(List<ColumnVO> valueCols) {
		this.valueCols = valueCols;
	}

	public List<ColumnId> getPivotCols() {
		return pivotCols;
	}

	public void setPivotCols(List<ColumnId> pivotCols) {
		this.pivotCols = pivotCols;
	}

	public boolean isPivotMode() {
		return pivotMode;
	}

	public void setPivotMode(boolean pivotMode) {
		this.pivotMode = pivotMode;
	}

	public List<String> getGroupKeys() {
		return groupKeys;
	}

	public void setGroupKeys(List<String> groupKeys) {
		this.groupKeys = groupKeys;
	}

	public List<FilterModel> getFilterModel() {
		return filterModel;
	}

	public void setFilterModel(List<FilterModel> filterModel) {
		this.filterModel = filterModel;
	}

	public List<SortModel> getSortModel() {
		return sortModel;
	}

	public void setSortModel(List<SortModel> sortModel) {
		this.sortModel = sortModel;
	}

	public static CustomGetRowsRequest getPivotValuesRequest(CustomGetRowsRequest original) {
		return getPivotValuesRequest(original, null, original.getStartRow(), original.getEndRow());
	}

	public static CustomGetRowsRequest getPivotValuesRequest(CustomGetRowsRequest original, ColumnId pivotCol) {
		return getPivotValuesRequest(original, pivotCol, 0, -1);
	}

	public static CustomGetRowsRequest getPivotValuesRequest(CustomGetRowsRequest original, ColumnId pivotCol, int startRow, int endRow) {
		CustomGetRowsRequest request = new CustomGetRowsRequest();
		request.rowGroupCols = emptyList();
		if (pivotCol == null) {
			pivotCol = CollectionUtils.isEmpty(original.getPivotCols()) ? null : original.getPivotCols().get(0);
		}
		request.valueCols = emptyList();
		request.pivotCols = pivotCol == null ? emptyList() : Collections.singletonList(pivotCol);
		request.groupKeys = emptyList();
		request.filterModel = original.getFilterModel();
		request.sortModel = emptyList();
		request.startRow = startRow;
		request.endRow = endRow;
		return request;
	}
}
