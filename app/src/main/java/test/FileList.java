package test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.nio.file.WatchEvent.Kind;

public class FileList {
    
    private Path storage;
    private WatchRegistry wr = new WatchRegistry();

    public FileList(Path storage)
    {
        this.wr.registerAll(this.storage);
    }

    public Path getResources()
    {

    }
}
