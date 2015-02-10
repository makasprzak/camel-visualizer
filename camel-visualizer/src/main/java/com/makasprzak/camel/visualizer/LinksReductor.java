package com.makasprzak.camel.visualizer;

import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.makasprzak.camel.visualizer.model.DiagramElement;
import com.makasprzak.camel.visualizer.model.Link;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Multimaps.filterKeys;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Sets.newHashSet;

public class LinksReductor {
    public List<List<Link>> groupLinks(Set<Link> links) {
        Set<Link> linksLeft = newHashSet(links);
        List<List<Link>> groupedLinks = newLinkedList();
        Multimap<DiagramElement, Link> linksBySources = LinkedListMultimap.create(index(links, bySource()));
        while (!linksLeft.isEmpty()) {
            Set<Link> entryLinks = entryLinks(linksLeft, linksBySources);
            for (Link link : entryLinks) {
                List<Link> group = newLinkedList();
                groupedLinks.add(group);
                Link currentLink = link;
                do {
                    linksLeft.remove(currentLink);
                    linksBySources.remove(currentLink.getSource(), currentLink);
                    group.add(currentLink);
                    currentLink = getFirst(linksBySources.get(currentLink.getTarget()), null);
                } while (currentLink != null);
            }
        }
        return groupedLinks;
    }



    private Set<Link> entryLinks(Set<Link> links, Multimap<DiagramElement, Link> linksBySources) {
        Multimap<DiagramElement, Link> entryLinksBySources = filterKeys(linksBySources, not(in(transform(links, byTarget()))));
        return copyOf(entryLinksBySources.values());
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
