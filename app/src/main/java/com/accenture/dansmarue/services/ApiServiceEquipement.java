package com.accenture.dansmarue.services;

import com.accenture.dansmarue.services.models.CategoryRequest;
import com.accenture.dansmarue.services.models.CategoryResponse;
import com.accenture.dansmarue.services.models.CongratulateAnomalieRequest;
import com.accenture.dansmarue.services.models.FollowRequest;
import com.accenture.dansmarue.services.models.GetIncidentsByUserRequest;
import com.accenture.dansmarue.services.models.GetIncidentsByUserResponse;
import com.accenture.dansmarue.services.models.IncidentResolvedRequest;
import com.accenture.dansmarue.services.models.ProcessWorkflowRequest;
import com.accenture.dansmarue.services.models.SaveIncidentRequest;
import com.accenture.dansmarue.services.models.SaveIncidentResponse;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.services.models.UnfollowRequest;
import com.accenture.dansmarue.services.models.equipements.CategoryEquipementResponse;
import com.accenture.dansmarue.services.models.equipements.EquipementRequest;
import com.accenture.dansmarue.services.models.equipements.EquipementResponse;
import com.accenture.dansmarue.services.models.equipements.GetEquipementsByPositionRequest;
import com.accenture.dansmarue.services.models.equipements.GetEquipementsByPositionResponse;
import com.accenture.dansmarue.services.models.equipements.GetIncidentEquipementByIdRequest;
import com.accenture.dansmarue.services.models.equipements.GetIncidentEquipementByIdResponse;
import com.accenture.dansmarue.services.models.equipements.GetIncidentsByEquipementRequest;
import com.accenture.dansmarue.services.models.equipements.GetIncidentsByEquipementResponse;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by d4v1d on 18/10/2017.
 */

public interface ApiServiceEquipement {

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<CategoryEquipementResponse> getCategoriesEquipement(@Field("jsonStream") CategoryRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<EquipementResponse> getEquipements(@Field("jsonStream") EquipementRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<GetIncidentsByEquipementResponse> getIncidentsByEquipement(@Field("jsonStream") GetIncidentsByEquipementRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<GetEquipementsByPositionResponse> getEquipementsByPosition(@Field("jsonStream") GetEquipementsByPositionRequest request);

    // Create incident

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<SaveIncidentResponse> saveIncident(@Field("jsonStream") SaveIncidentRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<SiraSimpleResponse> processWorkflow(@Field("jsonStream") ProcessWorkflowRequest request);

    @POST("equipement/api/photo")
    Single<ResponseBody> uploadPicture(@HeaderMap Map<String, String> headers, @Body RequestBody picture);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<GetIncidentsByUserResponse> getIncidentsByUser(@Field("jsonStream") GetIncidentsByUserRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<GetIncidentEquipementByIdResponse> getIncidentEquipementById(@Field("jsonStream") GetIncidentEquipementByIdRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<SiraSimpleResponse> congratulateAnomalie(@Field("jsonStream") CongratulateAnomalieRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<SiraSimpleResponse> follow(@Field("jsonStream") FollowRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<SiraSimpleResponse> unfollow(@Field("jsonStream") UnfollowRequest request);

    @FormUrlEncoded
    @POST("equipement/api/")
    Single<SiraSimpleResponse> incidentResolved(@Field("jsonStream") IncidentResolvedRequest request);

}
