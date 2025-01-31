package com.avensys.rts.jobservice.payload;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EmailMultiTemplateRequestDTO {
	private String[] to;
	private String[] Bcc;
	private String[] Cc;
	private String subject;
	private String content;
	private String category;
	private String subCategory;
	private String templateName;
	private Map<String, String> templateMap;
//	private MultipartFile[] attachments;
}
