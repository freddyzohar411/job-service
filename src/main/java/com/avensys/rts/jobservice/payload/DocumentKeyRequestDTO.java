package com.avensys.rts.jobservice.payload;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentKeyRequestDTO {
	private String documentKey;
	private MultipartFile file;
}
