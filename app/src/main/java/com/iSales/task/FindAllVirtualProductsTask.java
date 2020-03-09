package com.iSales.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.database.entry.ProduitEntry;
import com.iSales.database.entry.ServerEntry;
import com.iSales.interfaces.FindProductVirtualListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.ProductVirtual;
import com.iSales.remote.rest.FindProductVirtualREST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.harmony.awt.internal.nls.Messages.getString;


public class FindAllVirtualProductsTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = com.iSales.task.FindAllVirtualProductsTask.class.getSimpleName();
    private FindProductVirtualListener task;
    private Context context;
    private ProgressDialog mProgressDialog;
    private AppDatabase mDb;

    public FindAllVirtualProductsTask(Context context, FindProductVirtualListener taskComplete) {
        this.task = taskComplete;
        this.context = context;
        this.mProgressDialog = new ProgressDialog(this.context);
        this.mDb = AppDatabase.getInstance(this.context);

        //    mDb.debugMessageDao().insertDebugMessage(
        //            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindAllVirtualProductsTask.class.getSimpleName(), "FindAllVirtualProductsTask()", "Called.", ""));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgressDialog.setMessage("Mise a jour des produits virtuel en cours...");
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {


        // ###########################################################################################
        // LAST EDIT : telecharger les details des produits a partir de la bas de donnée + ecritur du ficher JSON
        // ###########################################################################################

        Log.e(TAG, " doInBackground : There are "+mDb.virtualProductDao().getAllVirtualProduct().size()+" to be deleted!");

        List<ProduitEntry> produitEntries = mDb.produitDao().getAllProduits();
        mDb.virtualProductDao().deleteAllVirtualProduct();

        int allProducts = produitEntries.size();

        final JSONArray productsList = new JSONArray();

        FileWriter writer = null;
        //Writer output = null;
        File directory = new File(Environment.getExternalStorageDirectory()+"/iSales_Produits");
        if(!directory.exists()){
            directory.mkdir();
        }

        File file_infos = new File(Environment.getExternalStorageDirectory()+"/iSales_Produits/produits_details.json");
        try {
            writer = new FileWriter(file_infos);
            //output = new BufferedWriter(new FileWriter(file_infos));
        } catch (IOException e) {

            Log.e(TAG, "Message : "+e.getMessage()+" \nStackTrace :"+e.getStackTrace());
        }

        String baseUrl = null;
        ServerEntry serverEntry = mDb.serverDao().getActiveServer(true);

        if (serverEntry == null) {
            return -1;
        }else{
            baseUrl = String.format("%s/", serverEntry.getHostname_img());
        }

        for (final ProduitEntry produit : produitEntries){

            final JSONObject products = new JSONObject();
            final JSONObject details = new JSONObject();

            Call<ArrayList<ProductVirtual>> call = ApiUtils.getISalesRYImg(context, baseUrl).ryFindProductVirtual(produit.getId());
            Response response = null;

            try {
                Log.e(TAG, "URL : "+call.request().url());
                response = call.execute();
                if (response.isSuccessful()){

                    try {
                        ArrayList<ProductVirtual> virtualArrayList = (ArrayList<ProductVirtual>) response.body();
                        if (virtualArrayList.size() > 0){
                            Log.e(TAG, "virtualArrayList size : "+virtualArrayList.size());

                            //calcule colis
                            double price0 = Double.parseDouble(produit.getPrice()) * Integer.parseInt(virtualArrayList.get(0).getQty());
                            double priceTTC0 = Double.parseDouble(produit.getPrice_ttc()) * Integer.parseInt(virtualArrayList.get(0).getQty());
                            virtualArrayList.get(0).setPrice("" + price0);
                            virtualArrayList.get(0).setPrice_ttc("" + priceTTC0);

                            details.put("Rowid_Colis",virtualArrayList.get(0).getRowid());
                            details.put("Ref_Colis",virtualArrayList.get(0).getRef());
                            details.put("Name_Colis", virtualArrayList.get(0).getLabel());
                            details.put("Price_Colis", virtualArrayList.get(0).getPrice());
                            details.put("Price_TTC_Colis", virtualArrayList.get(0).getPrice_ttc());
                            details.put("Quantite_Colis", virtualArrayList.get(0).getQty());
                            details.put("TVA_Colis", virtualArrayList.get(0).getTva_tx());
                            details.put("Stock_Colis", virtualArrayList.get(0).getStock());

                            //calcule palette et autres
                            for (int i = 0; i < virtualArrayList.size(); i++) {
                                double price = Double.parseDouble(virtualArrayList.get(i).getPrice()) * Integer.parseInt(virtualArrayList.get(i).getQty());
                                double priceTTC = Double.parseDouble(virtualArrayList.get(i).getPrice_ttc()) * Integer.parseInt(virtualArrayList.get(i).getQty());
                                virtualArrayList.get(i).setPrice("" + price);
                                virtualArrayList.get(i).setPrice_ttc("" + priceTTC);

                                details.put("Rowid_Palette",virtualArrayList.get(i).getRowid());
                                details.put("Ref_Palette",virtualArrayList.get(i).getRef());
                                details.put("Name_Palette", virtualArrayList.get(i).getLabel());
                                details.put("Price_Palette", virtualArrayList.get(i).getPrice());
                                details.put("Price_TTC_Palette", virtualArrayList.get(i).getPrice_ttc());
                                details.put("Quantite_Palette", virtualArrayList.get(i).getQty());
                                details.put("TVA_Palette", virtualArrayList.get(i).getTva_tx());
                                details.put("Stock_Palette", virtualArrayList.get(i).getStock());

                            }
                            //                        details.put("id_produit_unit : ",produit.getId());
                            //writer.append("\n#############################################\n");
                            //writer.flush();

                        }

                        products.put("id_product_unit",produit.getId());
                        products.put("Produit_details", details);
                        productsList.put(products);

                    }catch (Exception e){
                        Log.e(TAG, "********** Error CallBack Exception **********");
                        Log.e(TAG, "URL : "+call.request().url());
                        Log.e(TAG, " onFailure() "+e.getMessage());
                        e.printStackTrace();
                    }

                }else{
                    Log.e(TAG, "Not Successful");
                }


               if(allProducts>500){
                    Thread.sleep(250);
                }





            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            /*
            try{
                Log.e(TAG, "Try URL : "+call.request().url());
                call.enqueue(new Callback<ArrayList<ProductVirtual>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ProductVirtual>> call, Response<ArrayList<ProductVirtual>> response) {0
                        if (response.isSuccessful()){

                            try {
                                ArrayList<ProductVirtual> virtualArrayList = response.body();
                                if (virtualArrayList.size() > 0){
                                    Log.e(TAG, "virtualArrayList size : "+virtualArrayList.size());

                                    //calcule colis
                                    double price0 = Double.parseDouble(produit.getPrice()) * Integer.parseInt(virtualArrayList.get(0).getQty());
                                    double priceTTC0 = Double.parseDouble(produit.getPrice_ttc()) * Integer.parseInt(virtualArrayList.get(0).getQty());
                                    virtualArrayList.get(0).setPrice("" + price0);
                                    virtualArrayList.get(0).setPrice_ttc("" + priceTTC0);

                                    details.put("Rowid_Colis",virtualArrayList.get(0).getRowid());
                                    details.put("Ref_Colis",virtualArrayList.get(0).getRef());
                                    details.put("Name_Colis", virtualArrayList.get(0).getLabel());
                                    details.put("Price_Colis", virtualArrayList.get(0).getPrice());
                                    details.put("Price_TTC_Colis", virtualArrayList.get(0).getPrice_ttc());
                                    details.put("Quantite_Colis", virtualArrayList.get(0).getQty());
                                    details.put("TVA_Colis", virtualArrayList.get(0).getTva_tx());
                                    details.put("Stock_Colis", virtualArrayList.get(0).getStock());

                                    //calcule palette et autres
                                    for (int i = 0; i < virtualArrayList.size(); i++) {
                                        double price = Double.parseDouble(virtualArrayList.get(i).getPrice()) * Integer.parseInt(virtualArrayList.get(i).getQty());
                                        double priceTTC = Double.parseDouble(virtualArrayList.get(i).getPrice_ttc()) * Integer.parseInt(virtualArrayList.get(i).getQty());
                                        virtualArrayList.get(i).setPrice("" + price);
                                        virtualArrayList.get(i).setPrice_ttc("" + priceTTC);

                                        details.put("Rowid_Palette",virtualArrayList.get(i).getRowid());
                                        details.put("Ref_Palette",virtualArrayList.get(i).getRef());
                                        details.put("Name_Palette", virtualArrayList.get(i).getLabel());
                                        details.put("Price_Palette", virtualArrayList.get(i).getPrice());
                                        details.put("Price_TTC_Palette", virtualArrayList.get(i).getPrice_ttc());
                                        details.put("Quantite_Palette", virtualArrayList.get(i).getQty());
                                        details.put("TVA_Palette", virtualArrayList.get(i).getTva_tx());
                                        details.put("Stock_Palette", virtualArrayList.get(i).getStock());

                                    }
                                    //                        details.put("id_produit_unit : ",produit.getId());
                                    //writer.append("\n#############################################\n");
                                    //writer.flush();

                                }

                                products.put("id_product_unit",produit.getId());
                                products.put("Produit_details", details);
                                productsList.put(products);

                            }catch (Exception e){
                                Log.e(TAG, "********** Error CallBack Exception **********");
                                Log.e(TAG, "URL : "+call.request().url());
                                Log.e(TAG, " onFailure() "+e.getMessage());
                                e.printStackTrace();
                            }

                        }else{
                            Log.e(TAG, "not isSuccessful");
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ProductVirtual>> call, Throwable t) {
                        Log.e(TAG, "\n\n********** Error CallBack **********");
                        Log.e(TAG, "URL : "+call.request().url());
                        Log.e(TAG, " onFailure() Message : "+t.getMessage());
                        Log.e(TAG, " onFailure() Cause : "+t.getCause());
                        t.getStackTrace();

                        call.cancel();
                    }
                });

            }catch (Exception e){
                Log.e(TAG, "Message : "+e.getMessage()+" \n");
                e.printStackTrace();
            }
            */

        }

        try {
            writer.write(productsList.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    String toJSON(ArrayList<ProductVirtual> list) {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        for(ProductVirtual d : list) {
            sb.append(gson.toJson(d)+"");
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        task.onFindProductVirtualCompleted(integer);
    }
}
