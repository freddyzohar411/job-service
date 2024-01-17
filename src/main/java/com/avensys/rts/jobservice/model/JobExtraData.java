package com.avensys.rts.jobservice.model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobExtraData {

	private Long id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String createdByName;
	private String updatedByName;

	public JobExtraData(JobEntity jobEntity) {
		this.id = jobEntity.getId();
		this.createdAt = jobEntity.getCreatedAt();
		this.updatedAt = jobEntity.getUpdatedAt();
	}

	// Use a Set to store all properties dynamically
	public Set<String> getAllFields() {
		Set<String> fields = new HashSet<>();
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.canAccess(this)) {
				field.setAccessible(true);
			}
			fields.add(field.getName());
		}
		return fields;
	}

	// Get all field in map ( label and value)
	public List<HashMap<String, String>> getAllFieldsMap() {
		return getAllFields().stream().map(field -> {
			return new HashMap<String, String>() {{
				put("label", StringUtil.convertCamelCaseToTitleCase2(field));
				put("value", field);
			}};
		}).collect(Collectors.toList());
	}

	public JsonNode getSelectedFieldsJsonNode() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

		// Use reflection to get all declared fields of AccountExtraData
		Field[] fields = JobExtraData.class.getDeclaredFields();

		for (Field field : fields) {
			try {
				if (!field.canAccess(this)) {
					field.setAccessible(true);
				}

				// Add field to the JSON node
				jsonNode.put(field.getName(), String.valueOf(field.get(this)));
			} catch (IllegalAccessException e) {
				// Handle the exception as needed
				e.printStackTrace();
			}
		}
		return jsonNode;
	}

}
