package demo.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("watch")
public class WatchServiceConfig {

	@NotEmpty
	List<WatchItemConfig> configs;

	public List<WatchItemConfig> getFolders() {
		return this.configs;
	}

	public void setFolders(List<WatchItemConfig> folders) {
		this.configs = folders;
	}

	private boolean isEqual(String path, WatchItemConfig other) {
		try {
			return other.getPath().toFile().getCanonicalPath().equals(path);
		} catch (IOException e) {
		}
		return false;
	}

	public Optional<WatchItemConfig> findByPath(Path dir) {
		try {
			String path = dir.toFile().getCanonicalPath();
			return configs.stream().filter(c -> isEqual(path, c)).findFirst();
		} catch (IOException e) {
		}
		return Optional.empty();
	}
}
