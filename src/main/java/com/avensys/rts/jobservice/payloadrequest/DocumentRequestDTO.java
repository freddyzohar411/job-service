package com.avensys.rts.jobservice.payloadrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * author: Koh He Xiang
 * This is the DTO class for the document request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentRequestDTO {
    private String type;
    private String title;
    private String description;
    private Integer entityId;
    private String entityType;
    MultipartFile file;
}
