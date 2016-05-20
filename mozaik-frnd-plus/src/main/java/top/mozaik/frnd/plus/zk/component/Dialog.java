/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.plus.zk.component;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class Dialog {
	
	private static String exToString(Throwable e){
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(baos);
		e.printStackTrace(writer);
		writer.flush();
		writer.close();
		return baos.toString();
	}
	
	public static void error(String title, Throwable e){
		Messagebox.show(exToString(e), title,
			Messagebox.OK, Messagebox.ERROR);
	}
	
	public static void error(String title, String message){
		Messagebox.show(message, title,
			Messagebox.OK, Messagebox.ERROR);
	}
	
	public static void info(String title, String message){
		Messagebox.show(message, title,
			Messagebox.OK, Messagebox.INFORMATION);
	}
	
	
	public static void confirm(String title, String message, final Confirmable c){
				
		Messagebox.show(message, title,
			Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, 
			new EventListener<Event>(){
				public void onEvent(final Event event) {
					if(event.getName().equals(Messagebox.ON_YES)) {
						c.onConfirm();
					} else {
						c.onCancel();
					}
				}
			}
		);
	}
	
	public static void confirm(String title, String message, final Confirmable c, Component ui){
		final Window wnd = new Window(title, "normal", true);
		wnd.setWidth("450px");
		
		wnd.setPage(Executions.getCurrent().getDesktop().getFirstPage());
		
		final Vlayout layout = new Vlayout();
		layout.setStyle("padding:20px");
		layout.setSpacing("7px");
		final Label titleLable = new Label(message);
		titleLable.setStyle("font-weight:bold");
		layout.appendChild(titleLable);
		if(ui != null) {
			layout.appendChild(ui);
		}
		
		final Div btnsArea = new Div();
		btnsArea.setStyle("text-align:center");
		final Button okBtn = new Button("Да");
		okBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event arg0) throws Exception {
				wnd.detach();
				c.onConfirm();
			}
		});
		
		final Button cancelBtn = new Button("Отмена");
		cancelBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event arg0) throws Exception {
				wnd.detach();
				c.onCancel();
			}
		});
		btnsArea.appendChild(okBtn);
		btnsArea.appendChild(cancelBtn);
		
		layout.appendChild(btnsArea);
		wnd.appendChild(layout);
		wnd.doModal();
	}
	
	public static void confirm2(String title, String message, final Confirmable c){
		
		final Messagebox.Button[] btns = { Messagebox.Button.YES, Messagebox.Button.CANCEL };
		final String[] labels = { "Да", "Отмена" };
		
		final Messagebox.Button clickedBtn = Messagebox.show(message, title,
				btns, labels, Messagebox.QUESTION, Messagebox.Button.YES,  (EventListener) null);		
		if(clickedBtn == Messagebox.Button.YES) {
			c.onConfirm();
		} else {
			//c.onCancel();
			c.onConfirm();
		}
	}
	
	public static interface Confirmable {
		void onConfirm();
		void onCancel();
	}
}
