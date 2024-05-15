package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateDocumentListKeyDTO {
	private String entityType;
	private int entityId;
//	private DocumentKeyRequestDTO[] documentKeyRequestDTO;
	private String[] fileKeys;
	private MultipartFile[] files;
}
