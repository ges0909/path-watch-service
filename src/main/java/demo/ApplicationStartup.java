package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationRunner {

	@Autowired
	PathWatchService pathWatchService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		pathWatchService.start();
	}
}
