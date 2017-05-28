package code.ponfee.view.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerTemplateUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final TemplateLoader SOURCE_TEMPLATE_LOADER = new SourceTemplateLoader();
    private static final TemplateLoader URL_TEMPLATE_LOADER = new ExtendedURLTemplateLoader();

    // 加载指定ftl文件的模板
    public static Template load4class(String basepackage, String name) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(Thread.currentThread().getStackTrace()[2].getClass(), basepackage);
        return getTemplate(cfg, name);
    }

    public static Template load4dir(String basedir, String name) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        try {
            cfg.setDirectoryForTemplateLoading(new File(basedir));
            return getTemplate(cfg, name);
        } catch (IOException e) {
            throw new RuntimeException(basedir + " not found", e);
        }
    }

    public static Template load4source(String source) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setTemplateLoader(SOURCE_TEMPLATE_LOADER);
        return getTemplate(cfg, source);
    }

    public static Template load4url(String url) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setTemplateLoader(URL_TEMPLATE_LOADER);
        return getTemplate(cfg, url);
    }

    public static void print(Template tpl, Object root, OutputStream out) {
        try {
            tpl.process(root, new PrintWriter(out));
            out.flush();
        } catch (TemplateException e) {
            throw new RuntimeException("template " + tpl.getName() + " process error", e);
        } catch (IOException e) {
            throw new RuntimeException("template " + tpl.getName() + " load error", e);
        }
    }

    private static Template getTemplate(Configuration cfg, String name) {
        cfg.setDefaultEncoding(DEFAULT_CHARSET);
        try {
            cfg.setLocalizedLookup(false);
            return cfg.getTemplate(name, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
