package code.ponfee.view.auth;

/**
 * 响应类型
 *
 * @author fupf
 */
public enum Type {

    JSON("application/json"), HTML("text/html"), PLAIN("text/plain"), REDIRECT, FORWARD, ALERT, TOP;

    private String media;

    private Type() {}

    private Type(String media) {
        this.media = media;
    }

    public String media() {
        return media;
    }

}
