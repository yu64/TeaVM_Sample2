package svgviewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class EmbedViewer extends Application{

    public static void open(String url)
    {
        Application.launch(EmbedViewer.class, url);
    }


    private String url;
    private WebView webView;

    @Override
    public void start(Stage stage) throws Exception 
    {
        this.url = this.getParameters().getRaw().get(0);
        if(url == null || url.isEmpty())
        {
            url = "http://example.com/";
        }

        this.webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(this.url);

        BorderPane pane = new BorderPane(this.webView);
        Scene scene = new Scene(pane, 500, 500);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception
    {
        System.exit(0);
    }

}