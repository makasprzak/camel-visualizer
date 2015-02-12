package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.DiagramElement;
import org.junit.Test;

import java.util.Arrays;

import static com.makasprzak.camel.visualizer.model.Activity.activity;
import static com.makasprzak.camel.visualizer.model.Condition.Builder.condition;
import static com.makasprzak.plantuml.dsl.activity.ActivityGraphBuilder.activityGraph;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

public class PlantUMLComposerTest {
    private final PlantUMLComposer plantUMLComposer = new PlantUMLComposer();

    @Test
    public void shouldComposeSimpleTreeThreeActivities() throws Exception {
        assertThat(plantUMLComposer.toPlantUML(
                asList(
                        Arrays.<DiagramElement>asList(activity("a"), activity("b"), activity("c"))
                )
        )).isEqualTo(
                activityGraph().activity("a").activity("b").activity("c").build()
        );

    }

    @Test
    public void shouldComposeTreeWithConditionAsThirdNode() throws Exception {
        assertThat(plantUMLComposer.toPlantUML(
                asList(
                        asList(
                                activity("a"),
                                activity("b"),
                                condition()
                                        .withWhenTrue(activity("c"))
                                        .withWhenFalse(activity("d"))
                                        .withExpression("condition")
                                        .build())
                )
        )).isEqualTo(
                activityGraph()
                        .activity("a")
                        .activity("b")
                        .condition("condition")
                            .whenTrue()
                                .activity("c")
                            .whenFalse()
                                .activity("d")
                            .endIf()
                        .build()
        );

    }
}