package svgviewer.teavm;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.function.Consumer;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.History;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;

public class TeavmMain {

	private static String nowPath = null;

	public static void main(String[] args) throws Exception
	{
		TeavmMain.init();
	}

	public static void init() throws MalformedURLException
	{
		final Window window = Window.current();
		final HTMLDocument doc = window.getDocument();

		final HTMLElement left = doc.querySelector("#left");
		final HTMLElement right = doc.querySelector("#right");

		final Map<String, String> query = Util.getMapFromQuery(window.getLocation());
		
		TeavmMain.nowPath = query.get("path");
		TeavmMain.updateUrl(TeavmMain.nowPath);

		left.addEventListener("click", TeavmMain::next);
		right.addEventListener("click", TeavmMain::prev);

	}

	public static void updateUrl(String path)
	{
		final Window window = Window.current();
		final HTMLDocument doc = window.getDocument();
		final String title = doc.getTitle();
		final History history = History.current();
		final HTMLImageElement img = (HTMLImageElement) doc.querySelector("#center img");

		System.out.println("newPath: " + path);
		TeavmMain.nowPath = path;
		String query = (path == null ? "" : "?path=" + path);
		
		//URL差し替え
		history.replaceState(null, title, "/index.html" + query);

		//画像差し替え
		img.setSrc("/storage" + query);
	}

	public static void next(Event e)
	{
		TeavmMain.fetchNextPath(TeavmMain.nowPath, TeavmMain::updateUrl);
	}

	public static void prev(Event e)
	{
		TeavmMain.fetchPrevPath(TeavmMain.nowPath, TeavmMain::updateUrl);
	}

	private static void fetchNextPath(String nowPath, Consumer<String> callback)
	{
		String url = "/next" + (nowPath == null ? "" : "?path=" + nowPath);
		TeavmMain.fetch(url, req -> callback.accept(req.getResponseText()));
	}

	private static void fetchPrevPath(String nowPath, Consumer<String> callback)
	{
		String url = "/prev" + (nowPath == null ? "" : "?path=" + nowPath);
		TeavmMain.fetch(url, req -> callback.accept(req.getResponseText()));
	}

	private static void fetch(String url, Consumer<XMLHttpRequest> callback)
	{
		XMLHttpRequest req = XMLHttpRequest.create();
		req.open("GET", url);
		req.onComplete(() -> {

			callback.accept(req);
		});

		req.send();
	}



}
