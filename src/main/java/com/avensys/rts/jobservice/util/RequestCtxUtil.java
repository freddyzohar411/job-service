package com.avensys.rts.jobservice.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class RequestCtxUtil {

	public static void addAttribute(String key, Object value) {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		attributes.setAttribute(key, value, RequestAttributes.SCOPE_REQUEST);
	}

	public static Object getAttribute(String key) {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		return attributes.getAttribute(key, RequestAttributes.SCOPE_REQUEST);
	}

	public static void removeAttribute(String key) {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		attributes.removeAttribute(key, RequestAttributes.SCOPE_REQUEST);
	}

	public static String getHeaderAttributes(String key) {
		// Obtain the current RequestAttributes
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

		// Check if RequestAttributes are present
		if (attributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();

			// Return the header value
			return request.getHeader(key);
		}

		// Return null if RequestAttributes are not available
		return null;
	}
}
