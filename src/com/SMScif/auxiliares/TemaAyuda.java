package com.SMScif.auxiliares;

import com.SMScif.smscif.R;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class TemaAyuda extends Activity {   
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//No aparezca barra de titulo
		setContentView(R.layout.activity_tema_ayuda);  

		Bundle extras = getIntent().getExtras();
		String idTemaSeleccionado = extras.getString("idTemaSeleccionado"); 
		
		Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");	//tipografia  
		Typeface fontMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");	
		TextView textoTitulo = (TextView)findViewById(R.id.textTituloTemaAyuda);
		textoTitulo.setTypeface(fontMedium);
	    TextView textoTema = (TextView)findViewById(R.id.txTemaAyuda); 
	    textoTema.setTypeface(fontRegular);
				 
		 if(idTemaSeleccionado.equals("1")) {
			 textoTitulo.setText(R.string.text_help_list1); 
			 textoTema.setText(R.string.text_help_tema1);
 		     }
         if(idTemaSeleccionado.equals("2")) {
        	 textoTitulo.setText(R.string.text_help_list2);
        	 textoTema.setText(R.string.text_help_tema2);
        	 }
         if(idTemaSeleccionado.equals("3")) {
        	 textoTitulo.setText(R.string.text_help_list3);
        	 textoTema.setText(R.string.text_help_tema3);
             }
         if(idTemaSeleccionado.equals("4")) {
        	 textoTitulo.setText(R.string.text_help_list4); 
        	 textoTema.setText(R.string.text_help_tema4);
        	 }
         if(idTemaSeleccionado.equals("5")) {
        	 textoTitulo.setText(R.string.text_help_list5); 
        	 textoTema.setText(R.string.text_help_tema5);
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
	/*********************************************************/
	public void cerrarTemaAyuda(View view){
		finish();
		overridePendingTransition(R.anim.right_in,R.anim.right_out);
	}
	
}
