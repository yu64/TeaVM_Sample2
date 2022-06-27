package test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

public class WatchRegistry implements AutoCloseable{

	private final Kind<?>[] events = {

			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.ENTRY_MODIFY
		};

	private final Map<Path, WatchKey> keyMap = new HashMap<>();
	private final Map<WatchKey, Path> pathMap = new HashMap<>();

	private final WatchService ws;

	public WatchRegistry()
	{
		try
		{
			this.ws = FileSystems.getDefault().newWatchService();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public WatchKey poll()
	{
		return this.ws.poll();
	}

	public Path getPath(WatchKey key)
	{
		return this.pathMap.get(key);
	}

	public void registerAll(Path src)
	{
		if(!Files.isDirectory(src))
		{
			throw new RuntimeException("対象がディレクトリではありません。: " + src);
		}

		try
		{
			Files.walk(src)
				.filter(Files::isDirectory)
				.forEach(this::register);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void register(Path p)
	{
		if(!Files.isDirectory(p))
		{
			throw new RuntimeException("対象がディレクトリではありません。: " + p);
		}

		this.unregister(p);

		try
		{
			WatchKey key = p.register(this.ws, this.events);

			this.pathMap.put(key, p);
			this.keyMap.put(p, key);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void unregisterAll(Path src)
	{
		if(!Files.isDirectory(src))
		{
			throw new RuntimeException("対象がディレクトリではありません。: " + src);
		}

		try
		{
			Files.walk(src)
				.filter(Files::isDirectory)
				.forEach(this::unregister);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void unregister(Path p)
	{
		if(!Files.isDirectory(p))
		{
			throw new RuntimeException("対象がディレクトリではありません。: " + p);
		}

		WatchKey key = this.keyMap.remove(p);
		if(key == null)
		{
			return;
		}

		this.pathMap.remove(key);
		key.cancel();
	}

	@Override
	public void close()
	{
		try
		{
			this.ws.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}



}