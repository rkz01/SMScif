package com.SMScif.auxiliares;

import java.util.ArrayList;

import com.SMScif.preferencias.PreferenciaActivity;
import com.SMScif.smscif.ListaAdaptador;
import com.SMScif.smscif.R;
import com.SMScif.smscif.RedactarActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class Ayuda extends Activity {
	private ListView listaTemas;
	private ArrayList<DatosListaAyuda> temasAyuda = new ArrayList<DatosListaAyuda>();
	private String idTemaSeleccionado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparesca barra de titulo
		setContentView(R.layout.activity_ayuda);  
				 
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");	
        //TextView textoTema = (TextView) findViewById(R.id.textTemaAyuda); 
        TextView titulo = (TextView) findViewById(R.id.textTituloAyuda);
        //textoTema.setTypeface(fontRegular);
        titulo.setTypeface(fontMedium);

		 temasAyuda.add(new DatosListaAyuda("1"));
		 temasAyuda.add(new DatosListaAyuda("2"));
		 temasAyuda.add(new DatosListaAyuda("3"));
		 temasAyuda.add(new DatosListaAyuda("4"));
		 temasAyuda.add(new DatosListaAyuda("5"));
			
	     listaTemas = (ListView) findViewById(R.id.listViewHelp);
	     listaTemas.setAdapter(new ListaAdaptador(this,R.layout.ayuda_datos,temasAyuda){
			@Override
			public void onEntrada(Object entrada, View view) {
		        if (entrada != null) {
		        	TextView textoTema = (TextView) view.findViewById(R.id.textTemaAyuda);
		        	TextView icono = (TextView) view.findViewById(R.id.icono);
		        	Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia 
		        	textoTema.setTypeface(fontRegular);
		        	
		            if (textoTema != null)
		            	
		            	if((((DatosListaAyuda)entrada).temaAyuda.equals("1"))) {
		            		textoTema.setText(R.string.text_help_list1);
		            		icono.setBackgroundResource(R.drawable.ic_launcher);
	            		   }
		                if((((DatosListaAyuda)entrada).temaAyuda.equals("2"))) {
		                	textoTema.setText(R.string.text_help_list2); 
		                	icono.setBackgroundResource(R.drawable.ic_main_sms);
		               	   }
		                if((((DatosListaAyuda)entrada).temaAyuda.equals("3"))) {
		                	textoTema.setText(R.string.text_help_list3);
		                	icono.setBackgroundResource(R.drawable.ic_main_chat);
	            		   }
		                if((((DatosListaAyuda)entrada).temaAyuda.equals("4"))) {
		                	textoTema.setText(R.string.text_help_list4);
		                	icono.setBackgroundResource(R.drawable.ic_main_cipher);
		               	   }
		                if((((DatosListaAyuda)entrada).temaAyuda.equals("5"))) {
		                	textoTema.setText(R.string.text_help_list5); 
		                	icono.setBackgroundResource(R.drawable.ic_main_cipher);
			               }
		        }
			}
			
		});

		
	     listaTemas.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
				DatosListaAyuda temaAyudaElegido = (DatosListaAyuda)pariente.getItemAtPosition(posicion);
				idTemaSeleccionado = temaAyudaElegido.temaAyuda;
				abrirTemaAyuda();
			}		
		}); 		
	}
	
	public void abrirTemaAyuda(){
		Intent temaAyuda = new Intent(this,TemaAyuda.class);
		temaAyuda.putExtra("idTemaSeleccionado", idTemaSeleccionado);
		startActivity(temaAyuda);
		overridePendingTransition(R.anim.left_in,R.anim.left_out);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ayuda, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	/*Opciones elegidas desde el menu*/
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_configuracion:
	        	Intent preferencias = new Intent(this,PreferenciaActivity.class);
	        	startActivity(preferencias);
	        	overridePendingTransition(R.anim.left_in,R.anim.left_out);	//Animacion al cambiar de vista(animacion de la nueva vista, animacion de esta vista que sale)
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	/**************************************Para animacion al presionar boton regresar*************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.right_in,R.anim.right_out);
			return true;
		}
		else
		return super.onKeyDown(keyCode, event);		
		
	}
	
	/************************Presionar back************/
	public void cerrarAyuda(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}

}

