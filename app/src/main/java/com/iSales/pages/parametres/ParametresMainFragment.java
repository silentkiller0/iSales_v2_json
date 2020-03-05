package com.iSales.pages.parametres;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugSettingsEntry;
import com.iSales.database.entry.SettingsEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParametresMainFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = ParametresMainFragment.class.getSimpleName();

    private AppDatabase db;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    private String getServeurHostname(){
        /*
         * Returns food (France Food Company) || soifexpress (Soif Express) || asiafood (Asia Food) || bdc (BDC)
         */
        String hostname = db.serverDao().getActiveServer(true).getHostname();
        String new_str;
        new_str = hostname.replace("http://"," ");
        return new_str.replace(".apps-dev.fr/api/index.php"," ");
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add 'general' preferences, defined in the XML file
        db = AppDatabase.getInstance(getContext());

        addPreferencesFromResource(R.xml.preference_parametres_main);

        SettingsEntry config = db.settingsDao().getAllSettings().get(0);

        SharedPreferences.Editor prefs_1 = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefs_1.putBoolean("parametres_activer_description", config.isShowDescripCataloge());
        prefs_1.commit();

        Preference mSwith = findPreference("parametres_activer_synchronisation_ProduitVirtuel");

        Log.e(TAG, "Hostname : "+getServeurHostname());
        if (getServeurHostname().contains("food")) {
            SharedPreferences.Editor prefs_2 = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefs_2.putBoolean("parametres_activer_synchronisation_ProduitVirtuel", config.isEnableVirtualProductSync());
            prefs_2.commit();
            mSwith.setVisible(true);
        }else{
            mSwith.setVisible(false);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged: ");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.e(TAG, "onSharedPreferenceChanged: ");

        //Activer / Desactiver la description du produit dans le catalogue
        if (sharedPreferences.getBoolean("parametres_activer_description", true)){

            SettingsEntry config = db.settingsDao().getAllSettings().get(0);
            config.setShowDescripCataloge(true);
            db.settingsDao().updateSettings(config);

            Toast.makeText(getContext(), "Activé !", Toast.LENGTH_SHORT).show();
        }else{
            SettingsEntry config = db.settingsDao().getAllSettings().get(0);
            config.setShowDescripCataloge(false);
            db.settingsDao().updateSettings(config);

            Toast.makeText(getContext(), "Désactivé !", Toast.LENGTH_SHORT).show();
        }

        //Activer / Desactiver la synchronisation des produits virtuel
        if (sharedPreferences.getBoolean("parametres_activer_synchronisation_ProduitVirtuel", true)){

            SettingsEntry config = db.settingsDao().getAllSettings().get(0);
            config.setEnableVirtualProductSync(true);
            db.settingsDao().updateSettings(config);
            Toast.makeText(getContext(), "Activé !", Toast.LENGTH_SHORT).show();
        }else{

            SettingsEntry config = db.settingsDao().getAllSettings().get(0);
            config.setEnableVirtualProductSync(false);
            db.settingsDao().updateSettings(config);
            Toast.makeText(getContext(), "Désactivé !", Toast.LENGTH_SHORT).show();
        }
    }
}
