package com.avensys.rts.jobservice.interceptor;

import com.avensys.rts.jobservice.util.JwtUtil;

import com.avensys.rts.jobservice.util.RequestCtxUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		requestTemplate.header("Authorization", "Bearer " + JwtUtil.getTokenFromContext());
		String audit = RequestCtxUtil.getHeaderAttributes("Audit");
		if (audit != null) {
			requestTemplate.header("Audit", audit);
		}
	}

//	@Bean
//	public Encoder feignFormEncoder() {
//		return new SpringFormEncoder();
//	}
}
