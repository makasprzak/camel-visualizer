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
    public void shouldComposeMultipleRoutes() throws Exception {
        assertThat(plantUMLComposer.toPlantUML(
                asList(
                        Arrays.<DiagramElement>asList(activity("a"), activity("b"), activity("c")),
                        Arrays.<DiagramElement>asList(activity("b"), activity("d"))
                )
        )).isEqualTo(
                activityGraph().activity("a").activity("b").activity("c").beginAnother().activity("b").activity("d").build()
        );

    }

    @Test
    public void shouldComposeTreeWithConditionAtThirdNode() throws Exception {
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

    @Test
    public void shouldComposeTreeWithConditionAtSecondNode() throws Exception {
        assertThat(plantUMLComposer.toPlantUML(
                asList(
                        asList(
                                activity("a"),
                                condition()
                                        .withWhenTrue(activity("b"))
                                        .withWhenFalse(activity("c"))
                                        .withExpression("condition")
                                        .build())
                )
        )).isEqualTo(
                activityGraph()
                        .activity("a")
                        .condition("condition")
                            .whenTrue()
                                .activity("b")
                            .whenFalse()
                                .activity("c")
                            .endIf()
                        .build()
        );

    }

    @Test
    public void shouldComposeTreeWithComplexCondition() throws Exception {
        assertThat(plantUMLComposer.toPlantUML(
                asList(
                        asList(
                                activity("a"),
                                condition()
                                        .withWhenTrue(
                                                condition()
                                                    .withWhenTrue(activity("b"))
                                                    .withWhenFalse(activity("c"))
                                                    .withExpression("condition B")
                                                    .build()
                                        )
                                        .withWhenFalse(activity("d"))
                                        .withExpression("condition A")
                                        .build())
                )
        )).isEqualTo(
                activityGraph()
                        .activity("a")
                        .condition("condition A")
                            .whenTrue()
                                .condition("condition B")
                                    .whenTrue()
                                        .activity("b")
                                    .whenFalse()
                                        .activity("c")
                                .endIf()
                            .whenFalse()
                                .activity("d")
                            .endIf()
                        .build()
        );

    }

}