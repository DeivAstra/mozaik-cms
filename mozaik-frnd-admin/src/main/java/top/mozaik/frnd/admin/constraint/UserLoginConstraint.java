package top.mozaik.frnd.admin.constraint;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.SimpleConstraint;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.model.User;
import top.mozaik.bknd.api.service.UserService;

public class UserLoginConstraint implements Constraint {
    
    private static final SimpleConstraint noEmptyConstraint = new SimpleConstraint("no empty");
    
    private final UserService userService = ServicesFacade.$().getUserService();
    
    private String login;
    
    @Override
    public void validate(Component comp, Object value)
            throws WrongValueException {
        
        noEmptyConstraint.validate(comp, value);
        
        final String login = (String) value;
        
        if(login.matches("[A-Za-z0-9_]+")) {
        	throw new WrongValueException("Login '" + login + 
					"' is not valid. Only A-Z a-z _ are allowed.");
        }
        
        // skip if initial login eq compared login
        if(login.equalsIgnoreCase(this.login)) return;
        
        try {
            final User user = userService.read1(new User().setLogin(login));
            if(user != null) {
            	throw new WrongValueException(comp, 
                        (!user.isActive()?"Not active user":"User")+" with same login already exists");
            }
        } catch (Exception e) {
            throw new WrongValueException(comp, e.getMessage());
        }
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
}