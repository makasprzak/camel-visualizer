package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Link;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CamelVisualizer {
    private final ModelCamelContext camelContext;
    private final CamelModelMapper camelModelMapper = new CamelModelMapper();

    public CamelVisualizer(ModelCamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public String toPlantUML() {
        List<RouteDefinition> routeDefinitions = camelContext.getRouteDefinitions();
        Set<Link> links = new HashSet<Link>();
        for (RouteDefinition routeDefinition : routeDefinitions) {
            links.addAll(camelModelMapper.map(routeDefinition));
        }


        return null;
    }


}
