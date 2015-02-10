package com.makasprzak.camel.visualizer;

import com.google.common.collect.ImmutableSet;
import com.makasprzak.camel.visualizer.model.Link;
import org.fest.assertions.Condition;
import org.junit.Test;

import java.util.List;

import static com.makasprzak.camel.visualizer.model.Activity.activity;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

public class LinksReducerTest {

    private final LinksReducer linksReducer = new LinksReducer();

    @Test
    public void shouldGroupTwoSerialLinksInOne() throws Exception {
        assertThat(
                linksReducer.groupLinks(ImmutableSet.of(
                        link("direct:a","direct:b"),
                        link("direct:b","direct:c")
                ))
        ).containsExactly(asList(
                link("direct:a", "direct:b"),
                link("direct:b", "direct:c")
        ));

    }

    @Test
    public void shouldGroupBranchToTwo() throws Exception {
        assertThat(
                linksReducer.groupLinks(ImmutableSet.of(
                        link("direct:a","direct:b"),
                        link("direct:b", "direct:c"),
                        link("direct:b", "direct:d")
                ))
        ).is(new Condition<List<?>>() {
            @Override
            public boolean matches(List<?> value) {
                return value.equals(asList(
                        asList(link("direct:a","direct:b"),link("direct:b", "direct:c")),
                        asList(link("direct:b","direct:d"))
                )) ||
                        value.equals(asList(
                                asList(link("direct:a","direct:b"),link("direct:b", "direct:d")),
                                asList(link("direct:b","direct:c"))
                        ));
            }
        });

    }

    private Link link(String from, String to) {
        return Link.Builder.link().withSource(activity(from)).withTarget(activity(to)).build();
    }
}