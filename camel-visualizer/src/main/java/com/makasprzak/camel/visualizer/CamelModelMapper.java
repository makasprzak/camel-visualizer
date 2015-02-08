package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Activity;
import com.makasprzak.camel.visualizer.model.Condition;
import com.makasprzak.camel.visualizer.model.Link;
import org.apache.camel.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.makasprzak.camel.visualizer.model.Activity.activity;
import static com.makasprzak.camel.visualizer.model.Condition.Builder.condition;
import static com.makasprzak.camel.visualizer.model.Link.Builder.link;

public class CamelModelMapper {
    public Set<Link> map(RouteDefinition routeDefinition) {
        Set<Link> links = new HashSet<Link>();
        for (FromDefinition fromDefinition : routeDefinition.getInputs()) {
            Activity lastSource = activity(fromDefinition.getUri());
            List<ProcessorDefinition<?>> outputs = routeDefinition.getOutputs();
            links.addAll(processOutputs(lastSource, outputs));
        }
        return links;
    }

    private Set<Link> processOutputs(Activity lastSource, List<ProcessorDefinition<?>> outputs) {
        Set<Link> links = new HashSet<Link>();
        for (ProcessorDefinition<?> output : outputs) {
            if (output instanceof ToDefinition) {
                Activity target = activity(((ToDefinition) output).getUri());
                links.add(
                        link()
                                .withSource(lastSource)
                                .withTarget(target)
                                .build()
                );
                lastSource = target;
            } else {
                ChoiceDefinition choiceDefinition = (ChoiceDefinition)output;
                WhenDefinition whenDefinition = choiceDefinition.getWhenClauses().get(0);
                OtherwiseDefinition otherwiseDefinition = choiceDefinition.getOtherwise();
                List<ProcessorDefinition<?>> whenOutputs = whenDefinition.getOutputs();
                List<ProcessorDefinition<?>> otherwiseOutputs = otherwiseDefinition.getOutputs();
                ProcessorDefinition<?> otherwise = otherwiseOutputs.get(0);
                Activity whenActivity = activity(((ToDefinition) whenOutputs.get(0)).getUri());
                Activity otherwiseActivity = activity(((ToDefinition) otherwise).getUri());
                Condition condition = condition()
                        .withWhenTrue(whenActivity)
                        .withWhenFalse(otherwiseActivity)
                        .withExpression(whenDefinition.getExpression().getExpression())
                        .build();
                links.add(
                        link()
                                .withSource(lastSource)
                                .withTarget(condition)
                                .build()
                );
                if (whenOutputs.size() > 1) {
                    links.addAll(processOtherOutputs(whenOutputs, whenActivity));
                }
                if (otherwiseOutputs.size() > 1) {
                    links.addAll(processOtherOutputs(otherwiseOutputs, otherwiseActivity));
                }

                lastSource = null; //TODO assuming choice is always last node, is it possible that the assumption is wrong?
            }
        }
        return links;
    }

    private Set<Link> processOtherOutputs(List<ProcessorDefinition<?>> outputs, Activity source) {
        return processOutputs(source, outputs.subList(1, outputs.size()));
    }
}
