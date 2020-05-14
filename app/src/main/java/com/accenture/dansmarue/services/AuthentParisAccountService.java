package com.accenture.dansmarue.services;

import com.accenture.dansmarue.services.models.AuthenticationResponse;
import com.accenture.dansmarue.services.models.UserValidResponse;

import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by PK on 16/05/2017.
 */
public interface AuthentParisAccountService {

    /**
     * Authentication to VDP account using login/pwd
     *
     * @param headers X-OpenAM-Username:<login>
     *                X-OpenAM-Password:<password>
     * @return authentication token (tokenId)
     */
    @POST("authenticate")
    Single<AuthenticationResponse> authenticate(@HeaderMap Map<String, String> headers);

    /**
     * Check if an account is valid using the login and a token (@see AuthentParisAccountService{@link #authenticate(Map)})
     *
     * @param headers mcpAuth:<token_id>
     * @param login   user login
     * @return the guid (VDP user identifier) if the account is valid <br>
     * error code 401 if invalid
     */
    @GET("users/{login}")
    Single<UserValidResponse> userValid(@HeaderMap Map<String, String> headers, @Path("login") String login);
}
