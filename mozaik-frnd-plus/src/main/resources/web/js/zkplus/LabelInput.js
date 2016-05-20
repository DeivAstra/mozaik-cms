zkplus.LabelInput = zk.$extends(zul.inp.InputWidget, {
	_label : '',
	 
    getLabel : function() {
        return this._label;
    },
 
    setLabel : function(label) {
        if (this._label != label) {
            this._label = label;
            if (this.desktop)
                this.$n().innerHTML = zUtl.encodeXML(label);
        }
    }
});