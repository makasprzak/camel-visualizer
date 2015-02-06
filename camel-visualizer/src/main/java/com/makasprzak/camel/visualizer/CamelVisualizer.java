package com.makasprzak.camel.visualizer;

import com.makasprzak.plantuml.dsl.activity.levels.FirstNodeLevel;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.List;

import static com.makasprzak.plantuml.dsl.activity.ActivityGraphBuilder.activityGraph;

public class CamelVisualizer {
    private final ModelCamelContext camelContext;

    public CamelVisualizer(ModelCamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public String toPlantUML() {
        List<RouteDefinition> routeDefinitions = camelContext.getRouteDefinitions();
        FirstNodeLevel firstNodeLevel = activityGraph();
        for (RouteDefinition routeDefinition : routeDefinitions) {
            System.out.println(routeDefinition);
            System.out.println(routeDefinition.getInputs());
            System.out.println(routeDefinition.getInterceptStrategies());
            System.out.println(routeDefinition.getOutputs());
        }
        return null;
    }
}
