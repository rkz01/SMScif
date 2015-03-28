package com.SMScif.cifraExixtentes;

public class CifrarContactosDatos{
   public String textoNombre = null;
   public String textoNumero = null;
   public String textoHilo = null;
   boolean selected= false;


public CifrarContactosDatos(String numeroSeleccionado,String nombreSeleccionado,String hilo,boolean selected){
	this.textoNombre = nombreSeleccionado;
	this.textoNumero = numeroSeleccionado;	
	this.textoHilo = hilo;	
	this.selected = selected;
}	

public String getNum() {
	return textoNumero;
}

public String getNom() {
	return textoNombre;
}

public String getHilo() {
	return textoHilo;
}


public boolean isSelected() {
	return selected;
}
public void setSelected(boolean selected) {
	this.selected = selected;
}	
}
