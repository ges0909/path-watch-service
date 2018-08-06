package demo.config;

import java.nio.file.Path;

import javax.validation.constraints.NotBlank;

public class WatchItemConfig {

	public enum Format {
		APACHE_ERROR, APACHE_COMBINED;
	}

	@NotBlank
	Path path;

	@NotBlank
	Format format;

	public Path getPath() {
		return this.path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Format getFormat() {
		return this.format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}
}
