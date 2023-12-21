package com.accenture.dansmarue.services;

import com.accenture.dansmarue.services.models.AuthenticationResponse;
import com.accenture.dansmarue.services.models.UserInfoResponse;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by PK on 16/05/2017.
 */
public interface AuthentParisAccountService {


    /**
     * Get User authenticated Info
     *
     * @param headers Authorization: Bearer <accessToken>
     * @return UserInfoResponse
     */
    @GET("userinfo")
    Single<UserInfoResponse> userInfo(@HeaderMap Map<String, String> headers);

    /**
     * Logout.
     * @param idToken
     *        token to revoke
     * @return
     */
    @GET("logout")
    Call<Void> logout(@Query("id_token_hint") String idToken);

}
