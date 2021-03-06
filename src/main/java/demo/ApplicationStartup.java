package demo;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import demo.config.WatchItemConfig;
import demo.config.WatchServiceConfig;

@Component
public class ApplicationStartup implements ApplicationRunner {

	@Autowired
	WatchServiceConfig watchServiceConfig;

	@Autowired
	ApplicationWatchService watchService;

	/**
	 * Starts watch-service to observe the configured dirs for files created in
	 * them.
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		createDirsIfNotExist();
		watchService.start(watchServiceConfig);
	}

	/**
	 * Creates non-exsiting dirs.
	 */
	private void createDirsIfNotExist() throws IOException {
		for (WatchItemConfig config : watchServiceConfig.getFolders()) {
			if (!Files.exists(config.getPath())) {
				Files.createDirectories(config.getPath());
			}
		}
	}
}
