package com.makasprzak.camel.visualizer;

import com.makasprzak.camel.visualizer.model.Link;
import org.junit.Test;

import static com.makasprzak.camel.visualizer.model.Activity.activity;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

public class LinksToNodesTransformerTest {

    private final LinksToNodesTransformer transformer = new LinksToNodesTransformer();

    @Test
    public void shouldTransformTree() throws Exception {
        assertThat(transformer.transformToElements(
                asList(
                    asList(link("a", "b"), link("b","c"), link("c", "d")),
                    asList(link("a1","b1"),link("b1","c")),
                    asList(link("c","d1"))
                )
        )).isEqualTo(
                asList(
                        asList(activity("a"),activity("b"), activity("c"), activity("d")),
                        asList(activity("a1"),activity("b1"),activity("c")),
                        asList(activity("c"),activity("d1"))
                )
        );

    }

    private Link link(String s, String t) {
        return Link.Builder.link().withSource(activity(s)).withTarget(activity(t)).build();
    }
}