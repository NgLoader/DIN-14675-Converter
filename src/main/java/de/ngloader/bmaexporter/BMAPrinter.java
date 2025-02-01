package de.ngloader.bmaexporter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class BMAPrinter {

	private static String castString(String input) {
	   return StringEscapeUtils.escapeHtml4(input)
			   .replaceAll("(?<=\\S)\n", "<br>")
               .replaceAll("\r\n", "<br>");
	}

	public BMAPrinter(BMAExporter exporter) {
		JSONArray list = new JSONArray();
		
		int index = 0;
		int seperator = 0;
		for (BMAQuestion question : exporter.getQuestions()) {
			if (index == 50) {
				index = 0;
				seperator++;
			}
			index++;

			BMACategory category = exporter.getCategories().stream()
					.filter(id -> Integer.valueOf(question.category().split("\\.")[0]) == id.id())
					.findFirst()
					.get();
			list.put(toJson(question, category, String.format(
					"%d. Fragen Block %d-%d",
					seperator + 1,
					(seperator * 50) + 1,
					(seperator * 50) + 50)));
		}

		try (FileWriter fileWriter = new FileWriter(Path.of(BMAExporter.FILE_EXPORT_NAME).toFile())) {
			fileWriter.write(list.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JSONObject toJson(BMAQuestion question, BMACategory category, String splitCategory) {
		JSONObject json = new JSONObject();
		json.put("question", String.format("""
				<div>ID: <strong>%d</strong></div>
				<br/>
				<div>Punkte: <strong>%d</strong></div>
				<br/>
				<br/>
				<div>Frage: (ID: %d, Block: %d)</div>
				<br/>
				<div>%s</div>
				""",
				question.questionId(),
				question.points(),
				question.id(),
				question.block(),
				castString(question.question())));

		JSONArray mchoice = new JSONArray();
		for (BMAAnswer answer : question.answers()) {
			JSONObject jsonAnswer = new JSONObject();
			jsonAnswer.put("answerNbr", answer.id());
			jsonAnswer.put("answer", "<ul><li>" + castString(answer.answer()) + "</li></ul>");
			jsonAnswer.put("correct", false);
			mchoice.put(jsonAnswer);
		}
		json.put("mchoice", mchoice);

		JSONArray categories = new JSONArray();
		categories.put(castString(category.name()));
		categories.put(castString(splitCategory));
		json.put("categories", categories);
		
		return json;
	}
}