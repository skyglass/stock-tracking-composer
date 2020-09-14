package skyglass.composer.stock.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import skyglass.composer.stock.domain.model.IdObject;

public class UnsupportedParameterValueException extends ClientException {
	private static final long serialVersionUID = 1L;

	public UnsupportedParameterValueException(String paramName, Object value) {
		this(paramName, value, null);
	}

	public UnsupportedParameterValueException(String paramName, Object value, Throwable cause) {
		this(null, paramName, null, value, cause);
	}

	public UnsupportedParameterValueException(String paramName, String reason, Object value, Throwable cause) {
		this(null, paramName, reason, value, cause);
	}

	public UnsupportedParameterValueException(Class<? extends IdObject> type, String paramName, Object value) {
		this(type, paramName, null, value, null);
	}

	public UnsupportedParameterValueException(Class<? extends IdObject> type, String paramName, Object value,
			Throwable cause) {
		this(type, paramName, null, value, cause);
	}

	public UnsupportedParameterValueException(Class<? extends IdObject> type, String paramName, String reason,
			Object value, Throwable cause) {
		super(HttpStatus.BAD_REQUEST, buildMessage(type, paramName, reason, value), cause);
	}

	private static String buildMessage(Class<? extends IdObject> type, String paramName, String reason,
			Object value) {
		String message = "Illegal value '" + value + "' for " + paramName;

		String typeName = "";
		if (type != null) {
			typeName = type.getSimpleName();
		}

		if (!StringUtils.isBlank(typeName)) {
			message += " of the " + (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entity" : typeName);
		}

		if (!StringUtils.isBlank(reason)) {
			message += ": " + reason;
		}

		return message;
	}
}
