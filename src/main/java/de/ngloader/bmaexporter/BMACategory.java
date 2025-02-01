package de.ngloader.bmaexporter;

import java.util.Arrays;

public record BMACategory(int id, String name, BMACategorySub[] modules) {

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "BMACategory [id=" + id + ", name=" + name + ", modules=" + Arrays.toString(modules) + "]";
	}

	public static class Builder {

		private int id;
		private String name;
		private BMACategorySub[] modules;

		private Builder() {
		}

		public Builder setId(int id) {
			this.id = id;
			return this;
		}

		public int getId() {
			return this.id;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setModules(BMACategorySub[] modules) {
			this.modules = modules;
			return this;
		}

		public Builder addModule(BMACategorySub module) {
			if (this.modules == null) {
				this.modules = new BMACategorySub[] { module };
			} else {
				this.modules = Arrays.copyOf(modules, modules.length + 1);
				this.modules[this.modules.length - 1] = module;
			}
			return this;
		}

		public BMACategory build() {
			return new BMACategory(this.id, this.name, this.modules);
		}
	}
}
