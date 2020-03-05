package com.iSales.utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.model.ISalesIncidentTable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

/**
 * Created by netserve on 30/08/2018.
 */

public final class ISalesUtility {
    private static final String TAG = com.iSales.utility.ISalesUtility.class.getSimpleName();
    public static String ENCODE_IMG = "&img";
    public static String ENCODE_DESC = "&desc";
    private static String ENCODE_CAROUSEL = "&amp;carousel;";

    public static String CURRENCY = "€";
    public static String ISALES_PATH_FOLDER = "iSales";
    public static String ISALES_PRODUCTS_IMAGESPATH_FOLDER = "iSales/iSales Produits";
    public static String ISALES_CUSTOMER_IMAGESPATH_FOLDER = "iSales/iSales Clients";

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 296);
        return noOfColumns;
    }

    public static int calculateNoOfColumnsCmde(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 320);
        return noOfColumns;
    }

    // Renvoi le nom de l'image du produit a partir de la description
    public static String getImgProduit(String description) {
        if (description == null || description.isEmpty()) {
            return null;
        }

//        Log.e(TAG, "getImgProduit: description="+description);
        // extraction de la chaine apres code encodage de la photo
        String[] descriptionTab = description.split(ENCODE_IMG);
//        Log.e(TAG, "getImgProduit: descriptionTab before length="+descriptionTab.length);
        // S'il n'ya pas d'encode de img, on renvoi null
        if (descriptionTab.length < 2) {
            return null;
        }
//        Log.e(TAG, "getImgProduit: descriptionTab after length="+descriptionTab.length);
        // extraction de la chaine avant code ':&'
        /*String[] imgTab = descriptionTab[1].split(":&");
        if (imgTab.length <= 1) {
            return null;
        }*/
        // console.log(this.TAG, "descriptionTab:getImgProduit ", descriptionTab);
        // console.log(this.TAG, "descriptionTab:imgTab ", imgTab);
        return descriptionTab[1];
    }

    // Renvoi le nom de l'image du produit a partir de la description
    public static String getDescProduit(String description) {
        return description;

        /*if (description == null || description.isEmpty()) {
            return null;
        }

//        Log.e(TAG, "getDescProduit: description="+description);
        // extraction de la chaine apres code encodage de la photo
        String[] descriptionTab = description.split(ENCODE_DESC);
//        Log.e(TAG, "getDescProduit: descriptionTab before length="+descriptionTab.length);
        // S'il n'ya pas d'encode de img, on renvoi null
        if (descriptionTab.length < 2) {
            return null;
        }
//        Log.e(TAG, "getImgProduit: descriptionTab after length="+descriptionTab.length);
        // extraction de la chaine avant code ':&'
        /*String[] imgTab = descriptionTab[1].split(":&");
        if (imgTab.length <= 1) {
            return null;
        }*/
        // console.log(this.TAG, "descriptionTab:getImgProduit ", descriptionTab);
        // console.log(this.TAG, "descriptionTab:imgTab ", imgTab);

//        return descriptionTab[1];
    }

    // Renvoi le nom les images carousel du produit a partir de la description
    public static String[] getCarouselProduit(String description) {

        String[] carousel;
        // extraction de la chaine apres code encodage de la photo
        String[] descriptionTab = description.split(ENCODE_CAROUSEL);
        Log.e(TAG, "descriptionTab:getCarouselProduit " + descriptionTab.toString());
        // S'il n'ya pas d'encode de img, on renvoi null
        if (descriptionTab.length <= 1) {
            return null;
        }
        // extraction de la chaine avant code ':'
        String[] carouselTab = descriptionTab[1].split(":&");
        Log.e(TAG, "descriptionTab:carouselTab " + carouselTab.toString());
        if (carouselTab.length <= 1) {
            return null;
        }
        carousel = carouselTab[0].split(":");
        return carousel;
    }

    //    Arromdi les bord d'une image bitmap
    public static Bitmap getRoundedCornerBitmap(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    public static String strCapitalize(String str) {
        String name = str;
        if (name.length() >= 2) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        return name;
    }

    public static String amountFormat2(String value) {
        Log.e(TAG, "amountFormat2( "+value+" )");
        if(value != null){
            if (value != ""){
                double valueDouble = Double.parseDouble(value);
                String str = String.format(Locale.FRANCE,
                        "%,-10.2f", valueDouble);
                return String.valueOf(str).replace(" ", "");
            }else{
                //the value is empty so send a 0
                return "0";
            }
        }else{
            return "0";
        }
    }

    public static String roundOffTo2DecPlaces(String value) {
        double valueDouble = Double.parseDouble(value);
        return String.format("%.2f", valueDouble);
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * returns the bytesize of the give bitmap
     */
    public static int bitmapByteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static String getFilename(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static final void makeSureFileWasCreatedThenMakeAvailable(Context context, File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.toString()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e(TAG, "onScanCompleted: Scanned=" + path);
                        Log.e(TAG, "onScanCompleted: uri=" + uri);
                    }
                });
    }

    private static final String getCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatted = simpleDateFormat.format(calendar.getTime());
        return formatted;
    }

