package demo.config;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("watch")
public class PathWatchServiceConfig {

	@NotEmpty
	List<PathConfig> folders;

	public List<PathConfig> getFolders() {
		return this.folders;
	}

	public void setFolders(List<PathConfig> folders) {
		this.folders = folders;
	}
}
