package de.ngloader.bmaexporter;

public record BMACategorySub(int id, String name) {

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "BMACategorySub [id=" + id + ", name=" + name + "]";
	}

	public static class Builder {

		private int id;
		private String name;

		private Builder() {
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BMACategorySub build() {
			return new BMACategorySub(this.id, this.name);
		}
	}
}
