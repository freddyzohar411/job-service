package com.avensys.rts.jobservice.payloadrequest;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    private AccountInformation accountInformation;
    private JobOpeningInformation jobOpeningInformation;
    private JobCommercials jobCommercials;
    private String jobRemarks;
    
    @NotNull(message = "File cannot be null")
//    @ValidPdfFile(message = "File must be a PDF file")
//    @FileSize(maxSize = 1, message = "File size must be less than 1MB")
    private MultipartFile uploadJobDocuments;
}
