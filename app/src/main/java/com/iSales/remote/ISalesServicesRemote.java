package com.iSales.remote;

import com.iSales.remote.model.AgendaEventSuccess;
import com.iSales.remote.model.AgendaEvents;
import com.iSales.remote.model.Categorie;
import com.iSales.remote.model.Document;
import com.iSales.remote.model.DolPhoto;
import com.iSales.remote.model.Internaute;
import com.iSales.remote.model.InternauteSuccess;
import com.iSales.remote.model.Order;
import com.iSales.remote.model.OrderLine;
import com.iSales.remote.model.PaymentTypes;
import com.iSales.remote.model.Product;
import com.iSales.remote.model.ProductCustomerPrice;
import com.iSales.remote.model.ProductVirtual;
import com.iSales.remote.model.Thirdpartie;
import com.iSales.remote.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by netserve on 03/08/2018.
 */

public interface ISalesServicesRemote {

    //    COnnexion d'un internaute
    @POST("login")
    Call<InternauteSuccess> login(@Body Internaute internaute);

    //  Recupération de la liste des produits
    @GET("products")
    Call<ArrayList<Product>> findProductsByCategorie(@Query(ApiUtils.sqlfilters) String sqlfilters,
                                                     @Query(ApiUtils.sortfield) String sortfield,
                                                     @Query(ApiUtils.sortorder) String sortorder,
                                                     @Query(ApiUtils.limit) long limit,
                                                     @Query(ApiUtils.category) long category,
                                                     @Query(ApiUtils.mode) int mode);

    //  Recupération de la liste des produits
    @GET("products")
    Call<ArrayList<Product>> findProducts(@Query(ApiUtils.sqlfilters) String sqlfilters,
                                          @Query(ApiUtils.sortfield) String sortfield,
                                          @Query(ApiUtils.sortorder) String sortorder,
                                          @Query(ApiUtils.limit) long limit,
                                          @Query(ApiUtils.page) long page,
                                          @Query(ApiUtils.mode) int mode);

    //  Recupération de la liste des thirdpartie(client, prospect, autre)
    @GET("thirdparties")
    Call<ArrayList<Thirdpartie>> findThirdpartie(@Query(ApiUtils.limit) long limit,
                                                 @Query(ApiUtils.page) long page,
                                                 @Query(ApiUtils.mode) int mode);

    //  Recupération d'un thirdpartie a partir de son id
    @GET("thirdparties/{thirdpartieId}")
    Call<Thirdpartie> findThirdpartieById(@Path("thirdpartieId") long thirdpartieId);

    //  Recupération de la liste des categories
    @GET("categories")
    Call<ArrayList<Categorie>> findCategories(@Query(ApiUtils.sortfield) String sortfield,
                                              @Query(ApiUtils.sortorder) String sortorder,
                                              @Query(ApiUtils.limit) long limit,
                                              @Query(ApiUtils.page) long page,
                                              @Query(ApiUtils.type) String type);

    //  Recupération du poster d'un produit
    @GET("documents/download")
    Call<DolPhoto> findProductsPoster(@Query(ApiUtils.module_part) String module_part,
                                      @Query(ApiUtils.original_file) String original_file);

    //  Recupération d'un document
    @GET("documents/download")
    Call<DolPhoto> findDocument(@Query(ApiUtils.module_part) String module_part,
                                @Query(ApiUtils.original_file) String original_file);

    //  Enregistrement d'une categorie
    @POST("categories")
    Call<Long> saveCategorie(@Body Categorie categorie);

    //  Enregistrement d'un thirdparty
    @POST("thirdparties")
    Call<Long> saveThirdpartie(@Body Thirdpartie thirdpartie);

    //  Modification d'un thirdparty
    @PUT("thirdparties/{thirdpartiesId}")
    Call<Thirdpartie> updateThirdpartie(@Path("thirdpartiesId") Long thirdpartiesId,
                                        @Body Thirdpartie thirdpartie);

    //  suppression d'un thirdparty
    @DELETE("thirdparties/{thirdpartieId}")
    Call<Long> deleteThirdpartie(@Path("thirdpartieId") Long thirdpartieId);

    //  Enregistrement d'une commande client
    @POST("orders")
    Call<Long> saveCustomerOrder(@Body Order order);

    //  Recupération de la liste des commandes
    @GET("orders")
    Call<ArrayList<Order>> findOrders(@Query(ApiUtils.sqlfilters) String sqlfilters,
                                      @Query(ApiUtils.sortfield) String sortfield,
                                      @Query(ApiUtils.sortorder) String sortorder,
                                      @Query(ApiUtils.limit) long limit,
                                      @Query(ApiUtils.page) long page);

    //  Recupération des moyens de paiement
    @GET("setup/dictionary/payment_types")
    Call<ArrayList<PaymentTypes>> findPaymentTypes(@Query(ApiUtils.active) Integer active);

    //  Recupération de la liste des ligne d'une commandes
    @GET("orders/{orderId}/lines")
    Call<ArrayList<OrderLine>> findOrderLines(@Path("orderId") Long orderId);

    //  Valide un bon de commande
    @POST("orders/{orderId}/validate")
    Call<Order> validateCustomerOrder(@Path("orderId") Long orderId);

    //  Valide un bon de commande
    @POST("documents/upload")
    Call<String> uploadDocument(@Body Document document);

    //  Recupération d'un user a partir de son login
    @GET("users")
    Call<ArrayList<User>> findUserByLogin(@Query(ApiUtils.sqlfilters) String sqlfilters);



    // =======================  Agenda Events  ==============================
    @POST("agendaevents")
    Call<Long> createEvent(@Body AgendaEvents agendaEvents);

    @GET("agendaevents")
    Call<ArrayList<AgendaEvents>> getAllEvents(@Query(ApiUtils.sqlfilters) String sqlfilters,
                                               @Query(ApiUtils.sortfield) String sortfield,
                                               @Query(ApiUtils.sortorder) String sortorder,
                                               @Query(ApiUtils.limit) long limit,
                                               @Query(ApiUtils.page) long page);

    @GET("agendaevents/{eventId}")
    Call<AgendaEvents> getEventById(@Path("eventId") Long eventId);

    @PUT("agendaevents/{eventId}")
    Call<AgendaEvents> updateEvent(@Path("eventId") Long eventId,
                                   @Body AgendaEvents agendaEvents);

    @DELETE("agendaevents/{eventId}")
    Call<AgendaEventSuccess> deleteEventById(@Path("eventId") Long eventId);

    // ======== RYImg endpoinds  ==========

    //  Recupération des produit virtuels d'un produit
    @GET("product_virtual_3.php")
    Call<ArrayList<ProductVirtual>> ryFindProductVirtual(@Query(ApiUtils.id) Long productId);

    //  Recupération des produits affecté a un client
    @GET("product_customer_price.php")
    Call<List<ProductCustomerPrice>> ryFindProductPrice(@Query(ApiUtils.soc_id) Long productId);

}
