package demo;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.config.WatchItemConfig;
import demo.config.WatchServiceConfig;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Observes a list of directories to detect the creation of files in it.
 *
 * @author
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
    for (WatchItemConfig config : watchServiceConfig.getFolders()) {
      config.getPath().register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
    }
    WatchKey key;
    while ((key = watchService.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();
        if (kind == OVERFLOW) {
          continue;
        }
        Watchable watchable = key.watchable();
        Path head = (Path) watchable; // observed path
        WatchEvent<Path> ev = cast(event);
        Path tail = ev.context(); // name of file created/modified/deleted
        Path file = head.resolve(tail);
        Optional<WatchItemConfig> config = watchServiceConfig.findByPath(head);
        if (!config.isPresent()) {
          log.error("dir to observe not found in internal config: " + head);
        }
        if (kind == ENTRY_CREATE) {
          log.info("CREATE: " + file);
          consolidate(file, config.get().getFormat());
        } else if (kind == ENTRY_MODIFY) {
          log.info("MODIFY: " + file);
        } else if (kind == ENTRY_DELETE) {
          log.info("DELETE: " + file);
        }
      }
      if (!key.reset()) {
        break;
      }
    }

  }

  void consolidate(Path file, WatchItemConfig.Format format) {

  }

  /**
   * Helper method to handle cast mismatch.
   */
  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }
}
