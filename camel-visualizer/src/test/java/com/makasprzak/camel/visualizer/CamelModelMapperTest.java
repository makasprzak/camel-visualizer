package com.makasprzak.camel.visualizer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.camel.model.*;
import org.apache.camel.model.language.ExpressionDefinition;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.makasprzak.camel.visualizer.model.Activity.activity;
import static com.makasprzak.camel.visualizer.model.Condition.Builder.condition;
import static com.makasprzak.camel.visualizer.model.Link.Builder.link;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

public class CamelModelMapperTest {

    private final CamelModelMapper mapper = new CamelModelMapper();

    @Test
    public void shouldMapSimplestRouteToJustOneLink() throws Exception {
        assertThat(
                mapper.map(
                        route(
                                ImmutableList.of(from("direct:in")),
                                ImmutableList.<ProcessorDefinition<?>>of(to("direct:out"))
                        )
                )
        ).isEqualTo(ImmutableSet.of(
                link()
                        .withSource(activity("direct:in"))
                        .withTarget(activity("direct:out"))
                        .build()
                )
        );
    }

    @Test
    public void shouldMapRouteWithTwoOutputsToTwoLinks() throws Exception {
        assertThat(
                mapper.map(
                        route(
                                ImmutableList.of(from("direct:in")),
                                ImmutableList.<ProcessorDefinition<?>>of(
                                        to("direct:firstOut"),
                                        to("direct:secondOut")
                                )
                        )
                )
        ).containsOnly(
                link()
                        .withSource(activity("direct:in"))
                        .withTarget(activity("direct:firstOut"))
                        .build()
                ,
                link()
                        .withSource(activity("direct:firstOut"))
                        .withTarget(activity("direct:secondOut"))
                        .build()
        );
    }

    @Test
    public void shouldMapRouteWithTwoInputsAndTwoOutputsToThreeLinks() throws Exception {
        assertThat(
                mapper.map(
                        route(
                                asList(
                                        from("direct:in1"),
                                        from("direct:in2")
                                ),
                                Arrays.<ProcessorDefinition<?>>asList(
                                        to("direct:firstOut"),
                                        to("direct:secondOut")
                                )
                        )
                )
        ).containsOnly(
                link()
                        .withSource(activity("direct:in1"))
                        .withTarget(activity("direct:firstOut"))
                        .build(),
                link()
                        .withSource(activity("direct:in2"))
                        .withTarget(activity("direct:firstOut"))
                        .build(),
                link()
                        .withSource(activity("direct:firstOut"))
                        .withTarget(activity("direct:secondOut"))
                        .build()
        );
    }

    @Test
    public void shouldMapSimpleRouteWithChoiceToOneLinkWithCondition() throws Exception {
        assertThat(
                mapper.map(
                        route(
                                asList(
                                        from("direct:in")
                                ),
                                Arrays.<ProcessorDefinition<?>>asList(
                                        choice(otherwise(to("direct:otherwise")), when("a == b", to("direct:when")))
                                )
                        )
                )
        ).containsOnly(
                link()
                        .withSource(activity("direct:in"))
                        .withTarget(
                                condition()
                                        .withWhenTrue(activity("direct:when"))
                                        .withWhenFalse(activity("direct:otherwise"))
                                        .withExpression("a == b")
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    public void shouldMapRouteWithChoiceThatHasWhenClauseWithThreeOutputsToThreeLinks() throws Exception {
        assertThat(
                mapper.map(
                        route(
                                asList(
                                        from("direct:in")
                                ),
                                Arrays.<ProcessorDefinition<?>>asList(
                                        choice(otherwise(to("direct:otherwise")),when("a == b",to("direct:when1"),to("direct:when2"),to("direct:when3")))
                                )
                        )
                )
        ).containsOnly(
                link()
                        .withSource(activity("direct:in"))
                        .withTarget(
                                condition()
                                        .withWhenTrue(activity("direct:when1"))
                                        .withWhenFalse(activity("direct:otherwise"))
                                        .withExpression("a == b")
                                        .build()
                        )
                        .build(),
                link()
                    .withSource(activity("direct:when1"))
                    .withTarget(activity("direct:when2"))
                    .build(),
                link()
                        .withSource(activity("direct:when2"))
                        .withTarget(activity("direct:when3"))
                        .build()

        );
    }

    @Test
    public void shouldMapRouteWithChoiceThatHasOtherwiseClauseWithThreeOutputsToThreeLinks() throws Exception {
        assertThat(
                mapper.map(
                        route(
                                asList(
                                        from("direct:in")
                                ),
                                Arrays.<ProcessorDefinition<?>>asList(
                                        choice(otherwise(to("direct:otherwise1"), to("direct:otherwise2"), to("direct:otherwise3")),when("a == b",to("direct:when")))
                                )
                        )
                )
        ).containsOnly(
                link()
                        .withSource(activity("direct:in"))
                        .withTarget(
                                condition()
                                        .withWhenTrue(activity("direct:when"))
                                        .withWhenFalse(activity("direct:otherwise1"))
                                        .withExpression("a == b")
                                        .build()
                        )
                        .build(),
                link()
                    .withSource(activity("direct:otherwise1"))
                    .withTarget(activity("direct:otherwise2"))
                    .build(),
                link()
                        .withSource(activity("direct:otherwise2"))
                        .withTarget(activity("direct:otherwise3"))
                        .build()

        );
    }

//TODO multiple whens
    //TODO filters

    private FromDefinition from(String uri) {
        return new FromDefinition(uri);
    }

    private ToDefinition to(String uri) {
        return new ToDefinition(uri);
    }

    private RouteDefinition route(List<FromDefinition> inputs, List<ProcessorDefinition<?>> outputs) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setInputs(inputs);
        routeDefinition.setOutputs(outputs);
        return routeDefinition;
    }

    private ChoiceDefinition choice(OtherwiseDefinition otherwise, WhenDefinition ... whens) {
        ChoiceDefinition choiceDefinition = new ChoiceDefinition();
        choiceDefinition.setOtherwise(otherwise);
        choiceDefinition.setWhenClauses(asList(whens));
        return choiceDefinition;
    }

    private WhenDefinition when(String expression, ProcessorDefinition<?> ... outputs) {
        WhenDefinition whenDefinition = new WhenDefinition();
        whenDefinition.setExpression(new ExpressionDefinition(expression));
        whenDefinition.setOutputs(asList(outputs));
        return whenDefinition;
    }

    private OtherwiseDefinition otherwise(ProcessorDefinition<?> ... outputs) {
        OtherwiseDefinition otherwiseDefinition = new OtherwiseDefinition();
        otherwiseDefinition.setOutputs(asList(outputs));
        return otherwiseDefinition;
    }
}