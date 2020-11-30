
package skyglass.composer.utils.query;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.common.model.Language;

/**
 *
 * @author skliarm
 */
public class QueryTranslationUtil {

	public static String getNativeSearchLikeTerm(String fieldName, String parameterName) {
		return getSearchLikeTerm(fieldName, parameterName, true);
	}

	public static String getJpaSearchLikeTerm(String fieldName, String parameterName) {
		return getSearchLikeTerm(fieldName, parameterName, false);
	}

	private static String getSearchLikeTerm(String fieldName, String parameterName, boolean nativeQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append(" (");

		String parameterChar = nativeQuery ? "?" : ":";
		for (Language lang : Language.values()) {
			if (builder.length() > 2) {
				builder.append("OR ");
			}

			String languageColumn = lang.getLanguageCode();
			if (nativeQuery) {
				languageColumn = languageColumn.toUpperCase();
			}

			builder.append("LOWER(").append(fieldName).append(".").append(languageColumn).append(")");
			builder.append(" LIKE ");
			builder.append("LOWER(").append(parameterChar).append(parameterName).append(") ");
		}
		builder.append(") ");
		return builder.toString();
	}

	public static String getNativeSearchLikeTermValueBased(String fieldName, String value) {
		return getSearchLikeTermValueBased(fieldName, value, true);
	}

	public static String getJpaSearchLikeTermValueBased(String fieldName, String value) {
		return getSearchLikeTermValueBased(fieldName, value, false);
	}

	private static String getSearchLikeTermValueBased(String fieldName, String value, boolean nativeQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append(" (");

		for (Language lang : Language.values()) {
			if (builder.length() > 2) {
				builder.append("OR ");
			}

			String languageColumn = lang.getLanguageCode();
			if (nativeQuery) {
				languageColumn = languageColumn.toUpperCase();
			}

			builder.append("LOWER(").append(fieldName).append(".").append(languageColumn).append(")");
			builder.append(" LIKE ");
			builder.append("'%").append(NativeQueryUtil.getEncoded(StringUtils.lowerCase(value))).append("%' ");
		}

		builder.append(") ");

		return builder.toString();
	}

	public static String getNativeSearchEqualsTermValueBased(String fieldName, String value) {
		return getSearchEqualsTermValueBased(fieldName, value, true);
	}

	public static String getJpaSearchEqualsTermValueBased(String fieldName, String value) {
		return getSearchEqualsTermValueBased(fieldName, value, false);
	}

	private static String getSearchEqualsTermValueBased(String fieldName, String value, boolean nativeQuery) {
		StringBuilder builder = new StringBuilder();
		builder.append(" (");

		for (Language lang : Language.values()) {
			if (builder.length() > 2) {
				builder.append("OR ");
			}

			String languageColumn = lang.getLanguageCode();
			if (nativeQuery) {
				languageColumn = languageColumn.toUpperCase();
			}

			builder.append("LOWER(").append(fieldName).append(".").append(languageColumn).append(")");
			builder.append(" = ");
			builder.append("'").append(NativeQueryUtil.getEncoded(StringUtils.lowerCase(value))).append("' ");
		}

		builder.append(") ");

		return builder.toString();
	}

	public static String getJpaSearchEqualsTerm(String fieldName, String parameterName) {
		StringBuilder builder = new StringBuilder();
		builder.append(" (");
		for (Language lang : Language.values()) {
			if (builder.length() > 2) {
				builder.append(" OR ");
			}

			String languageColumn = lang.getLanguageCode();

			builder.append(fieldName).append(".").append(languageColumn);
			builder.append(" = ");
			builder.append(":").append(parameterName);
		}
		builder.append(") ");
		return builder.toString();
	}

	public static String getTranslatedField(String currentLang, String field) {
		return QueryFunctions.coalesce(getTranslatedFields(currentLang, field));
	}

	public static String[] getTranslatedFields(String currentLang, String... fields) {
		currentLang = getCurrentLang(currentLang);
		String[] result = new String[fields.length * Language.values().length];
		int j = 0;
		for (int i = 0; i < fields.length; i++) {
			result[j] = fields[i] + "." + getCurrentLang(currentLang);
			j++;
			for (Language lang : Language.getLanguages(currentLang)) {
				result[j] = fields[i] + "." + lang.getLanguageCode();
				j++;
			}
		}
		return result;
	}

	private static String getCurrentLang(String currentLang) {
		return StringUtils.isNotBlank(currentLang) ? currentLang : getDefaultLang();
	}

	private static String getDefaultLang() {
		return Language.DEFAULT.getLanguageCode();
	}

}
