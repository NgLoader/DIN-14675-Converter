package de.ngloader.bmaexporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class BMAExporter extends PDFTextStripper {

//	public static final String FILE_IMPORT_NAME = "./data/DIN14675_BMA.pdf";
//	public static final String FILE_EXPORT_NAME = "./data/export_BMA.json";
//	public static final String FILE_PAGE_PREFIX = "ARGE DIN 14675 + DIN EN 16763 - Prüfungsfragenkatalog BMA";

	public static final String FILE_IMPORT_NAME = "./data/DIN14675_SAA.pdf";
	public static final String FILE_EXPORT_NAME = "./data/export_SAA.json";
	public static final String FILE_PAGE_PREFIX = "ARGE DIN 14675 - Prüfungsfragenkatalog SAA";

	public static void main(String[] args) throws IOException {
		try (PDDocument document = PDDocument.load(Path.of(FILE_IMPORT_NAME).toFile())) {
			BMAExporter exporter = new BMAExporter();
			exporter.analyze(document);
//			System.out.println("SIZE: " + exporter.getQuestions().size());
//			System.out.println(exporter.getQuestions().get(12).toString());
			System.out.println("SIZE: " + exporter.getCategories().size());
			System.out.println(exporter.getCategories().get(0).toString());
		}
	}

	private List<BMACategory> categories = new ArrayList<>();
	private List<BMACategory.Builder> categoryBuilders = new ArrayList<>();

	private List<BMAQuestion> questions = new ArrayList<>();
	private BMAQuestion.Builder questionBuilder;
	private BMAAnswer.Builder answerBuilder;

	private Queue<Consumer<String>> readNext = new ConcurrentLinkedQueue<>();

	private StringBuilder stringBuilder;

	private boolean readyType = false;
	private boolean yesno = false;

	private BMACategory.Builder categoryBuilder;
	private boolean checkIfNextCategory = false;

	public BMAExporter() throws IOException {
		super();
	}

	public void analyze(PDDocument document) {
		this.setSortByPosition(true);
		this.setStartPage(0);
		this.setEndPage(document.getNumberOfPages());

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				Writer writer = new OutputStreamWriter(byteArrayOutputStream)) {
			this.writeText(document, writer);
			this.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}

		new BMAPrinter(this);
	}

	private void finish() {
		if (this.questionBuilder != null) {
			if (this.answerBuilder != null) {
				this.questionBuilder.addAnswer(this.answerBuilder.build());
			}

			this.questions.add(this.questionBuilder.build());
		}

		this.categories = this.categoryBuilders.stream().map(BMACategory.Builder::build).toList();
	}

	@Override
	protected void writeString(String line, List<TextPosition> textPositions) throws IOException {
		if (line.equals(FILE_PAGE_PREFIX)) {
			this.checkIfNextCategory = true;
			return;
		}

		if (this.checkIfNextCategory) {
			this.checkIfNextCategory = false;
			this.categoryBuilder = null;

			if (line.matches("[0-9]")) {
				int categoryId = Integer.valueOf(line);

				for (BMACategory.Builder category : this.categoryBuilders) {
					if (category.getId() == categoryId) {
						this.categoryBuilder = category;
						break;
					}
				}

				if (this.categoryBuilder == null) {
					this.categoryBuilder = BMACategory.builder();
					this.categoryBuilder.setId(categoryId);

					this.categoryBuilders.add(this.categoryBuilder);
					this.readNext.add((name) -> this.categoryBuilder.setName(name));
				} else {
					this.readNext.add(__ -> {});
				}

				BMACategorySub.Builder subBuilder = BMACategorySub.builder();
				this.readNext.add((subId) -> {
					subBuilder.setId(Integer.valueOf(subId.split("\\.")[1]));
				});
				this.readNext.add((name) -> {
					subBuilder.setName(name);
					this.categoryBuilder.addModule(subBuilder.build());
				});
				return;
			}
		}

		Consumer<String> next = this.readNext.poll();
		if (next != null) {
			next.accept(line);
			return;
		}

		if (line.matches("[0-9][0-9].[0-9][0-9].[0-9][0-9][0-9][0-9]")) {
			if (this.answerBuilder != null) {
				this.answerBuilder.setAnswer(this.stringBuilder.toString());
				this.questionBuilder.addAnswer(this.answerBuilder.build());
				this.questions.add(this.questionBuilder.build());
			}

			this.answerBuilder = null;
			this.questionBuilder = null;
			this.readyType = true;
		}

		switch (line) {
		case "ID:":
			if (this.answerBuilder != null) {
				this.answerBuilder.setAnswer(this.stringBuilder.toString());
				this.questionBuilder.addAnswer(this.answerBuilder.build());
				this.questions.add(this.questionBuilder.build());
			}

			this.questionBuilder = BMAQuestion.builder();
			this.answerBuilder = null;
			this.stringBuilder = null;
			this.readyType = true;

			this.readNext.add((id) -> this.questionBuilder.setId(Integer.valueOf(id)));
			return;
		case "Block:":
			this.readNext.add((id) -> this.questionBuilder.setBlock(Integer.valueOf(id)));
			return;
		case "Kategorie:":
			this.readNext.add((id) -> this.questionBuilder.setCategory(id));
			return;
		case "Punkte:":
			this.readNext.add((id) -> this.questionBuilder.setPoints(Integer.valueOf(id)));
			this.readNext.add((id) -> this.questionBuilder.setQuestionId(Integer.valueOf(id)));

			this.stringBuilder = new StringBuilder();
			return;
		}

		if (this.questionBuilder == null) {
			return;
		}

		if (this.readyType) {
			if (line.equals("q")) {
				this.questionBuilder.setQuestion(this.stringBuilder.toString());
				this.stringBuilder = new StringBuilder();

				this.stringBuilder = new StringBuilder();
				this.answerBuilder = BMAAnswer.builder();
				this.readNext.add((id) -> answerBuilder.setId(Integer.valueOf(id.substring(0, id.length() - 1))));
				this.answerBuilder.setYesno(false);

				this.readyType = false;
				this.yesno = false;
				return;
			} else if (line.matches("[0-9].")) {
				this.questionBuilder.setQuestion(this.stringBuilder.toString());
				this.stringBuilder = new StringBuilder();

				this.stringBuilder = new StringBuilder();
				this.answerBuilder = BMAAnswer.builder();
				this.answerBuilder.setId(1);
				this.answerBuilder.setYesno(true);

				this.readyType = false;
				this.yesno = true;
				return;
			}
		} else {
			if (line.equals("q") && !this.yesno) {
				if (this.answerBuilder != null) {
					this.answerBuilder.setAnswer(this.stringBuilder.toString());
					this.questionBuilder.addAnswer(this.answerBuilder.build());
				}

				this.stringBuilder = new StringBuilder();
				this.answerBuilder = BMAAnswer.builder();
				this.answerBuilder.setYesno(false);
				this.readNext.add((id) -> answerBuilder.setId(Integer.valueOf(id.substring(0, id.length() - 1))));
				return;
			} else if (line.matches("[0-9].") && this.yesno) {
				if (this.answerBuilder != null) {
					this.answerBuilder.setAnswer(this.stringBuilder.toString());
					this.questionBuilder.addAnswer(this.answerBuilder.build());
				}

				this.stringBuilder = new StringBuilder();
				this.answerBuilder = BMAAnswer.builder();
				this.answerBuilder.setYesno(true);
				this.answerBuilder.setId(Integer.valueOf(line.substring(0, line.length() - 1)));
				return;
			}

			if (line.equals("q")) {
				this.readNext.add((id) -> {});
				return;
			}
		}

		if (this.stringBuilder != null) {
			if (!this.stringBuilder.isEmpty()) {
				this.stringBuilder.append("\n");
			}
			this.stringBuilder.append(line);
			return;
		}
	}

	public List<BMAQuestion> getQuestions() {
		return this.questions;
	}

	public List<BMACategory> getCategories() {
		return this.categories;
	}
}