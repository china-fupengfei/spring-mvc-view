package code.ponfee.view.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import freemarker.cache.StringTemplateLoader;

class SourceTemplateLoader extends StringTemplateLoader {
    private static final String NAME_PREFIX = "source_";

    @Override
    public Object findTemplateSource(String source) {
        String name = NAME_PREFIX + md5(source);
        Object tpl = super.findTemplateSource(name);
        if (tpl == null) {
            super.putTemplate(name, source, 0);
            tpl = super.findTemplateSource(name);
        }
        return tpl;
    }

    private static String md5(String data) {
        try {
            byte[] encode = MessageDigest.getInstance("MD5").digest(data.getBytes());
            return new BigInteger(1, encode).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
    }

}
