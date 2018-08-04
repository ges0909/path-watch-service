package demo;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.config.WatchDirConfig;
import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
@ConfigurationProperties("watch")
public class PathWatchService {

  @NotEmpty
  List<WatchDirConfig> directories;

	public void setDirectories(List<WatchDirConfig> directories) {
		this.directories = directories;
	}

  @Async
  public void start() {
    try {
      WatchService watchService = FileSystems.getDefault().newWatchService();
      for (WatchDirConfig dir : directories) {
        Path path = Paths.get(dir.getPath());
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
          StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);
      }
      WatchKey key;
      while ((key = watchService.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
          Path dir = (Path) key.watchable();
          Path fullPath = dir.resolve((Path) event.context());
          WatchEvent.Kind kind = event.kind();
          if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            log.info("CREATE: " + fullPath.getFileName());
          } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            log.info("MODIFY: " + fullPath.getFileName());
          } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            log.info("DELETE: " + fullPath.getFileName());
          } else if (kind == StandardWatchEventKinds.OVERFLOW) {
            log.info("OVERFLOW: " + fullPath.getFileName());
          }
        }
        key.reset();
      }
    } catch (IOException | InterruptedException e) {
    }
  }
}
