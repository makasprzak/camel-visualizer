package com.makasprzak.camel.visualizer.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Condition implements DiagramElement{

    private final DiagramElement whenTrue;
    private final DiagramElement whenFalse;
    private final String expression;

    public Condition(
            DiagramElement whenTrue,
            DiagramElement whenFalse,
            String expression
    ) {

        this.whenTrue = whenTrue;
        this.whenFalse = whenFalse;
        this.expression = expression;
    }


    @Override
    public Set<DiagramElement> getOutputs() {
        return ImmutableSet.of(whenTrue, whenFalse);
    }

    public DiagramElement getWhenTrue() {
        return whenTrue;
    }

    public DiagramElement getWhenFalse() {
        return whenFalse;
    }

    public String getExpression() {
        return expression;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Condition that = (Condition) o;

        return Objects.equal(this.whenTrue, that.whenTrue) &&
                Objects.equal(this.whenFalse, that.whenFalse) &&
                Objects.equal(this.expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(whenTrue, whenFalse, expression);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("whenTrue", whenTrue)
                .add("whenFalse", whenFalse)
                .add("expression", expression)
                .toString();
    }

    public static interface WhenTrueStep {
        WhenFalseStep withWhenTrue(DiagramElement whenTrue);
    }

    public static interface WhenFalseStep {
        ExpressionStep withWhenFalse(DiagramElement whenFalse);
    }

    public static interface ExpressionStep {
        BuildStep withExpression(String expression);
    }

    public static interface BuildStep {
        Condition build();
    }


    public static class Builder implements WhenTrueStep, WhenFalseStep, ExpressionStep, BuildStep {
        private DiagramElement whenTrue;
        private DiagramElement whenFalse;
        private String expression;

        private Builder() {
        }

        public static WhenTrueStep condition() {
            return new Builder();
        }

        @Override
        public WhenFalseStep withWhenTrue(DiagramElement whenTrue) {
            this.whenTrue = whenTrue;
            return this;
        }

        @Override
        public ExpressionStep withWhenFalse(DiagramElement whenFalse) {
            this.whenFalse = whenFalse;
            return this;
        }

        @Override
        public BuildStep withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        @Override
        public Condition build() {
            return new Condition(
                    this.whenTrue,
                    this.whenFalse,
                    this.expression
            );
        }
    }
}
