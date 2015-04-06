package com.SMScif.preferencias;

import com.SMScif.smscif.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenciaFragment extends PreferenceFragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Establecemos el xml de preferencias.
		addPreferencesFromResource(R.xml.preferencia);
	}
}
