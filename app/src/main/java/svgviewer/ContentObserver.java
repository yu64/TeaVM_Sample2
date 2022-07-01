package svgviewer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentObserver implements AutoCloseable{
    
    private Path storage;
    private WatchRegistry wr = new WatchRegistry();

    private Set<Path> contents = new HashSet<>();

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
        return this.getFiles(true);
    }

    public Set<Path> getFiles(boolean canUpdate)
    {
        if(canUpdate)
        {
            this.update();
        }

        return new HashSet<>(this.contents);
    }

    public Path getHeadFile()
    {
        return this.getHeadFile(true);
    }

    public Path getHeadFile(boolean canUpdate)
    {
        if(canUpdate)
        {
            this.update();
        }

        if(this.contents.isEmpty())
        {
            return null;
        }

        return this.contents.iterator().next();
    }

    public boolean isEmpty()
    {
        return this.isEmpty(true);
    }

    public boolean isEmpty(boolean canUpdate)
    {
        if(canUpdate)
        {
            this.update();
        }

        return this.contents.isEmpty();
    }


    public boolean update()
    {
        boolean out = false;
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
                    this.add(path);
                    out = true;
                }
                else if(e.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                {
                    this.add(path);
                    out = true;
                }
                else if(e.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                {
                    this.remove(path);
                    out = true;
                }
            }

        }

        return out;
    }

    private void add(Path path)
    {
        this.contents.add(path);
    }

    private void remove(Path path)
    {
        this.contents.remove(path);
    }

    @Override
    public void close()
    {
        this.wr.close();
    }
}
