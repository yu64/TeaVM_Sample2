package test;

import java.nio.file.Path;

import flak.App;
import flak.AppFactory;
import flak.Flak;

public class Main {

	public static void main(String[] args) throws Exception
	{
		AppFactory factory = Flak.getFactory();
		factory.setPort(Util.getAnyPort());

		App app = factory.createApp();
		app.scan(new Handler(null));
		app.start();

		System.out.println(app.getRootUrl());
		EmbedViewer.open(app.getRootUrl());
	}

}
