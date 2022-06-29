package svgviewer;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import flak.App;
import flak.AppFactory;
import flak.Flak;

public class Main {

	public static void main(String[] args) throws Exception
	{
		Path storage = Path.of((args.length == 0 ? "./storage" : args[0]));
		Files.createDirectories(storage);

		AppFactory factory = Flak.getFactory();
		factory.setPort(Util.getAnyPort());

		App app = factory.createApp();
		app.scan(new Handler(new ContentObserver(storage)));
		app.start();

		System.out.println(app.getRootUrl());

		Desktop.getDesktop().browse(new URI(app.getRootUrl()));
		EmbedViewer.open(app.getRootUrl());
		
	}

}
