package de.hhu.thesis_jensclicker.utility;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownRenderer {
    public static String render(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer
                .builder()
                .sanitizeUrls(true)
                .escapeHtml(true)
                .build();
        return renderer.render(document);
    }
}
