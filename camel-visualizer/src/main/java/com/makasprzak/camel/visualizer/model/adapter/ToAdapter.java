package com.makasprzak.camel.visualizer.model.adapter;

import com.makasprzak.camel.visualizer.model.Activity;
import org.apache.camel.model.ToDefinition;

import static com.makasprzak.camel.visualizer.model.Activity.activity;

public class ToAdapter implements CamelModelAdapter<ToDefinition> {
    @Override
    public Activity transform(ToDefinition to) {
        return activity(to.getUri());
    }
}
