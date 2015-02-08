package com.makasprzak.camel.visualizer.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.Set;

public class Activity implements DiagramElement{
    private final String label;

    public Activity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity that = (Activity) o;

        return Objects.equal(this.label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("label", label)
                .toString();
    }

    public static Activity activity(String label) {
        return new Activity(label);
    }

    @Override
    public Set<DiagramElement> getOutputs() {
        return Collections.<DiagramElement>singleton(this);
    }
}
