package demo.config;

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

	public Optional<WatchItemConfig> findByPath(Path path) {
		return configs.stream().filter(p -> p.getPath().equals(path.toString())).findAny();
	}
}
