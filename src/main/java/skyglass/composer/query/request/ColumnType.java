package skyglass.composer.query.request;

import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

public enum ColumnType {
	Integer {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "0");
		}
	},
	Double {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "0.0");
		}
	},
	Money {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "0.00");
		}
	},
	DoubleInt {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> java.lang.Long.toString(
					java.lang.Double.valueOf(value.toString()).longValue()), "0");
		}
	},
	String {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	},
	Boolean {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	},
	Date {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	},
	DateTime {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	};

	public abstract String format(Object value, QueryContext queryContext);

	private static String safe(Supplier<String> supplier, String defaultValue) {
		try {
			String result = supplier.get();
			return StringUtils.isBlank(result) ? defaultValue : result;
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
