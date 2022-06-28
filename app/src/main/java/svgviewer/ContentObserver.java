package svgviewer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ContentObserver {
    
    private Path storage;
    private WatchRegistry wr = new WatchRegistry();

    private Set<Path> contents = new TreeSet<>(Comparator.comparing(p -> p.toString()));

    public ContentObserver(Path storage) throws IOException
    {
        this.storage = storage;
        this.wr.registerAll(this.storage);

        Set<Path> set = Files.list(this.storage)
            .map(p -> this.storage.relativize(p))
            .collect(Collectors.toSet());
            
        this.contents.addAll(set);
    }

    public Path getStorage()
    {
        return this.storage;
    }

    public Set<Path> getFiles()
    {
        this.update();
        return Collections.unmodifiableSet(new HashSet<>(this.contents));
    }

    public Path getHeadFile()
    {
        this.update();
        return this.contents.iterator().next();
    }

    public boolean isEmpty()
    {
        this.update();
        return this.contents.isEmpty();
    }


    public void update()
    {
        WatchKey key;
        while( (key = this.wr.poll()) != null)
        {
            Path dir = this.wr.getPath(key);
            
            for(WatchEvent<?> e : key.pollEvents())
            {
                Path path = dir.resolve((Path)e.context());
                path = this.storage.relativize(path);

                System.out.println(path);
                if(e.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                {
                    this.contents.add(path);
                }
                else if(e.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                {
                    this.contents.add(path);
                }
                else if(e.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                {
                    this.contents.remove(path);
                }
            }

        }
    }
}
