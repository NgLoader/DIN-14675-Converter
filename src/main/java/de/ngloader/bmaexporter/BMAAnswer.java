package de.ngloader.bmaexporter;

public record BMAAnswer(int id, String answer, boolean yesno) {

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "BMAAnswer [id=" + id + ", answer=" + answer + ", yesno=" + yesno + "]";
	}

	public static class Builder {

		private int id;
		private String answer;
		private boolean yesno;

		private Builder() {
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public void setYesno(boolean yesno) {
			this.yesno = yesno;
		}

		public BMAAnswer build() {
			return new BMAAnswer(this.id, this.answer, this.yesno);
		}
	}
}