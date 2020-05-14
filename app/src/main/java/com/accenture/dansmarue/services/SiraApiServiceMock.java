package com.accenture.dansmarue.services;

import com.accenture.dansmarue.services.models.CategoryResponse;
import com.accenture.dansmarue.services.models.equipements.EquipementResponse;
import com.accenture.dansmarue.services.models.equipements.GetIncidentsEquipementsByPositionResponse;

import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Created by d4v1d on 18/10/2017.
 */

public interface SiraApiServiceMock {

    @GET("getcatequipementenveloppe/")
    Single<CategoryResponse> getCategoriesEquipement();

    @GET("getequipementsenveloppe/")
    Single<EquipementResponse> getEquipements();

    @GET("getincidentsequipementbyposition/")
    Single<GetIncidentsEquipementsByPositionResponse> getIncidentsEquipementByPosition();


}
