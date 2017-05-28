package code.ponfee.view.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.TemplateLoader;

public class HtmlFreeMarkerConfigurer extends FreeMarkerConfigurer {

    @Override
    protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {
        return new HtmlTemplateLoader(super.getAggregateTemplateLoader(templateLoaders));
    }

    private static class HtmlTemplateLoader implements TemplateLoader {

        public static final String ESCAPE_PREFIX = "<#ftl strip_whitespace=true><#escape x as x?html>";
        public static final String ESCAPE_SUFFIX = "</#escape>";

        private final TemplateLoader delegate;

        public HtmlTemplateLoader(TemplateLoader delegate) {
            this.delegate = delegate;
        }

        public Object findTemplateSource(String name) throws IOException {
            return delegate.findTemplateSource(name);
        }

        public long getLastModified(Object templateSource) {
            return delegate.getLastModified(templateSource);
        }

        public Reader getReader(Object templateSource, String encoding) throws IOException {
            Reader reader = delegate.getReader(templateSource, encoding);
            try {
                String templateText = IOUtils.toString(reader);
                return new StringReader(ESCAPE_PREFIX + templateText + ESCAPE_SUFFIX);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        public void closeTemplateSource(Object templateSource) throws IOException {
            delegate.closeTemplateSource(templateSource);
        }
    }
}
