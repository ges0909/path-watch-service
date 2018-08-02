package demo;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class PathWatchService {

  @Value("${watch.path}")
  String watchPath;

  @Async
  public void start() {
    try {
      WatchService watchService = FileSystems.getDefault().newWatchService();
      Path path = Paths.get(this.watchPath);
      path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
          StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);
      WatchKey key;
      while ((key = watchService.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
          Path _path = ((WatchEvent<Path>) event).context();
          WatchEvent.Kind kind = event.kind();
          if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            log.info("ENTRY_CREATE: " + _path.toString());
          } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            log.info("ENTRY_MODIFY: " + _path.toString());
          } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            log.info("ENTRY_DELETE: " + _path.toString());
          } else if (kind == StandardWatchEventKinds.OVERFLOW) {
            log.info("OVERFLOW: " + _path.toString());
          }
        }
        key.reset();
      }
    } catch (IOException | InterruptedException e) {
    }
  }
}
