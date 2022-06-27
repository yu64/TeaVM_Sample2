package test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContentObserver {
    
    private Path storage;
    private WatchRegistry wr = new WatchRegistry();
    private Set<Path> contents = new HashSet<>();

    public ContentObserver(Path storage)
    {
        this.wr.registerAll(this.storage);
    }

    public Set<Path> getFiles()
    {
        this.update();
        return Collections.unmodifiableSet(new HashSet<>(this.contents));
    }

    public void update()
    {
        WatchKey key;
        while( (key = this.wr.poll()) != null)
        {
            Path path = this.wr.getPath(key);

            WatchEvent<?> last = null;
            for(WatchEvent<?> e : key.pollEvents())
            {
                last = e;
            }

            if(last == null)
            {
                break;
            }
            
            if(last == StandardWatchEventKinds.ENTRY_CREATE)
            {
                this.contents.add(path);
            }
            else if(last == StandardWatchEventKinds.ENTRY_MODIFY)
            {
                this.contents.add(path);
            }
            else if(last == StandardWatchEventKinds.ENTRY_DELETE)
            {
                this.contents.remove(path);
            }
        }
    }
}
