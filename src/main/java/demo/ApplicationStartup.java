package demo;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import demo.config.PathConfig;
import demo.config.PathWatchServiceConfig;

@Component
public class ApplicationStartup implements ApplicationRunner {

	@Autowired
	PathWatchService pathWatchService;

	@Autowired
	PathWatchServiceConfig pathWatchServiceConfig;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		for (PathConfig c : pathWatchServiceConfig.getFolders()) {
			File file = new File(c.getPath());
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		pathWatchService.start(pathWatchServiceConfig);
	}
}
