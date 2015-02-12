package com.makasprzak.camel.visualizer;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.makasprzak.camel.visualizer.model.DiagramElement;
import com.makasprzak.camel.visualizer.model.Link;

import java.util.List;

import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Lists.transform;

public class LinksToNodesTransformer {
    List<List<DiagramElement>> transformToElements(List<List<Link>> links) {
        return transform(links, new Function<List<Link>, List<DiagramElement>>() {
            @Override
            public List<DiagramElement> apply(List<Link> input) {
                return ImmutableList.<DiagramElement>builder().addAll(transform(input, new Function<Link, DiagramElement>() {
                    @Override
                    public DiagramElement apply(Link input) {
                        return input.getSource();
                    }
                })).add(getLast(input).getTarget()).build();
            }
        });
    }
}
