package com.accenture.dansmarue.ui.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.accenture.dansmarue.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Arrays

class EditAddressActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun newIntent(context: Context) = Intent(context, EditAddressActivity::class.java)
    }

    private val parisBounds = LatLngBounds(
        LatLng(48.811310, 2.217569),
        LatLng(48.905509, 2.469839)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_address)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        initAutoCompleteSearchBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initAutoCompleteSearchBar() {
        if (!Places.isInitialized()) {
            try {
                val metaData: Bundle = packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                ).metaData
                metaData.getString("com.google.android.geo.API_KEY")?.let {
                    Places.initialize(
                        this,
                        it
                    )
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        // Create a new Places client instance.
        val placesClient = Places.createClient(this)

        //        Search and place pin
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.addressAutoCompletePlacesFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME))
        //        Be more specific : limit result to a rectangle Meudon > Bobigny
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(parisBounds))
        (autocompleteFragment.requireView()
            .findViewById<View>(R.id.places_autocomplete_search_input) as EditText).setHint(
            R.string.google_searchbar_wording_desc
        )
        (autocompleteFragment.requireView()
            .findViewById<View>(R.id.places_autocomplete_search_input) as EditText).contentDescription =
            getString(R.string.google_searchbar_wording_desc)
        autocompleteFragment.requireView()
            .findViewById<View>(R.id.places_autocomplete_search_button).contentDescription =
            getString(R.string.google_searchbar_wording_desc)
        autocompleteFragment.requireView()
            .findViewById<View>(R.id.places_autocomplete_search_button).importantForAccessibility =
            View.IMPORTANT_FOR_ACCESSIBILITY_NO


//        Limit to France
        autocompleteFragment.setCountry("Fr")
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS)

//        Search Results
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val intent = Intent()
                val bundle = Bundle()
                bundle.putParcelable("place", place)
                intent.putExtra("data",bundle)
                setResult(RESULT_OK,intent)
                finish()
            }

            override fun onError(status: Status) {
                //Log.i(MapParisFragment.TAG, "onError: $status")
            }
        })
    }
}