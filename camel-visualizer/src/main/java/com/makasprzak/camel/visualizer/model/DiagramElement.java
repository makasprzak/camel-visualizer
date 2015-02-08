package com.makasprzak.camel.visualizer.model;

import java.util.Set;

public interface DiagramElement {
    Set<DiagramElement> getOutputs();
}
