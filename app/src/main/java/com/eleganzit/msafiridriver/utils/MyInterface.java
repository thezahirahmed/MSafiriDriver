package com.eleganzit.msafiridriver.utils;

import android.telecom.Call;


import java.util.ArrayList;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;



public interface MyInterface
{


    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/addDriver")
    public void registerDriver(@retrofit.http.Field("type") String type,
                               @retrofit.http.Field("fullname") String fullname,
                               @retrofit.http.Field("email") String email,
                               @retrofit.http.Field("password") String password,
                               @retrofit.http.Field("authentication_code") String authentication_code,
                               @retrofit.http.Field("device_id") String device_id,
                               @retrofit.http.Field("device_token") String device_token,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/driverLogin")
    public void loginDriver(@retrofit.http.Field("email") String email,
                            @retrofit.http.Field("password") String password,
                            @retrofit.http.Field("device_token") String device_token,
                            Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/driverSentcode")
    public void driverSentcode(@retrofit.http.Field("email") String email ,
                               @retrofit.http.Field("sentcode") String sentcode,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/driverResetpassword")
    public void driverResetpassword(@retrofit.http.Field("driver_id") String driver_id,
                               @retrofit.http.Field("password") String password,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getDriverdata")
    public void getDriverdata(@retrofit.http.Field("driver_id") String driver_id,
                              Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/driverProfile")
    public void getPersonalInfo(@retrofit.http.Field("driver_id") String driver_id,
                              Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/driverApprovel")
    public void getApprovalStatus(@retrofit.http.Field("email") String email,
                                Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/allApprovelStatus")
    public void getEmptyFields(@retrofit.http.Field("driver_id") String driver_id,
                                  Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getBankdetails")
    public void getBankdetails(@retrofit.http.Field("driver_id") String driver_id,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDriverprofile")
    public void updateDriverdata(@retrofit.http.Field("driver_id") String driver_id,
                                 @retrofit.http.Field("gender") String gender,
                                 @retrofit.http.Field("dob") String dob,
                                 @retrofit.http.Field("street") String street,
                                 @retrofit.http.Field("city") String city,
                                 @retrofit.http.Field("state") String state,
                                 @retrofit.http.Field("postal_code") String postal_code,
                                 @retrofit.http.Field("country") String country,
                                 @retrofit.http.Field("mobile_number") String mobile_number,
                                 @retrofit.http.Field("fullname") String fullname,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDriverprofile")
    public void updateApprovalStatus(@retrofit.http.Field("driver_id") String driver_id,
                                 @retrofit.http.Field("approvel") String approvel,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/addBankdetails")
    public void addBankdetails(@retrofit.http.Field("driver_id") String driver_id,
                               @retrofit.http.Field("bank_name") String bank_name,
                               @retrofit.http.Field("bank_payee") String bank_payee,
                               @retrofit.http.Field("bank_account") String bank_account,
                               @retrofit.http.Field("bank_ifsc") String bank_ifsc,
                               @retrofit.http.Field("street1") String street1,
                               @retrofit.http.Field("street2") String street2,
                               @retrofit.http.Field("city") String city,
                               @retrofit.http.Field("state") String state,
                               @retrofit.http.Field("postal_code") String postal_code,
                               @retrofit.http.Field("country") String country,
                               @retrofit.http.Field("birthday") String birthday,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateBankdetails")
    public void updateBankdetails(@retrofit.http.Field("driver_id") String driver_id,
                                  @retrofit.http.Field("bank_name") String bank_name,
                                  @retrofit.http.Field("bank_payee") String bank_payee,
                                  @retrofit.http.Field("bank_account") String bank_account,
                                  @retrofit.http.Field("bank_ifsc") String bank_ifsc,
                                  @retrofit.http.Field("street1") String street1,
                                  @retrofit.http.Field("street2") String street2,
                                  @retrofit.http.Field("city") String city,
                                  @retrofit.http.Field("state") String state,
                                  @retrofit.http.Field("postal_code") String postal_code,
                                  @retrofit.http.Field("country") String country,
                                  @retrofit.http.Field("birthday") String birthday,
                                  Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDriverprofile")
    public void updateDriverstatus(@retrofit.http.Field("driver_id") String driver_id,
                                   @retrofit.http.Field("status") String status,
                                   Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/driverChangepassword")
    public void changePassword(@retrofit.http.Field("driver_id") String driver_id,
                               @retrofit.http.Field("old_password") String old_password,
                               @retrofit.http.Field("password") String password,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/setDrivertrip")
    public void setDrivertrip(@retrofit.http.Field("driver_id") String driver_id,
                              @retrofit.http.Field("from_title") String from_title,
                              @retrofit.http.Field("from_lat") String from_lat,
                              @retrofit.http.Field("from_lng") String from_lng,
                              @retrofit.http.Field("from_address") String from_address,
                              @retrofit.http.Field("to_title") String to_title,
                              @retrofit.http.Field("to_lat") String to_lat,
                              @retrofit.http.Field("to_lng") String to_lng,
                              @retrofit.http.Field("to_address") String to_address,
                              @retrofit.http.Field("datetime") String datetime,
                              @retrofit.http.Field("end_datetime") String end_datetime,
                              Callback<Response> callback
    );



    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDrivertrip")
    public void updateDrivertrip(@retrofit.http.Field("id") String id,
                                 @retrofit.http.Field("from_title") String from_title,
                                 @retrofit.http.Field("from_lat") String from_lat,
                                 @retrofit.http.Field("from_lng") String from_lng,
                                 @retrofit.http.Field("from_address") String from_address,
                                 @retrofit.http.Field("to_title") String to_title,
                                 @retrofit.http.Field("to_lat") String to_lat,
                                 @retrofit.http.Field("to_lng") String to_lng,
                                 @retrofit.http.Field("to_address") String to_address,
                                 @retrofit.http.Field("datetime") String datetime,
                                 @retrofit.http.Field("end_datetime") String end_datetime,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDrivertrip")
    public void updateTripStatus(@retrofit.http.Field("id") String id,
                                 @retrofit.http.Field("status") String status,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDrivertrip")
    public void delayTrip(@retrofit.http.Field("id") String id,
                                 @retrofit.http.Field("delaytime") String delaytime,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/updateDrivertrip")
    public void updateTripStatus(@retrofit.http.Field("id") String id,
                                 @retrofit.http.Field("status") String status,
                                 @retrofit.http.Field("trip_map_screenshot") String trip_map_screenshot,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/onboardUserlist")
    public void onboardUserlist(@retrofit.http.Field("trip_id") String trip_id,
                                @retrofit.http.Field("user_id") String user_id,
                                Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/myDrivertrip")
    public void getDriverTrips(@retrofit.http.Field("driver_id") String driver_id,
                               @retrofit.http.Field("trip_type") String trip_type,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/tripUserlist")
    public void getPassengers(@retrofit.http.Field("trip_id") String trip_id,
                              Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getOnboardlist")
    public void getOnBoardPassengers(@retrofit.http.Field("trip_id") String trip_id,
                              Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/tripDetails")
    public void getTrip(@retrofit.http.Field("trip_id") String trip_id,
                        Callback<Response> callback
    );


    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getVehicledata")
    public void getVehicledata(@retrofit.http.Field("driver_id") String driver_id,
                               Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getDriverdocuments")
    public void getDriverdocuments(@retrofit.http.Field("driver_id") String driver_id,
                                   Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/removeDrivertrip")
    public void removeDrivertrip(@retrofit.http.Field("id") String id,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/deleteVehicleplate")
    public void removePhoto(@retrofit.http.Field("id") String id,
                            Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/deleteDocuments")
    public void deleteDocuments(@retrofit.http.Field("id") String id,
                                Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/rattingDetails")
    public void getratingDetails(@retrofit.http.Field("driver_id") String driver_id,
                                 Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getReview")
    public void getReview(@retrofit.http.Field("trip_id") String trip_id,
                          @retrofit.http.Field("user_id") String user_id,
                          Callback<Response> callback
    );

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getCountry")
    public void getCountry(@retrofit.http.Field("") String dummy,
                           Callback<Response> callback);

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/Triplocations")
    public void Triplocations(@retrofit.http.Field("") String dummy,
                           Callback<Response> callback);

    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/getState")
    public void getState(@retrofit.http.Field("country_id") String country_id,
                         Callback<Response> callback);


    @retrofit.http.FormUrlEncoded
    @retrofit.http.POST("/lastLocation")
    public void lastLocation(@retrofit.http.Field("trip_id") String trip_id,
                             @retrofit.http.Field("last_lat") String last_lat,
                             @retrofit.http.Field("last_lng") String last_lng,
                             Callback<Response> callback
    );
}
