package svgviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Base64.Encoder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import flak.Query;
import flak.Request;
import flak.annotations.Route;

public class Handler {

    private final String RESOURCES_CURRENT = "svgviewer/resources";

    private ContentObserver resources;
    private TemplateEngine engine;

    private TimeoutTimer timeout = new TimeoutTimer(1000 * 3);

    public Handler(Path storage) throws IOException
    {
        this.resources = new ContentObserver(storage);

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix(this.RESOURCES_CURRENT + "/");
        resolver.setSuffix("");

        this.engine = new TemplateEngine();
        this.engine.setTemplateResolver(resolver);
    }

// ================================================================================
    
    @Route("/")
	public String handle()
	{
        return this.handleIndex();
    }

    @Route("/index.html")
	public String handleIndex()
	{
        Context context = new Context();
        context.setVariable("pathTitle", this.resources.getHeadFile().toString());
        return this.engine.process("index.html", context);
    }

    @Route("/app.js")
    public String handleAppJs()
    {
        return Util.readResource(this.RESOURCES_CURRENT + "/app.js");
    }

    @Route("/start.js")
    public String handleStartJs()
    {
        Context context = new Context();
        return this.engine.process("start.js", context);
    }

    @Route("/style.css")
    public String handleCss()
    {
        return Util.readResource(this.RESOURCES_CURRENT + "/style.css");
    }

// ================================================================================

    @Route("/storage")
    public InputStream handleResource(Request req) throws IOException
    {
        if(this.resources.isEmpty())
        {
            return null;
        }

        Query q = req.getQuery();
        Path path = Util.convart(q.get("path"), Path::of, this.resources.getHeadFile());

        if(path.toString().endsWith(".svg"))
        {
            req.getResponse().addHeader("Content-type", "image/svg+xml");
        }

        return Files.newInputStream(this.resources.getStorage().resolve(path));
    }

    @Route("/next")
    public String handleNext(Request req)
    {
        req.getResponse().addHeader("Content-Type", "text/plain");
        return this.getOneSidePath(true, req.getQuery().get("path"));
    }

    @Route("/prev")
    public String handlePrev(Request req)
    {
        req.getResponse().addHeader("Content-Type", "text/plain");
        return this.getOneSidePath(false, req.getQuery().get("path"));
    }

    public String getOneSidePath(boolean rightElseLeft, String path)
    {
        if(this.resources.isEmpty())
        {
            return null;
        }

        path = Util.checkNull(path, this.resources.getHeadFile().toString());

        List<String> paths = this.resources.getFiles().stream()
            .map(Path::toString)
            .sorted()
            .collect(Collectors.toList());

        Map<String, Integer> numbers = IntStream.range(0, paths.size())
            .boxed()
            .collect(Collectors.toMap(i -> paths.get(i), i -> i));

        Integer index = numbers.get(path);
        if(index == null)
        {
            return this.resources.getHeadFile().toString();
        }

        int next = Util.updateCount(rightElseLeft, index, 0, paths.size() - 1);
        String nextPath = paths.get(next);
        
        return nextPath;
    }

    @Route("/all")
    public String all(Request req)
    {
        return this.resources.getFiles().stream()
            .map(Path::toString)
            .sorted()
            .collect(Collectors.joining(","));
    }

    @Route("/open")
    public String open(Request req)
    {
        Query q = req.getQuery();
        String pathText = q.get("storage");
        if(pathText == null)
        {
            req.getResponse().setStatus(400);
            return "[ERROR] use query param 'storage=<TargetDirPath>'.";
        }

        Path path = Path.of(pathText);
        if(!Files.exists(path))
        {
            req.getResponse().setStatus(400);
            return "[ERROR] not found '" + pathText + "'.";
        }

        this.resources.close();
        try
        {
            this.resources = new ContentObserver(path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        req.getResponse().redirect("/index.html");
        return null;
    }

    @Route("/keep_alive")
    public String updateTimeout(Request req) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        this.timeout.reset();
        return "Timeout extension.";
    }

    @Route("/stop")
	public void stop()
	{
        System.exit(0);
    }


// ================================================================================
 

    private static class TimeoutTimer {

        private Timer timer = null;
        private int timeout;

        public TimeoutTimer(int timeout)
        {
            this.timeout = timeout;
        }

        public void reset()
        {
            if(this.timer != null)
            {
                this.timer.cancel();
                this.timer.purge();
            }

            Timer timer = new Timer();
            timer.schedule(new RunnableTask(() -> {

                System.out.println("Timeout");
                System.exit(0);
            }), this.timeout);
            
            this.timer = timer;
        }
    }

    private static class RunnableTask extends TimerTask {

        private Runnable task;
        public RunnableTask(Runnable task)
        {
            this.task = task;
        }

        @Override
        public void run()
        {
            task.run();
        }

    }
}
