package demo.config;

import javax.validation.constraints.NotBlank;

public class PathConfig {

	@NotBlank
	String path;

	@NotBlank
	String format;

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
