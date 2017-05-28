package code.ponfee.view.web;

import code.ponfee.commons.exception.BasicException;

public class WebException extends BasicException {

    private static final long serialVersionUID = -4318846668197402130L;
    
    public WebException(int code, String message) {
        super(code, message);
    }

}
