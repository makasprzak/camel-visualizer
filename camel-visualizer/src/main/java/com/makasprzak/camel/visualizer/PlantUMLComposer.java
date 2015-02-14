package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Activity;
import com.makasprzak.camel.visualizer.model.Condition;
import com.makasprzak.camel.visualizer.model.DiagramElement;
import com.makasprzak.plantuml.dsl.activity.ActivityBuilderBase;
import com.makasprzak.plantuml.dsl.activity.ActivityGraphBuilder;
import com.makasprzak.plantuml.dsl.activity.levels.*;
import com.makasprzak.plantuml.dsl.activity.steps.ActivityStep;
import com.makasprzak.plantuml.dsl.activity.steps.ConditionStep;

import java.util.List;

public class PlantUMLComposer {
    public String toPlantUML(List<List<DiagramElement>> tree) {
        FirstNodeLevel firstNodeLevel = null;
        GeneralLevel builder = null;
        for (List<DiagramElement> elements : tree) {
            firstNodeLevel = next(firstNodeLevel, builder);
            SecondNodeLevel secondNodeLevel = firstNodeLevel.activity(getActivityLabel(elements.get(0)));
            builder = buildNode(GeneralLevel.class, secondNodeLevel, elements.get(1));
            for (int i = 2; i < elements.size(); i++) {
                DiagramElement diagramElement = elements.get(i);
                builder = buildNode(GeneralLevel.class, builder, diagramElement);
            }
        }
        return builder.build();
    }

    private FirstNodeLevel next(FirstNodeLevel firstNodeLevel, GeneralLevel builder) {
        if (firstNodeLevel == null) {
            firstNodeLevel = ActivityGraphBuilder.activityGraph();
        } else {
            firstNodeLevel = builder.beginAnother();
        }
        return firstNodeLevel;
    }

    private <R extends ActivityBuilderBase,T extends  ActivityBuilderBase>R buildNode(Class<R> clazz, T builder, DiagramElement diagramElement) {
        if (diagramElement instanceof Activity) {
            return toActivityBuilder(clazz,builder).activity(getActivityLabel(diagramElement));
        } else {
            Condition condition = (Condition)diagramElement;
            WhenTrueFirstLevel<R> generalLevelWhenTrueFirstLevel = toConditionBuilder(clazz, builder)
                    .condition(condition.getExpression())
                    .whenTrue();

            WhenTrueFurtherLevel<R> whenTrue = buildNode(WhenTrueFurtherLevel.class,generalLevelWhenTrueFirstLevel,condition.getWhenTrue());
            WhenFalseFirstLevel<R> whenFalseFirstLevel = whenTrue.whenFalse();
            WhenFalseFurtherLevel<R> whenFalse = buildNode(WhenFalseFurtherLevel.class,whenFalseFirstLevel,condition.getWhenFalse());
            return whenFalse
                    .endIf();
        }
    }

    private <R extends ActivityBuilderBase>ActivityStep<R> toActivityBuilder(Class<R> clazz, ActivityBuilderBase builder) {
        return (ActivityStep<R>)builder;
    }
    private <R extends ActivityBuilderBase>ConditionStep<ConditionLevel<R>> toConditionBuilder(Class<R> clazz, ActivityBuilderBase builder) {
        return (ConditionStep<ConditionLevel<R>>)builder;
    }

    private String getActivityLabel(DiagramElement activity) {
        return ((Activity) activity).getLabel();
    }
}
