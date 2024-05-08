package com.avensys.rts.jobservice.interceptor;

import com.avensys.rts.jobservice.util.JwtUtil;

import com.avensys.rts.jobservice.util.RequestCtxUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class JwtTokenInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		requestTemplate.header("Authorization", "Bearer " + JwtUtil.getTokenFromContext());
		String audit = RequestCtxUtil.getHeaderAttributes("Audit");
		if (audit != null) {
			requestTemplate.header("Audit", audit);
		}
	}
}
