package com.makasprzak.camel.visualizer;


import org.apache.camel.model.ModelCamelContext;
import org.junit.Before;
import org.junit.Test;

public class CamelVisualizerTest {
    private ModelCamelContext camelContext;

    @Before
    public void setUp() throws Exception {
        this.camelContext = ExampleContext.get();
    }

    @Test
    public void shouldVisualizeExampleContext() throws Exception {
        System.out.println(new CamelVisualizer(camelContext).toPlantUML());

    }

}