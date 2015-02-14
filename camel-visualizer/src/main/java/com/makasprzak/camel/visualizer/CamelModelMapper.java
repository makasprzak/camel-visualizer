package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Activity;
import com.makasprzak.camel.visualizer.model.Condition;
import com.makasprzak.camel.visualizer.model.Link;
import org.apache.camel.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.makasprzak.camel.visualizer.model.Activity.activity;
import static com.makasprzak.camel.visualizer.model.Condition.Builder.condition;
import static com.makasprzak.camel.visualizer.model.Link.Builder.link;

public class CamelModelMapper {
    private final Logger LOG = LoggerFactory.getLogger(CamelModelMapper.class);

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
                Activity target = activity(getLabel((ToDefinition) output));
                links.add(
                        link()
                                .withSource(lastSource)
                                .withTarget(target)
                                .build()
                );
                lastSource = target;
            } else if (output instanceof ChoiceDefinition) {
                ChoiceDefinition choiceDefinition = (ChoiceDefinition)output;
                links.add(
                        link()
                                .withSource(lastSource)
                                .withTarget(getCondition(links, choiceDefinition.getWhenClauses(), 0, choiceDefinition.getOtherwise()))
                                .build()
                );

                lastSource = null; //TODO assuming choice is always last node, is it possible that the assumption is wrong?
            } else if (output instanceof FilterDefinition) {
                FilterDefinition filterDefinition = (FilterDefinition)output;
                links.add(
                        link()
                        .withSource(lastSource)
                        .withTarget(
                                condition()
                                        .withWhenTrue(activity(getLabel((ToDefinition) filterDefinition.getOutputs().get(0))))
                                        .withWhenFalse(activity("STOP"))
                                        .withExpression(filterDefinition.getExpression().getExpression())
                                        .build()
                        ).build()
                );
                lastSource = null;
            } else {
                LOG.warn("Unsupported node {} skipped",output);
            }
        }
        return links;
    }

    private String getLabel(ToDefinition output) {
        return output.getUriOrRef();
    }

    private Condition getCondition(Set<Link> links, List<WhenDefinition> whens, int currentWhen, OtherwiseDefinition otherwiseDefinition) {
        return currentWhen < whens.size() - 1 ? transientCondition(links, whens, currentWhen, otherwiseDefinition) : terminalCondition(links, whens.get(currentWhen), otherwiseDefinition);
    }

    private Condition transientCondition(Set<Link> links, List<WhenDefinition> whens, int current, OtherwiseDefinition otherwiseDefinition) {
        WhenDefinition currentWhen = whens.get(current);
        List<ProcessorDefinition<?>> whenTrueOutputs = currentWhen.getOutputs();
        Activity whenTrueActivity = activity(getLabel((ToDefinition) whenTrueOutputs.get(0)));
        if (whenTrueOutputs.size() > 1) {
            links.addAll(processOtherOutputs(whenTrueOutputs, whenTrueActivity));
        }
        return condition()
                .withWhenTrue(whenTrueActivity)
                .withWhenFalse(getCondition(links, whens, current + 1, otherwiseDefinition))
                .withExpression(currentWhen.getExpression().getExpression())
                .build();
    }

    private Condition terminalCondition(Set<Link> links, WhenDefinition whenDefinition, OtherwiseDefinition otherwiseDefinition) {
        List<ProcessorDefinition<?>> whenTrueOutputs = whenDefinition.getOutputs();
        Activity whenTrueActivity = activity(getLabel((ToDefinition) whenTrueOutputs.get(0)));
        if (whenTrueOutputs.size() > 1) {
            links.addAll(processOtherOutputs(whenTrueOutputs, whenTrueActivity));
        }

        List<ProcessorDefinition<?>> whenFalseOutputs = otherwiseDefinition.getOutputs();
        ProcessorDefinition<?> otherwise = whenFalseOutputs.get(0);
        Activity whenFalseActivity = activity(getLabel((ToDefinition) otherwise));
        if (whenFalseOutputs.size() > 1) {
            links.addAll(processOtherOutputs(whenFalseOutputs, whenFalseActivity));
        }
        return condition()
                .withWhenTrue(whenTrueActivity)
                .withWhenFalse(whenFalseActivity)
                .withExpression(whenDefinition.getExpression().getExpression())
                .build();
    }

    private Set<Link> processOtherOutputs(List<ProcessorDefinition<?>> outputs, Activity source) {
        return processOutputs(source, outputs.subList(1, outputs.size()));
    }
}
