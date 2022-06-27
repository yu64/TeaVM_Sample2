package test;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import flak.App;
import flak.Flak;
import flak.annotations.Route;

public class Main {

	public static void main(String[] args) throws Exception
	{
		App app = Flak.createHttpApp(8080);
		app.scan(new Main());
		app.start();

		Desktop.getDesktop().browse(new URI(app.getRootUrl()));
		System.out.println("http://localhost:8080/stop");
		
	}

	@Route("/")
	public String handle()
	{
		String temp = "";
		temp += "<!DOCTYPE html>";
		temp += "<html>";
		temp += "<head>";
		temp += "<meta charset=\"UTF-8\">";
		temp += "<script>%s</script>";
		temp += "</head>";
		temp += "<body>";
		temp += "<p>テストHTML</p>";
		temp += "<script>callTeavmScript()</script>";
		temp += "</body>";
		temp += "</html>";
		
		InputStream is = ClassLoader.getSystemResourceAsStream("test/resources/app.js");
		InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);
		String js = br.lines().collect(Collectors.joining());

		return String.format(temp, js);
	}

	@Route("/stop")
	public void stop()
	{
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			public void run() {

				System.exit(0);
			};

		}, 1000);

	}
}
