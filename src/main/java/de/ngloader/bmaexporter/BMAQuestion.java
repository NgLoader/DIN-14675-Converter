package de.ngloader.bmaexporter;

import java.util.Arrays;

public record BMAQuestion(int questionId, int id, int block, String category, int points, String question,
		BMAAnswer[] answers) {

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "BMAQuestion [questionId=" + questionId + ", id=" + id + ", block=" + block + ", category=" + category
				+ ", points=" + points + ", question=" + question + ", answers=" + Arrays.toString(answers) + "]";
	}

	public static class Builder {

		private int questionId;
		private int id;
		private int block;
		private String category;
		private int points;
		private String question;
		private BMAAnswer[] answers;

		private Builder() {
		}

		public void setQuestionId(int questionId) {
			this.questionId = questionId;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setBlock(int block) {
			this.block = block;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public void setPoints(int points) {
			this.points = points;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public void setAnswers(BMAAnswer[] answers) {
			this.answers = answers;
		}

		public void addAnswer(BMAAnswer answer) {
			if (this.answers == null) {
				this.answers = new BMAAnswer[] { answer };
			} else {
				this.answers = Arrays.copyOf(answers, answers.length + 1);
				this.answers[this.answers.length - 1] = answer;
			}
		}

		public BMAQuestion build() {
			return new BMAQuestion(this.questionId, this.id, this.block, this.category, this.points, this.question,
					this.answers);
		}
	}
}
