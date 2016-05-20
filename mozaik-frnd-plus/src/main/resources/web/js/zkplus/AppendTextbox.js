zkplus.AppendTextbox = zk.$extends(zul.inp.Textbox, {
	 
    appendText : function(text) {
        this.setValue(this.getValue()+text);
    }
});