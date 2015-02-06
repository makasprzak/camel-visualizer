package com.makasprzak.camel.visualizer;


import org.apache.camel.model.ModelCamelContext;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.fest.assertions.Assertions.assertThat;

public class CamelVisualizerTest {
    private ModelCamelContext camelContext;

    @Before
    public void setUp() throws Exception {
        this.camelContext = ExampleContext.get();
    }

    @Test
    public void shouldVisualizeExampleContext() throws Exception {
        assertThat(new CamelVisualizer(camelContext).toPlantUML())
                .isEqualTo(read("/exampleContext.plantuml"));

    }

    private String read(String path) throws IOException {
        return readFileToString(new File(getClass().getResource(path).getFile()),(String)null).replaceAll("\\r","");
    }

}