package test.teavm;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLAnchorElement;
import org.teavm.jso.dom.html.HTMLBodyElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class Main {

	public static void main(String[] args) throws Exception
	{
		System.out.println("Hello TeaVM Script. TeaVMで生成されたスクリプト");
		HTMLDocument doc = Window.current().getDocument();
		
		HTMLElement h1 = doc.createElement("h1");
		h1.withText("TeaVMを使用しています");
		
		HTMLAnchorElement a = (HTMLAnchorElement) doc.createElement("a");
		a.setHref("http://localhost:8080/stop");
		a.withText(a.getHref());

		HTMLBodyElement body = doc.getBody();
		body.insertBefore(h1, null);
		body.insertBefore(a, null);
	}



}
