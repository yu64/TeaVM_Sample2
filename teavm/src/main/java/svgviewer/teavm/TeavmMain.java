package svgviewer.teavm;

import java.util.Arrays;

import org.teavm.jso.JSBody;
import org.teavm.jso.browser.History;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class TeavmMain {

	public static void main(String[] args) throws Exception
	{
		TeavmMain.init();
	}

	public static void init()
	{
		final Window window = Window.current();
		final HTMLDocument doc = window.getDocument();
		final String title = doc.getTitle();

		final HTMLElement left = doc.querySelector("#left");
		final HTMLElement right = doc.querySelector("#right");

		final History history = History.current();
		

		left.addEventListener("click", e -> {

			//history.replaceState(null, title, "https://www.google.com");
		});

		right.addEventListener("click", e -> {

		});


		System.out.println(Arrays.toString(TeavmMain.getContents()));
	}

	public static void open(String path)
	{

	}
 
	@JSBody(script = "return getContents()")
	public static native String[] getContents();
}
