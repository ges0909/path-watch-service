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

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.config.PathConfig;
import demo.config.PathWatchServiceConfig;
import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class PathWatchService {

  @Async
  public void start(PathWatchServiceConfig config) throws IOException, InterruptedException {
    WatchService watchService = FileSystems.getDefault().newWatchService();
    for (PathConfig c : config.getFolders()) {
      Path path = Paths.get(c.getPath());
      path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE, OVERFLOW);
    }
    WatchKey key;
    while ((key = watchService.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();
        if (kind == OVERFLOW) {
          continue;
        }
        WatchEvent<Path> ev = cast(event);
        Path filename = ev.context();
        if (kind == ENTRY_CREATE) {
          log.info("CREATE: " + filename);
        } else if (kind == ENTRY_MODIFY) {
          log.info("MODIFY: " + filename);
        } else if (kind == ENTRY_DELETE) {
          log.info("DELETE: " + filename);
        }
      }
      if (!key.reset()) {
        break;
      }
    }
  }

  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }
}
