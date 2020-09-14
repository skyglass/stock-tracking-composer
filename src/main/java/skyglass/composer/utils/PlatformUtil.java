package skyglass.composer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.stock.domain.dto.ExtUserDTO;
import skyglass.composer.stock.domain.dto.ExtUserNameDTO;

public class PlatformUtil {
	private static final Logger log = LoggerFactory.getLogger(PlatformUtil.class);

	public static final String USERNAME_PROP_NAME = "username";

	private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

	private static final String LIBRARY_NAME = "commons";

	public static boolean isLocalDevelopment(HttpServletRequest request) {
		return isLocalDevelopment(request.getRequestURL().toString());
	}

	public static boolean isLocalDevelopment(String url) {
		String patternString = ".*http://localhost.*";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

	public static ExtUserDTO createDummyUser(String name) {
		ExtUserDTO dto = new ExtUserDTO();
		ExtUserNameDTO nameDTO = new ExtUserNameDTO(name);
		nameDTO.setGivenName(name);
		dto.setName(nameDTO);
		dto.setId(name);
		return dto;
	}

	public static void setUsernameInThreadLocal(String username) {
		threadLocal.set(username);
	}

	public static String getUsernameFromThreadLocal() {
		return threadLocal.get();
	}

	public static String getUsernameFromCtx() {
		String username = null;

		try {
			username = PlatformUtil.getUsernameFromThreadLocal();
		} catch (Throwable ex) {
			log.error("Could not get user info", ex);
		}

		return username != null ? username.toUpperCase() : null;
	}

}
