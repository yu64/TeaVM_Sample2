package test;

import flak.annotations.Route;

public class Handler {
    
    public Handler(AppResource resource)
    {

    }

    @Route("/")
	public String handle()
	{
        return "<p>Test</p>";
    }

    @Route("/stop")
	public void stop()
	{
        System.exit(0);
    }
}
