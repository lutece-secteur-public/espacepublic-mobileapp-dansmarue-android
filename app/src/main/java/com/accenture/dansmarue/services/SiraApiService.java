package com.accenture.dansmarue.services;

import com.accenture.dansmarue.services.models.CategoryRequest;
import com.accenture.dansmarue.services.models.CategoryResponse;
import com.accenture.dansmarue.services.models.CongratulateAnomalieRequest;
import com.accenture.dansmarue.services.models.FollowRequest;
import com.accenture.dansmarue.services.models.GetIncidentByIdRequest;
import com.accenture.dansmarue.services.models.GetIncidentByIdResponse;
import com.accenture.dansmarue.services.models.GetIncidentsByPositionRequest;
import com.accenture.dansmarue.services.models.GetIncidentsByPositionResponse;
import com.accenture.dansmarue.services.models.GetIncidentsByUserRequest;
import com.accenture.dansmarue.services.models.GetIncidentsByUserResponse;
import com.accenture.dansmarue.services.models.IdentityRequest;
import com.accenture.dansmarue.services.models.IdentityResponse;
import com.accenture.dansmarue.services.models.IncidentResolvedRequest;
import com.accenture.dansmarue.services.models.ProcessWorkflowRequest;
import com.accenture.dansmarue.services.models.SaveIncidentRequest;
import com.accenture.dansmarue.services.models.SaveIncidentResponse;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.services.models.UnfollowRequest;

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
 * Created by PK on 27/03/2017.
 * Retrofit interface for the SiraServices
 */
public interface SiraApiService {

    /**
     * Retrieve all the categories
     *
     * @param request request
     * @return All the categories
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<CategoryResponse> getCategories(@Field("jsonStream") CategoryRequest request);

    /**
     * Create a incident
     *
     * @param request request
     * @return Response containing the incident ID
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<SaveIncidentResponse> saveIncident(@Field("jsonStream") SaveIncidentRequest request);

    /**
     * Search for all the incident near a position (lont, lat)
     *
     * @param request request
     * @return Response the list of incidents near the given position
     */
    @FormUrlEncoded
    @POST("signalement/api")
    Single<GetIncidentsByPositionResponse> getIncidentsByPosition(@Field("jsonStream") GetIncidentsByPositionRequest request);

//    @POST("sira/signalement/api")


    /**
     * Follow an incident
     *
     * @param request request containing the incident id to follow
     * @return Response containing the status code
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<SiraSimpleResponse> follow(@Field("jsonStream") FollowRequest request);

    /**
     * Unfollow an incident
     *
     * @param request request containing the incident id to unfollow
     * @return Response containing the status code
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<SiraSimpleResponse> unfollow(@Field("jsonStream") UnfollowRequest request);

    /**
     * Retrive the details of an incident
     *
     * @param request request containing the incident id to get detail from
     * @return response containing the details
     */
    @FormUrlEncoded
    @POST("signalement/api")
    Single<GetIncidentByIdResponse> getIncidentById(@Field("jsonStream") GetIncidentByIdRequest request);

//    @POST("sira/signalement/api")


    /**
     * Attach a picture to an incident
     *
     * @param headers header map
     * @param picture picture file
     * @return status 0 is upload ok
     */
    @POST("signalement/api/photo")
    Single<ResponseBody> uploadPicture(@HeaderMap Map<String, String> headers, @Body RequestBody picture);

    /**
     * Launch the workflow post creation.<br>
     * This has to be call after all the pictures are uploaded so the incident is valid when the process is launch in the BO (email sending etc)
     *
     * @param request request containing the id of the newly created incident
     * @return Response containing the status code
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<SiraSimpleResponse> processWorkflow(@Field("jsonStream") ProcessWorkflowRequest request);


    /**
     * Retreive the list of incidents created and/or followed by a user
     *
     * @param request request containig the user id
     * @return list of incidents
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<GetIncidentsByUserResponse> getIncidentsByUser(@Field("jsonStream") GetIncidentsByUserRequest request);


    /**
     * Congratulate an anomaly
     *
     * @param request request
     * @return Response containing the status code
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<SiraSimpleResponse> congratulateAnomalie(@Field("jsonStream") CongratulateAnomalieRequest request);

    /**
     * Resolve an incident
     *
     * @param request request containig hte incident id to resolve
     * @return Response containing the status code
     */
    @FormUrlEncoded
    @POST("signalement/api/")
    Single<SiraSimpleResponse> incidentResolved(@Field("jsonStream") IncidentResolvedRequest request);

    /**
     * Retrieve user informations base on his guid (id compte parisien)
     *
     * @param request request containing a valid user guid
     * @return response containing firstname lastname and user email
     */
    @FormUrlEncoded
    @POST("sira/identitystore")
    Single<IdentityResponse> getUserInformations(@Field("jsonStream") IdentityRequest request);


}
