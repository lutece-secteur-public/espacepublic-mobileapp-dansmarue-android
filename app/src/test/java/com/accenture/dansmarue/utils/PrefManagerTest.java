package com.accenture.dansmarue.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by PK on 21/03/2017.
 * Unit test implementation of {@link PrefManager}
 */
public class PrefManagerTest extends TestCase {

    private static final String PREF_NAME = "dansmarue";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String CATEGORIES_VERSION = "categoriesVersion";
    private static final String DEFAULT_CATEGORIES_VERSION = "0";


    @Mock
    private Context mockContext;
    @Mock
    private  SharedPreferences mockPrivateSharedPreferences;
    @Mock
    private  SharedPreferences.Editor mockPrivateSharedPreferencesEditor;

    private PrefManager prefManager;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockContext.getSharedPreferences(PREF_NAME,0)).thenReturn(mockPrivateSharedPreferences);
        Mockito.when(mockPrivateSharedPreferences.edit()).thenReturn(mockPrivateSharedPreferencesEditor);
        prefManager = new PrefManager(mockContext);
    }


    /**
     * Test setFirstTimeLaunch with TRUE flag.
     * Check that prefManager passes the right flag to the sharedPreferenceEditor (private) and apply (vs commit) the modification
     */
    public void testSetFirstTimeLaunchIsTrue(){
        prefManager.setFirstTimeLaunch(Boolean.TRUE);
        Mockito.verify(mockPrivateSharedPreferencesEditor).putBoolean(IS_FIRST_TIME_LAUNCH,Boolean.TRUE);
        Mockito.verify(mockPrivateSharedPreferencesEditor).apply();
        Mockito.verify(mockPrivateSharedPreferencesEditor,Mockito.never()).commit();
    }


    /**
     * Test setFirstTimeLaunch with FALSE flag.
     * Check that prefManager passes the right flag to the sharedPreferenceEditor (private) and apply (vs commit) the modification
     */
    public void testSetFirstTimeLaunchIsFalse(){
        //when
        prefManager.setFirstTimeLaunch(Boolean.FALSE);

        //then
        Mockito.verify(mockPrivateSharedPreferencesEditor).putBoolean(IS_FIRST_TIME_LAUNCH,Boolean.FALSE);
        Mockito.verify(mockPrivateSharedPreferencesEditor).apply();
        Mockito.verify(mockPrivateSharedPreferencesEditor,Mockito.never()).commit();
    }

    /**
     * Test isFirstTimeLaunch with a TRUE return value.
     * Check that the prefManager returns the value stored in the privateSharedPreference
     */
    public void testIsFirstTimeLaunchTrue(){
        //given
        Mockito.when(mockPrivateSharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH,Boolean.TRUE)).thenReturn(Boolean.TRUE);

        //when
        final boolean actual = prefManager.isFirstTimeLaunch();

        //then
        Assert.assertSame(Boolean.TRUE,actual);
    }

    /**
     * Test isFirstTimeLaunch with a FALSE return value.
     * Check that the prefManager returns the value stored in the privateSharedPreference
     */
    public void testIsFirstTimeLaunchFalse(){
        //given
        Mockito.when(mockPrivateSharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH,Boolean.TRUE)).thenReturn(Boolean.FALSE);

        //when
        final boolean actual = prefManager.isFirstTimeLaunch();

        //then
        Assert.assertSame(Boolean.FALSE,actual);
    }

    public void testSetCategoriesVersion(){
        //given
        final String version = "2.33";
        //when
        prefManager.setCategoriesVersion(version);

        //then
        Mockito.verify(mockPrivateSharedPreferencesEditor).putString(CATEGORIES_VERSION,version);
        Mockito.verify(mockPrivateSharedPreferencesEditor).apply();
        Mockito.verify(mockPrivateSharedPreferencesEditor,Mockito.never()).commit();

    }


    public void testGetCategoriesVersionDefault(){
        //given
        Mockito.when(mockPrivateSharedPreferences.getString(CATEGORIES_VERSION,DEFAULT_CATEGORIES_VERSION)).thenReturn(DEFAULT_CATEGORIES_VERSION);

        //when
        final String actual = prefManager.getCategoriesVersion();

        //then
        Assert.assertEquals(DEFAULT_CATEGORIES_VERSION,actual);
    }

    public void testGetCategoriesVersion(){
        //given
        final String version="5";
        Mockito.when(mockPrivateSharedPreferences.getString(CATEGORIES_VERSION,DEFAULT_CATEGORIES_VERSION)).thenReturn(version);

        //when
        final String actual = prefManager.getCategoriesVersion();

        //then
        Assert.assertEquals(version,actual);
    }





}
