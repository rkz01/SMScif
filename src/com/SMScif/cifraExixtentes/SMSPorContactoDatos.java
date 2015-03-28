package com.SMScif.cifraExixtentes;

public class SMSPorContactoDatos {

	public String textoId = null;
	public String textoThread = null;
	public String textoFecha = null;
	public String textoCuerpo = null;
	public String textoNumero = null;
	public String textoPerson = null;
	boolean selected = false;
	
	public SMSPorContactoDatos(String idt, String threadt, String fechat, String cuerpot, String numerot, String personat, boolean selected) {
		this.textoId = idt;
		this.textoThread = threadt; 
		this.textoFecha = fechat;
		this.textoCuerpo = cuerpot;
		this.textoNumero = numerot;
		this.textoPerson = personat;
		this.selected = selected;
	} 


	public String getId() {
		return textoId;
	}
 
	public String getFecha() {
		return textoFecha;
	}
 
	public String getThread() {
		return textoThread;
	}

	public String getCuerpo() {
		return textoCuerpo;
	}
	
	public String getPersona() {
		return textoPerson;
	}
	
	public String getNumero() {
		return textoNumero;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