//    enregistre la photo d'un produit en loca
    public static final String saveProduitImage(Context context, Bitmap imageToSave, String filename) {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), ISALES_PRODUCTS_IMAGESPATH_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
                Log.e(TAG, "saveProduitImage: folder created" );
            }
        }

        File file = new File(dir, String.format("%s.jpg", filename, currentDateAndTime));

        if (file.exists ()) file.delete();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
            makeSureFileWasCreatedThenMakeAvailable(context, file);

            return file.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "saveProduitImage:FileNotFoundException "+e.getMessage() );
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveProduitImage:IOException "+e.getMessage() );
            return null;
        }

    }

//    enregistre la photo d'un produit en loca
    public static final Target getTargetSaveProduitImage(final String filename) {
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String currentDateAndTime = getCurrentDateAndTime();
                        File dir = new File(Environment.getExternalStorageDirectory(), ISALES_PRODUCTS_IMAGESPATH_FOLDER);
                        if (!dir.exists()) {
                            if (dir.mkdirs()){
                                Log.e(TAG, "saveProduitImage: folder created" );
                            }
                        }

                        File file = new File(dir, String.format("%s.jpg", filename, currentDateAndTime));

                        if (file.exists ()) file.delete();

                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                            fos.flush();
                            fos.close();
//                            makeSureFileWasCreatedThenMakeAvailable(context, file);

//                            return file.getAbsolutePath();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e(TAG, "saveProduitImage:FileNotFoundException "+e.getMessage() );
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "saveProduitImage:IOException "+e.getMessage() );
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

    }

