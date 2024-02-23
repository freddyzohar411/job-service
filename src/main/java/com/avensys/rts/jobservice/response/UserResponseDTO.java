package com.avensys.rts.jobservice.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDTO {
	private Integer id;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String mobile;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean locked;
	private Boolean enabled;

}
