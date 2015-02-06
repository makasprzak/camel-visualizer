package com.makasprzak.camel.visualizer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

public class ExampleContext extends RouteBuilder{

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

    public static ModelCamelContext get() throws Exception {
        return new ExampleContext().buildContext();
    }

}
