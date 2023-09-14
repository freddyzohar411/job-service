package com.avensys.rts.jobservice.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.avensys.rts.jobservice.payloadrequest.DocumentRequestDTO;
import com.avensys.rts.jobservice.payloadresponse.HttpResponse;


/**
 * author Koh He Xiang
 * This class is an interface to interact with document microservice
 */
@Configuration
@FeignClient(name = "document-service", url = "http://localhost:8500")
public interface DocumentAPIClient {
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    HttpResponse createDocument(@ModelAttribute DocumentRequestDTO documentRequest);

    @PutMapping(value = "/documents" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    HttpResponse updateDocument(@ModelAttribute DocumentRequestDTO documentRequest);

//    @DeleteMapping("/documents")
//    HttpResponse deleteDocumentByEntityIdAndType(@RequestBody DocumentDeleteRequestDTO documentDeleteRequestDTO);
//
//    @GetMapping("/documents")
//    HttpResponse getDocumentByEntityTypeAndId(@RequestParam String entityType, @RequestParam int entityId);

}
