package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Activity;
import com.makasprzak.camel.visualizer.model.Link;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.makasprzak.camel.visualizer.model.Activity.activity;

public class CamelVisualizer {
    private final ModelCamelContext camelContext;

    public CamelVisualizer(ModelCamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public String toPlantUML() {
        List<RouteDefinition> routeDefinitions = camelContext.getRouteDefinitions();
        Set<Link> links = new HashSet<Link>();
        for (RouteDefinition routeDefinition : routeDefinitions) {
            for (FromDefinition fromDefinition : routeDefinition.getInputs()) {
                Activity source = activity(fromDefinition.getUri());
                List<ProcessorDefinition<?>> outputs = routeDefinition.getOutputs();
            }
        }


        return null;
    }


}
