package skyglass.composer.query.request;

import java.util.Date;

import skyglass.composer.common.model.Currency;
import skyglass.composer.common.model.Language;
import skyglass.composer.db.DatabaseType;
import skyglass.composer.utils.date.DateUtil;

public class QueryContext {

	private ContextSettings settings;

	private DatabaseType databaseType;

	private String timezone;

	private String offsetSeconds;

	private Language language;

	private Date from;

	private Date to;

	private Currency currency;

	public QueryContext(ContextSettings settings, DatabaseType databaseType,
			String timezone, Language language, Date from, Date to, Currency currency) {
		this.settings = settings;
		this.databaseType = databaseType;
		this.timezone = timezone;
		this.offsetSeconds = DateUtil.getOffsetSeconds(timezone, from);
		this.language = language;
		this.from = from;
		this.to = to;
		this.currency = currency;
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public String getTimezone() {
		return timezone;
	}

	public String getOffsetSeconds() {
		return offsetSeconds;
	}

	public Language getLanguage() {
		return language;
	}

	public String getLanguageCode() {
		return language.getLanguageCode();
	}

	public Date getFrom() {
		return from;
	}

	public Date getTo() {
		return to;
	}

	public Currency getCurrency() {
		return currency != null ? currency : getSettingsCurrency();
	}

	private Currency getSettingsCurrency() {
		return settings == null || settings.getCurrency() == null ? Currency.EUR : settings.getCurrency();
	}

}
