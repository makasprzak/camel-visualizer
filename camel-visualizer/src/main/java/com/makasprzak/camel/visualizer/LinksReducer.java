package com.makasprzak.camel.visualizer;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.makasprzak.camel.visualizer.model.DiagramElement;
import com.makasprzak.camel.visualizer.model.Link;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Multimaps.index;

public class LinksReducer {
    public List<List<Link>> groupLinks(Set<Link> links) {
        Multimap<DiagramElement, Link> linksBySources = index(links, bySource());
        Link start = getFirst(links, null);
        List<List<Link>> grouped = newLinkedList();
        while(true) {
            start.getTarget();
        }
    }


    private Function<Link, DiagramElement> byTarget() {
        return new Function<Link, DiagramElement>() {
            @Override
            public DiagramElement apply(Link input) {
                return input.getTarget();
            }
        };
    }

    private Function<Link, DiagramElement> bySource() {
        return new Function<Link, DiagramElement>() {
            @Override
            public DiagramElement apply(Link input) {
                return input.getSource();
            }
        };
    }
}
