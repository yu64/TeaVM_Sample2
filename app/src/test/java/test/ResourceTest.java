package test;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

public class ResourceTest {

	
	
	@Test
	public void test()
	{
		ClassGraph graph = new ClassGraph()
				
				.acceptPackages("test.resources")
				
				;
		
		System.out.println(graph);
		
		try(ScanResult r = graph.scan())
		{
			Map<String, ResourceList> map = r.getAllResourcesAsMap();
			
			long count = map.entrySet().stream()
				.peek(e -> System.out.println(e))
				.count();
			
			Assertions.assertNotEquals(0, count);
		}	
	}
}
