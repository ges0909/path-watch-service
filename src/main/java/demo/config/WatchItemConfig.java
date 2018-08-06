package demo.config;

import java.nio.file.Path;

import javax.validation.constraints.NotBlank;

public class WatchItemConfig {

	@NotBlank
	Path path;

	@NotBlank
	String format;

	public Path getPath() {
		return this.path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
