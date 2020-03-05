package com.iSales.interfaces;

import com.iSales.remote.rest.FindProductsREST;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by netserve on 29/08/2018.
 */

public interface FindProductsListener {
    void onFindProductsCompleted(FindProductsREST findProductsREST) throws IOException, JSONException;
    void onFindAllProductsCompleted();
}
