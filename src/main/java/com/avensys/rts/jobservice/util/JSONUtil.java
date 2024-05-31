package com.avensys.rts.jobservice.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONUtil {

	public static String mergeJsonObjects(String... jsonObjects) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode mergedNode = objectMapper.createObjectNode();

			for (String jsonObject : jsonObjects) {
				JsonNode jsonNode = objectMapper.readTree(jsonObject);
				mergeJsonNode(mergedNode, jsonNode);
			}

			return objectMapper.writeValueAsString(mergedNode);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonNode mergeJsonNodes(List<JsonNode> jsonNodes) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mergedNode = mapper.createObjectNode();

		for (JsonNode node : jsonNodes) {
			if (node.isObject()) {
				mergedNode.setAll((ObjectNode) node);
			} else {
				throw new IllegalArgumentException("Only JSON objects can be merged.");
			}
		}
		return mergedNode;
	}

	private static void mergeJsonNode(ObjectNode target, JsonNode source) {
		for (Iterator<Map.Entry<String, JsonNode>> it = source.fields(); it.hasNext();) {
			Map.Entry<String, JsonNode> entry = it.next();
			String fieldName = entry.getKey();
			JsonNode sourceValue = entry.getValue();

			JsonNode targetValue = target.get(fieldName);
			if (targetValue != null && targetValue.isObject() && sourceValue.isObject()) {
				// Recursive merge for nested JSON objects
				mergeJsonNode((ObjectNode) targetValue, sourceValue);
			} else {
				// Non-nested merge or overwrite
				target.set(fieldName, sourceValue);
			}
		}
	}

	public static void writeJsonToFile(String filePath, String fileName, JsonNode jsonNode)
			throws IOException, IOException {
		// Ensure the file path ends with a separator
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}

		// Create the file object
		File file = new File(filePath + fileName);

		// Initialize ObjectMapper instance
		ObjectMapper mapper = new ObjectMapper();

		// Writing to a file
		mapper.writeValue(file, jsonNode);
	}

	public static String writeToJsonString(Object obj) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}

	public static JsonNode convertObjectToJsonNode(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(obj);
	}

	public static <T> T convertJsonStringToObject(String jsonString, Class<T> javaClass) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(jsonString, javaClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String extractJson(String text) {
		String regex = "\\{(?:[^{}]*|\\{(?:[^{}]*|\\{[^{}]*\\})*\\})*\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public static String removeTrailingCommas(String json) {
		// Pattern for trailing commas before a closing bracket or brace
		String patternStr = ",\\s*(?=[\\]}])";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(json);
		StringBuffer sb = new StringBuffer();

		// Replace all occurrences of the pattern
		while (matcher.find()) {
			// Replace with the empty string
			matcher.appendReplacement(sb, "");
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	public static String fixJsonCommas(String jsonString) {
		// Specifically target likely missing comma scenarios, focusing on structures
		// Add commas between JSON objects and arrays only if not followed by a comma or closing bracket
		jsonString = jsonString.replaceAll("(\\})([ \\t]*)([\\[\\{])", "$1,$2$3");
		jsonString = jsonString.replaceAll("(\\])([ \\t]*)([\\[\\{])", "$1,$2$3");

		// Add commas between elements of arrays and properties of objects
		// Only if they are followed by quotes indicating a new element or property
		jsonString = jsonString.replaceAll("(\"[ \\t]*)([^,:\\]}]+?)([ \\t]*\")([ \\t]*)([\\{\"\\[])","$1$2$3,$4$5");

		// Correcting common property patterns: avoid adding a comma between a property value and a closing brace/bracket
		jsonString = jsonString.replaceAll("(,)([ \\t]*)([\\]}])", "$2$3");

		// Avoid inserting commas directly before a closing bracket or brace
		jsonString = jsonString.replaceAll("(,)([ \\t]*)([\\]}])", "$2$3");

		return jsonString;
	}

	public static String processJSONString(String response) {

		// Extract JSON object
		String jsonString = extractJson(response);

		// Fix missing commas
		jsonString = fixJsonCommas(jsonString);

		// Remove trailing commas
		jsonString = removeTrailingCommas(jsonString);

		// Return the processed JSON string
		return jsonString;
	}


}
