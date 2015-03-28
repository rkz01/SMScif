/*
 * SMScif programa de servicio de mensajes de texto que cifra y descifra su contenido          
 * 
 */

package com.SMScif.smscif;

import com.SMScif.auxiliares.Ayuda;
import com.SMScif.cifraExixtentes.CifrarContactos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo
		setContentView(R.layout.activity_main);
		
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
		((TextView) findViewById(R.id.textNameApp)).setTypeface(fontMedium);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void redactar(View view){   
    	String textoVacio = "";//envia a redactar ya que abre por primera vez
    	String numeroVacio = ""; //envia a redectar 
    	String personaVacio = "";
    	Intent abrirRedactar = new Intent(this,RedactarActivity.class); //evento llama layout
    	abrirRedactar.putExtra("sms", textoVacio);//aqui esta vacio ya que es el inicio, mensaje plano no existe 
    	abrirRedactar.putExtra("smsCifrado", textoVacio);//aqui esta vacio ya que es el inicio, mensaje cifrado no existe qui
    	abrirRedactar.putExtra("numero", numeroVacio);//esta vacio numero 
    	abrirRedactar.putExtra("nombrePersona", personaVacio);//esta vacio nombre contacto 
    	startActivity(abrirRedactar);   
    	overridePendingTransition(R.anim.left_in,R.anim.left_out);	//Animacion al cambiar de vista(animacion de la nueva vista, animacion de esta vista que sale)
    }
    
    public void leer(View view){   
    	Intent abrirSms = new Intent(this,ContactosConversacion.class);
    	startActivity(abrirSms);
    	overridePendingTransition(R.anim.left_in,R.anim.left_out);   
    }
    
    public void leerSMSporContacto(View view){
    	Intent abrirSmsContacto = new Intent(this,CifrarContactos.class);
    	abrirSmsContacto.putExtra("indicador", false);
    	startActivity(abrirSmsContacto);
    	overridePendingTransition(R.anim.left_in,R.anim.left_out);
    }
  
    public void abrirAyuda(View view){
    	Intent ayuda = new Intent(this,Ayuda.class);
    	//ayuda.putExtra("indicador", false);
    	startActivity(ayuda);
    	overridePendingTransition(R.anim.left_in,R.anim.left_out);
    }   
        
    public void salir(View view){
    	finish();
    }
    
}
