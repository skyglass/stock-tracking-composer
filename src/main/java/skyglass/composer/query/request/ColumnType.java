package skyglass.composer.query.request;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Supplier;

public enum ColumnType {
	Integer(Integer.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "0");
		}
	},
	Double(Double.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "0.0");
		}
	},
	Money(BigDecimal.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "0.00");
		}
	},
	DoubleInt(Integer.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> java.lang.Long.toString(
					java.lang.Double.valueOf(value.toString()).longValue()), "0");
		}
	},
	String(String.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	},
	Boolean(Boolean.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	},
	Date(java.util.Date.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	},
	DateTime(java.util.Date.class) {
		public String format(Object value, QueryContext queryContext) {
			return safe(() -> value.toString(), "");
		}
	};

	private Class<?> javaClass;

	private ColumnType(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	public abstract String format(Object value, QueryContext queryContext);

	public Class<?> getJavaClass() {
		return javaClass;
	}

	private static String safe(Supplier<String> supplier, String defaultValue) {
		try {
			String result = supplier.get();
			return StringUtils.isBlank(result) ? defaultValue : result;
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
