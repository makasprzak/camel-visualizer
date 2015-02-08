package com.makasprzak.camel.visualizer.model.adapter;

import com.makasprzak.camel.visualizer.model.DiagramElement;
import org.apache.camel.model.ProcessorDefinition;

public interface CamelModelAdapter<T extends ProcessorDefinition<?>> {
    DiagramElement transform(T processorDefinition);
}
