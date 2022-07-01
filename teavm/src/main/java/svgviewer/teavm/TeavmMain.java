package svgviewer.teavm;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.function.Consumer;

import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.History;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLImageElement;

public class TeavmMain {

	private static final Window WINDOW = Window.current();
	private static final HTMLDocument DOC = TeavmMain.WINDOW.getDocument();
	private static final HTMLElement LEFT = TeavmMain.DOC.querySelector("#left");
	private static final HTMLElement RIGHT = TeavmMain.DOC.querySelector("#right");
	private static final String TITLE = TeavmMain.DOC.getTitle();
	private static final History HISTORY = History.current();
	private static final HTMLImageElement IMG = (HTMLImageElement) TeavmMain.DOC.querySelector("#viewer img");
	private static final HTMLElement VIEWER = TeavmMain.DOC.querySelector("#viewer");
	private static final HTMLElement MAIN = TeavmMain.DOC.querySelector("main");
	private static final HTMLElement PATH_TITLE = TeavmMain.DOC.querySelector("#pathTitle");


	private static String nowPath = null;

	private static MouseEvent clickedEvent = null;
	private static String startTop = "0px";
	private static String startLeft = "0px";

	public static void main(String[] args) throws Exception
	{
		TeavmMain.init();
	}


//======================================================================================

	/**
	 * 初期化
	 */
	public static void init() throws MalformedURLException
	{
		final Map<String, String> query = Util.getMapFromQuery(TeavmMain.WINDOW.getLocation());
		
		//初期のパスを適用
		TeavmMain.nowPath = query.get("path");
		TeavmMain.applyPath(TeavmMain.nowPath);

		//定期的に生存報告(WebSokectを使いたかった)
		Window.setInterval(() -> {

			TeavmMain.fetch("/keep_alive", res -> System.out.println(res.getResponseText()));
		}, 1000);


		//移動
		TeavmMain.LEFT.addEventListener("click", TeavmMain::next);
		TeavmMain.RIGHT.addEventListener("click", TeavmMain::prev);

		//ドラッグ
		TeavmMain.MAIN.addEventListener("mousedown", TeavmMain::downButton);
		TeavmMain.MAIN.addEventListener("mouseup", TeavmMain::upButton);
		TeavmMain.DOC.addEventListener("mousemove", TeavmMain::scrollImg);
	}

	/**
	 * 指定されたパスを適用します
	 */
	public static void applyPath(String path)
	{
		
		System.out.println("newPath: " + path);
		TeavmMain.nowPath = path;
		String query = (path == null ? "" : "?path=" + path);
		
		//URL差し替え
		TeavmMain.HISTORY.replaceState(null, TeavmMain.TITLE, "/index.html" + query);

		//画像差し替え
		TeavmMain.IMG.setSrc("/storage" + query);

		//スクロール位置修正
		TeavmMain.VIEWER.getStyle().setProperty("top", "0px");
		TeavmMain.VIEWER.getStyle().setProperty("left", "0px");

		//現在のパスを表示
		if(path != null)
		{
			TeavmMain.PATH_TITLE.withText(path);
		}
	}


//======================================================================================

	/**
	 * 次に移動するイベントリスナー
	 */
	public static void next(Event event)
	{
		TeavmMain.fetchNextPath(TeavmMain.nowPath, TeavmMain::applyPath);
	}

	/**
	 * 前に移動するイベントリスナー
	 */
	public static void prev(Event event)
	{
		TeavmMain.fetchPrevPath(TeavmMain.nowPath, TeavmMain::applyPath);
	}

	public static void upButton(Event event)
	{
		final MouseEvent e = (MouseEvent)event;
		if(e.getButton() == 0)
		{
			TeavmMain.startTop = null;
			TeavmMain.startLeft = null;
			TeavmMain.clickedEvent = null;
		}
	}

	public static void downButton(Event event)
	{
		MouseEvent e = (MouseEvent)event;
		if(e.getButton() == 0)
		{
			CSSStyleDeclaration sytle = TeavmMain.VIEWER.getStyle();
			TeavmMain.startTop = sytle.getPropertyValue("top");
			TeavmMain.startLeft = sytle.getPropertyValue("left");
			TeavmMain.clickedEvent = e;
		}
	}


	public static void scrollImg(Event event)
	{
		MouseEvent e = (MouseEvent)event;
		if(TeavmMain.clickedEvent == null)
		{
			return;
		}

		int addX = e.getScreenX() - TeavmMain.clickedEvent.getScreenX();
		int addY = e.getScreenY() - TeavmMain.clickedEvent.getScreenY();

		CSSStyleDeclaration sytle = TeavmMain.VIEWER.getStyle();
		sytle.setProperty("top", "calc(" + addY + "px" + " + " + TeavmMain.startTop + ")");
		sytle.setProperty("left", "calc(" + addX + "px" + " + " + TeavmMain.startLeft + ")");
	}


//======================================================================================

	/**
	 * 次のパスをリクエストする
	 */
	private static void fetchNextPath(String nowPath, Consumer<String> callback)
	{
		String url = "/next" + (nowPath == null ? "" : "?path=" + nowPath);
		TeavmMain.fetch(url, req -> callback.accept(req.getResponseText()));
	}

	/**
	 * 前のパスをリクエストする
	 */
	private static void fetchPrevPath(String nowPath, Consumer<String> callback)
	{
		String url = "/prev" + (nowPath == null ? "" : "?path=" + nowPath);
		TeavmMain.fetch(url, req -> callback.accept(req.getResponseText()));
	}

	/**
	 * HTTPリクエストを送信
	 */
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
