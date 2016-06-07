package top.mozaik.frnd.admin.vm.access.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.enums.E_UserRole;
import top.mozaik.bknd.api.model.User;
import top.mozaik.bknd.api.service.UserService;
import top.mozaik.frnd.plus.zk.component.Dialog;
import top.mozaik.frnd.plus.zk.component.Notification;
import top.mozaik.frnd.plus.zk.converter.DateToStringConverter;
import top.mozaik.frnd.plus.zk.event.I_CUDEventHandler;
import top.mozaik.frnd.plus.zk.event.ListboxCUDEventHandler;
import top.mozaik.frnd.plus.zk.vm.BaseVM;

public class UsersVM extends BaseVM {
	
	private final UserService userService = ServicesFacade.$().getUserService();
	
	private final DateToStringConverter dateConverter = new DateToStringConverter("yyyy-MM-dd HH:mm");
	
	private I_CUDEventHandler<User> eventHandler;
	
	@Wire
	Listbox userListbox;
	
	@AfterCompose(superclass=true)
	public void doAfterCompose() {
		
		eventHandler = new ListboxCUDEventHandler<User>(userListbox){
			@Override
			public void onCreate(User v) {
				//super.onCreate(v);
				UsersVM.this.reloadComponent();
			}
		};
	}
	
	/// BINDING ///
	
	public List<User> getUserList() {
		return userService.readAll();
	}
	
	public DateToStringConverter getDateConverter() {
		return dateConverter;
	}
	
	/// COMMANDS ///
	
	@Command
	public void create() {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/access/user/createUser.wnd.zul", null, args);
	}
	
	@Command
	public void edit(@BindingParam("bean") User bean) {
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("bean", bean);
		args.put("eventHandler", eventHandler);
		Executions.createComponents("/WEB-INF/zul/access/user/editUser.wnd.zul", null, args);
	}
	
	@Command
	public void remove() {
		final Listitem item = userListbox.getSelectedItem();
		if(item == null) return;
		
		final User bean = item.getValue();
		
		// CHECK IF LAST ADMIN
		if(bean.getRole() == E_UserRole.ADMIN) {
			final List<User> admins = userService.read(new User().setRole(E_UserRole.ADMIN));
			if(admins.size() <= 1) {
				Notification.showError("You can't delete last ADMIN user");
				return;
			}
		}
		
		final StringBuilder msg = new StringBuilder("User '")
			.append(bean.getLogin()).append("' will be removed. Continue?");
		
		Dialog.confirm("Remove User", msg.toString(), new Dialog.Confirmable() {
			@Override
			public void onConfirm() {
				try {
					userService.delete1(bean);
					eventHandler.onDelete(bean);
					Notification.showMessage("User removed succesfully");
				} catch (Exception e) {
					Dialog.error("Error occured while remove User: " + bean, e);
					e.printStackTrace();
				}
			}
			@Override
			public void onCancel() {}
		});
	}
	
	@Command
	@NotifyChange("userList")
	public void refresh() {
	}
}