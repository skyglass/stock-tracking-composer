package skyglass.composer.query.response;

import java.util.List;
import java.util.Map;

import skyglass.composer.query.request.ColumnId;
import skyglass.composer.query.request.ColumnType;

public class CustomGetRowsResponse {
	private List<Map<String, String>> data;

	private int totalCount;

	private List<String> secondaryColumnFields;

	private Map<ColumnId, ColumnType> columnFormats;

	public CustomGetRowsResponse(List<Map<String, String>> data, int totalCount, List<String> secondaryColumnFields,
			Map<ColumnId, ColumnType> columnFormats) {
		this.data = data;
		this.totalCount = totalCount;
		this.secondaryColumnFields = secondaryColumnFields;
		this.columnFormats = columnFormats;
	}

	public List<Map<String, String>> getData() {
		return data;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public List<String> getSecondaryColumnFields() {
		return secondaryColumnFields;
	}

	public Map<ColumnId, ColumnType> getColumnFormats() {
		return columnFormats;
	}
}
