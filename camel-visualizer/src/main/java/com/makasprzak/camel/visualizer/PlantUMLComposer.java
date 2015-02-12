package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Activity;
import com.makasprzak.camel.visualizer.model.Condition;
import com.makasprzak.camel.visualizer.model.DiagramElement;
import com.makasprzak.plantuml.dsl.activity.ActivityGraphBuilder;
import com.makasprzak.plantuml.dsl.activity.levels.GeneralLevel;

import java.util.List;

public class PlantUMLComposer {
    public String toPlantUML(List<List<DiagramElement>> tree) {
        List<DiagramElement> elements = tree.get(0);
        GeneralLevel builder = ActivityGraphBuilder.activityGraph()
                .activity(((Activity) elements.get(0)).getLabel())
                .activity(((Activity) elements.get(1)).getLabel());
        for (int i = 2; i < elements.size(); i++) {
            DiagramElement diagramElement = elements.get(i);
            if (diagramElement instanceof Activity) {
                builder = builder.activity(getActivityLabel(diagramElement));
            } else {
                Condition condition = (Condition)diagramElement;
                builder = builder
                        .condition(condition.getExpression())
                            .whenTrue()
                                .activity(getActivityLabel(condition.getWhenTrue()))
                            .whenFalse()
                                .activity(getActivityLabel(condition.getWhenFalse()))
                        .endIf();
            }
        }
        return builder.build();
    }

    private String getActivityLabel(DiagramElement activity) {
        return ((Activity) activity).getLabel();
    }
}