//    enregistre la photo d'un client en loca
    public static final String saveClientImage(Context context, Bitmap imageToSave, String filename) {
        String currentDateAndTime = getCurrentDateAndTime();
        File dir = new File(Environment.getExternalStorageDirectory(), ISALES_CUSTOMER_IMAGESPATH_FOLDER);
        if (!dir.exists()) {
            if (dir.mkdirs()){
//                Log.e(TAG, "saveClientImage: folder created" );
            }
        }

        File file = new File(dir, String.format("%s.jpg", filename, currentDateAndTime));

        if (file.exists ()) file.delete();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
            makeSureFileWasCreatedThenMakeAvailable(context, file);

            return file.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "saveClientImage:FileNotFoundException "+e.getMessage() );
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveClientImage:IOException "+e.getMessage() );
            return null;
        }

    }

    public static final void deleteProduitsImgFolder() {
        File myDir = new File(Environment.getExternalStorageDirectory(), ISALES_PRODUCTS_IMAGESPATH_FOLDER);
        if (myDir.isDirectory() && myDir.list() != null) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    public static final void deleteClientsImgFolder() {
        File myDir = new File(Environment.getExternalStorageDirectory(), ISALES_CUSTOMER_IMAGESPATH_FOLDER);
        if (myDir.isDirectory() && myDir.list() != null) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    public static final String getStatutCmde(int statut) {
        String str = "";
        switch (statut) {
            case 0:
                str = "Brouillon";
                break;
            case 1:
                str = "Validée";
                break;
            case 2:
                str = "En Cours";
                break;
            case 3:
                str = "Livrée";
                break;
        }
        return str;
    }

    public static final int getStatutCmdeColor(int statut) {
        int color = R.color.colorGray;
        switch (statut) {
            case 0:
                color = R.color.colorGray;
                break;
            case 1:
                color = R.color.colorWarning;
                break;
            case 2:
                color = R.color.colorGray;
                break;
            case 3:
                color = R.color.colorGreen;
                break;
        }
        return color;
    }

    public File generateAttchmentFile(Context context){
        AppDatabase db = AppDatabase.getInstance(context);

        //get all logs
        String logDataText = "";
        String logDataClassName = "";
        boolean firstDebugMessage = true;
        List<DebugItemEntry> debugList = db.debugMessageDao().getAllDebugMessages();
        for (int i=0; i<debugList.size(); i++){
            if (logDataClassName.equals(debugList.get(i).getClassName())){
                logDataText += DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date(debugList.get(i).getDatetimeLong()*1000)).toString() + " | Mask: " + debugList.get(i).getMask() + "\n" +
                        "Class: " + debugList.get(i).getClassName() + " => Method: " +debugList.get(i).getMethodName() + "\n" +
                        "Message: "+debugList.get(i).getMessage()+"\nStraceStack: "+debugList.get(i).getStackTrace()+"\n";
            }else{
                logDataClassName = debugList.get(i).getClassName();
                if(firstDebugMessage){
                    logDataText += DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date(debugList.get(i).getDatetimeLong()*1000)).toString() + " | Mask: " + debugList.get(i).getMask() + "\n" +
                            "Class: " + debugList.get(i).getClassName() + " => Method: " +debugList.get(i).getMethodName() + "\n" +
                            "Message: "+debugList.get(i).getMessage()+"\nStraceStack: "+debugList.get(i).getStackTrace()+"\n";
                    firstDebugMessage = false;
                }else{
                    logDataText += "\n\n"+DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date(debugList.get(i).getDatetimeLong()*1000)).toString() + " | Mask: " + debugList.get(i).getMask() + "\n" +
                            "Class: " + debugList.get(i).getClassName() + " => Method: " +debugList.get(i).getMethodName() + "\n" +
                            "Message: "+debugList.get(i).getMessage()+"\nStraceStack: "+debugList.get(i).getStackTrace()+"\n";
                }
            }
        }

        //create log file
        File dir = new File(Environment.getExternalStorageDirectory(), "iSales/iSales Logs");
        if (!dir.exists()) {
            if (dir.mkdirs()){
                Log.e(TAG, "Log folder created" );
            }
        }

        File file = new File(dir, String.format("%s.txt", "iSales_logs_"+(System.currentTimeMillis()/1000)));

        if (file.exists ()) file.delete();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(logDataText.getBytes());

            fos.flush();
            fos.close();
            makeSureFileWasCreatedThenMakeAvailable(context, file);

            file.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "saveProduitImage:FileNotFoundException "+e.getMessage() );

            //send email with ticket of exception
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveProduitImage:IOException "+e.getMessage() );

            //send email with ticket of exception
        }
        return file;
    }

    public static String[][] getAlliSalesPriorityTable(){
        String[][] table = new String[6][3];
        table[0][0] = "P0"; table[0][1] = "#000000"; table[0][2] = "Votre ticket sera traité dans 24 heurs ouvrables";
        table[1][0] = "P1"; table[1][1] = "#FF0000"; table[1][2] = "Votre ticket sera traité dans 24 heurs ouvrables";
        table[2][0] = "P2"; table[2][1] = "#FF4400"; table[2][2] = "Votre ticket sera traité dans 1-2 jours ouvrables";
        table[3][0] = "P3"; table[3][1] = "##E8700C"; table[3][2] = "Votre ticket sera traité dans 2-3 jours ouvrables";
        table[4][0] = "P4"; table[4][1] = "#FFFF00"; table[4][2] = "Votre ticket sera traité dans 2-3 jours ouvrables";
        table[5][0] = "P0-P4"; table[5][1] = "#0C83E8"; table[5][2] = "Votre ticket sera traité au plus vite";
        return table;
    }

    public String[][] getiSalesPriorityTableByPriority(String priority){
        String[][] result = null;
        String[][] table = getAlliSalesPriorityTable();

        for (int i=0; i<table.length; i++){
            if (table[i][0].equals(priority)){
                result = new String[1][3];
                result[0][0] = table[i][0]; result[0][1] = table[i][1]; result[0][2] = table[i][2];
            }
        }
        return result;
    }

    public static ArrayList<ISalesIncidentTable> getiSalesIncidentList(){
        ArrayList<ISalesIncidentTable> list = new ArrayList<>();
        /** ############################## Cas Général ############################## **/
        list.add(new ISalesIncidentTable("Général - Erreur réseau (même avec Internet et accès Internet)","P0"));
        list.add(new ISalesIncidentTable("Général - Erreur envoi mail","P0"));
        list.add(new ISalesIncidentTable("Général - Erreur de synchronization","P0"));
        list.add(new ISalesIncidentTable("Général - Erreur de téléchargement des images","P2"));
        list.add(new ISalesIncidentTable("Général - Ralentissement de l'application","P2"));
        list.add(new ISalesIncidentTable("Général - Mode hors ligne ne fonction pas","P2"));
        list.add(new ISalesIncidentTable("Général - Autre","P0-P4"));

        /** ############################## Dans l'onglet Client ############################## **/
        list.add(new ISalesIncidentTable("Client - Erreur de création d'un client","P1"));
        list.add(new ISalesIncidentTable("Client - Erreur de récupération des clients","P1"));
        list.add(new ISalesIncidentTable("Client - Erreur de modification d'un client","P1"));
        list.add(new ISalesIncidentTable("Client - Erreur mise à jour d'un client","P1"));
        list.add(new ISalesIncidentTable("Client - Erreur suppression un client","P3"));
        list.add(new ISalesIncidentTable("Client - Erreur afficher le profile client","P2"));

        /** ############################## Dans l'onglet Panier ############################## **/
        list.add(new ISalesIncidentTable("Panier - Erreur du vidage","P0"));
        list.add(new ISalesIncidentTable("Panier - Erreur de récupération du panier","P0"));
        list.add(new ISalesIncidentTable("Panier - Erreur de modifier le nombre d'article","P1"));
        list.add(new ISalesIncidentTable("Panier - Erreur mise à jour du prix HT","P1"));
        list.add(new ISalesIncidentTable("Panier - Erreur mise à jour du prix TTC","P1"));

        /** ############################## Dans l'onglet Categorie ############################## **/
        list.add(new ISalesIncidentTable("Categorie - Erreur lors de la récupération des catégories","P1"));
        list.add(new ISalesIncidentTable("Categorie - Erreur de filtrage des catégories","P1"));
        list.add(new ISalesIncidentTable("Categorie - Erreur récupération des produits","P1"));
        list.add(new ISalesIncidentTable("Categorie - Erreur récupération des images produits","P1"));
        list.add(new ISalesIncidentTable("Categorie - Erreur de filtrage des produits","P1"));
        list.add(new ISalesIncidentTable("Categorie - Erreur d'affichage du produit sélectionné","P1"));
        list.add(new ISalesIncidentTable("Categorie - Erreur de récupération des produits visuel","P1"));

        /** ############################## Dans l'onglet Commande ############################## **/
        list.add(new ISalesIncidentTable("Commande - Erreur lors de l'envoi d'une commande","P4"));
        list.add(new ISalesIncidentTable("Commande - Erreur de filtrage des commandes","P2"));
        list.add(new ISalesIncidentTable("Commande - Erreur de relance d'un commande","P2"));
        list.add(new ISalesIncidentTable("Commande - Erreur d'envoi des mails","P0"));

        /** ############################## Dans l'onglet Profile ############################## **/
        list.add(new ISalesIncidentTable("Profile - Erreur d'affichage du profile","P3"));
        list.add(new ISalesIncidentTable("Profile - Erreur lors de la mise à jour de l'adresse email","P4"));

        /** ############################## Dans l'onglet Agenda ############################## **/
        list.add(new ISalesIncidentTable("Agenda - Erreur création d'un événement","P2"));
        list.add(new ISalesIncidentTable("Agenda - Erreur de récupération des événements","P2"));
        list.add(new ISalesIncidentTable("Agenda - Erreur de modification d'un événement","P2"));
        list.add(new ISalesIncidentTable("Agenda - Erreur lors de la mise à jour d'un événement","P2"));
        list.add(new ISalesIncidentTable("Agenda - Erreur suppression un d'événement","P2"));

        /** ############################## Dans l'onglet Support Ticket ############################## **/
        list.add(new ISalesIncidentTable("Support Ticket - Erreur création d'un ticket automatique","P2"));
        list.add(new ISalesIncidentTable("Support Ticket - Erreur création d'un ticket manuel","P2"));
        list.add(new ISalesIncidentTable("Support Ticket - Erreur d'envoi des mails","P0"));
        return list;
    }
}