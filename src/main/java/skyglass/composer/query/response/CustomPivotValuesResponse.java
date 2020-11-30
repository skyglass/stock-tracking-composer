package skyglass.composer.query.response;

import java.util.List;

public class CustomPivotValuesResponse {

	private List<String> data;

	private int totalCount;

	public CustomPivotValuesResponse(List<String> data, int totalCount) {
		this.data = data;
		this.totalCount = totalCount;
	}

	public List<String> getData() {
		return data;
	}

	public int getTotalCount() {
		return totalCount;
	}

}
