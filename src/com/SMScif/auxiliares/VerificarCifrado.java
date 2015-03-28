package com.SMScif.auxiliares;

public class VerificarCifrado {
	public boolean verificarSMSCifrado(String texto){
		boolean estaCifrado = true;
		int tam = texto.length();
		
		for(int i=0; i<tam; i++){
			if(texto.charAt(i) == ' '){	//ya que un sms cifrado no contiene espacios
				estaCifrado = false;
			}
		}
		return estaCifrado;
	}
}
