package demo;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.config.WatchItemConfig;
import demo.config.WatchServiceConfig;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Observes a list of directories to detect the creation of files in it.
 *
 * @author gerrit.schrader@gmail.com
 */
@Service
@CommonsLog
public class ApplicationWatchService {

  /**
   * Registers paths at Java Watch Service API to observe them in changes like
   * file creation.
   */
  @Async
  public void start(WatchServiceConfig watchServiceConfig) throws IOException, InterruptedException {
    WatchService watchService = FileSystems.getDefault().newWatchService();
    WatchServiceConfig watchServiceConfig2 = watchServiceConfig;
    for (WatchItemConfig config : watchServiceConfig2.getFolders()) {
      Paths.get(config.getPath()).register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
    }
    WatchKey key;
    while ((key = watchService.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();
        if (kind == OVERFLOW) {
          continue;
        }
        WatchEvent<Path> ev = cast(event);
        Path path = ev.context().toAbsolutePath();
        Optional<WatchItemConfig> config = watchServiceConfig2.findByPath(path);
        if (config.isPresent()) {
          log.info("path found: " + path);
        } else {
          log.error("path not found: " + path);
        }
        if (kind == ENTRY_CREATE) {
          log.info("CREATE: " + path);
        } else if (kind == ENTRY_MODIFY) {
          log.info("MODIFY: " + path);
        } else if (kind == ENTRY_DELETE) {
          log.info("DELETE: " + path);
        }
      }
      if (!key.reset()) {
        break;
      }
    }

  }

  /**
   * Helper method to handle cast mismatch.
   */
  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }
}
