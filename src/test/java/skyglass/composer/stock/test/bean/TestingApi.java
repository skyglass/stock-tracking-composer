package skyglass.composer.stock.test.bean;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestingApi {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void executeScript(File file) throws IOException {
		String sqlScript = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		jdbcTemplate.execute(sqlScript);
	}

	public void executeString(String sql) {
		jdbcTemplate.execute(sql);
	}

	public boolean checkH2Database() throws IllegalStateException, UnsupportedOperationException {
		try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			return StringUtils.containsIgnoreCase(metaData.getDriverName(), "h2");
		} catch (SQLException ex) {
			throw new IllegalStateException("Could not establish database connection", ex);
		}
	}
}
