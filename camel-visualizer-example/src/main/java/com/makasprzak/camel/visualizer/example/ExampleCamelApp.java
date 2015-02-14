package com.makasprzak.camel.visualizer.example;

import com.makasprzak.camel.visualizer.exporter.FileCamelDiagramExporter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleCamelApp extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleCamelApp.class);

    @Override
    public void configure() throws Exception {
        from("direct:startA")
                .to("direct:middle");
        from("direct:startB")
                .to("direct:middle");
        from("direct:middle")
                .choice()
                   .when(header("propertyA").isEqualTo("firstValue"))
                      .to("direct:endA")
                   .when(header("propertyA").isEqualTo("secondValue"))
                      .to("direct:endB")
                   .otherwise()
                      .to("direct:endC");
        from("direct:endB")
                .filter(header("propertyB").isGreaterThan(3))
                    .to("direct:middle2")
                    .to("direct:endC");
    }

    public ModelCamelContext buildContext() throws Exception {
        ModelCamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(this);
        camelContext.start();
        return camelContext;
    }

    public static void main(String[] args) throws Exception {
        ModelCamelContext camelContext = new ExampleCamelApp().buildContext();
        camelContext.getRouteDefinitions().forEach(route -> {
            LOG.info(route.toString());
            route.getInputs().forEach(in -> System.out.println("Input: "+in.getUri()));
            route.getOutputs().forEach(out -> System.out.println("Out: "+out.getClass()));
        });
        FileCamelDiagramExporter.fileExporter(camelContext, System.getProperty("diagram.source.out","camel.plantuml"), System.getProperty("diagram.out","camel.png")).export();
    }
}
