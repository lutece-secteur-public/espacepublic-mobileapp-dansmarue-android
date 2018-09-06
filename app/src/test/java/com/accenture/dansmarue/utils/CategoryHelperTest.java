package com.accenture.dansmarue.utils;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.services.models.CategoryResponse;
import com.google.gson.GsonBuilder;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PK on 30/03/2017.
 * Unit Test for {@link CategoryHelper}
 */
public class CategoryHelperTest extends TestCase {

    //JSON data test Strings
    private static final String JSON_ANSWER_ONE_LVL ="{\"request\":\"getCategories\",\"answer\":{\"status\":0,\"version\":2.8899982,\"categories\":{\"0\":{\"children_id\":[1000,2000]},\"1000\":{\"parent_id\":0,\"name\":\"Abris bus\"},\"2000\":{\"parent_id\":0,\"name\":\"Affiche, autocollant\",\"children_id\":[]},\"3000\":{\"parent_id\":0,\"name\":\"Affiche, autocollant\",\"children_id\":[]}}}}";
    private static final String JSON_ANSWER_TWO_LVLS ="{\"request\":\"getCategories\",\"answer\":{\"status\":0,\"version\":2.8899982,\"categories\":{\"0\":{\"children_id\":[1000,2000]},\"1000\":{\"parent_id\":0,\"name\":\"Abris bus\"},\"2000\":{\"parent_id\":0,\"name\":\"Affiche, autocollant\",\"children_id\":[3702]},\"3702\":{\"parent_id\":2000,\"name\":\"Affiche, autocollant\",\"children_id\":[]}}}}";
    private static final String JSON_ANSWER_THREE_LVLS ="{\"request\":\"getCategories\",\"answer\":{\"status\":0,\"version\":2.8899982,\"categories\":{\"0\":{\"children_id\":[1000,2000]},\"1000\":{\"parent_id\":0,\"name\":\"Abris bus\"},\"2000\":{\"parent_id\":0,\"name\":\"Affiche, autocollant\",\"children_id\":[3702]},\"3702\":{\"parent_id\":2000,\"name\":\"Affiche, autocollant\",\"children_id\":[4000]},\"4000\":{\"parent_id\":3702,\"name\":\"Affiche, autocollant\",\"children_id\":[]}}}}";

    private CategoryResponse responseOneLvl;
    private CategoryResponse responseTwoLvls;
    private CategoryResponse responseThreeLvls;


    protected void setUp() throws Exception {
        super.setUp();
        //Generate the data
        responseOneLvl = new GsonBuilder().create().fromJson(JSON_ANSWER_ONE_LVL,CategoryResponse.class);
        responseTwoLvls = new GsonBuilder().create().fromJson(JSON_ANSWER_TWO_LVLS,CategoryResponse.class);
        responseThreeLvls = new GsonBuilder().create().fromJson(JSON_ANSWER_THREE_LVLS,CategoryResponse.class);
    }


    /**
     * Unit test of {@link CategoryHelper#answerToCategories(Map)} with a null map
     */
    public void testNullAnswerToCategories(){
        //Given
        final Map<String,Category> expected = null;

        //When
        final Map<String,Category> actual = CategoryHelper.answerToCategories(null);

        //Then
        Assert.assertEquals(actual,expected);
    }

    /**
     * Unit test of {@link CategoryHelper#answerToCategories(Map)} with an empty map
     */
    public void testEmptyAnswerToCategories(){
        //Given
        final Map<String,Category> expected = null;

        //When
        final Map<String,Category> actual = CategoryHelper.answerToCategories(new HashMap<String, CategoryResponse.Answer.Category>());

        //Then
        Assert.assertEquals(actual,expected);
    }

    /**
     * Unit test of {@link CategoryHelper#answerToCategories(Map)} with a simple answer
     */
    public void testAnswerToCategoriesOneLvl(){

        //When
        final Map<String,Category> actual = CategoryHelper.answerToCategories(responseOneLvl.getAnswer().getCategories());

        //Then
        Assert.assertNotNull("le résultat n'set pas null",actual);
        Assert.assertEquals("Il y a 4 catégories dans la Map",4,actual.size());
        Assert.assertNotNull("L'élement d'id 0 a des enfants",actual.get("0").getChildren());
        Assert.assertEquals("L'élement d'id 0 a 2 enfants",2,actual.get("0").getChildren().size());
        Assert.assertSame("L'élement d'id 3000 n'a pas d'enfants",actual.get("3000").getChildren().size(),0);
    }

    /**
     * Unit test of {@link CategoryHelper#answerToCategories(Map)} with a more complex answer (two levels)
     */
    public void testAnswerToCategoriesTwoLvls(){

        //When
        final Map<String,Category> actual = CategoryHelper.answerToCategories(responseTwoLvls.getAnswer().getCategories());

        //Then
        Assert.assertNotNull("le résultat n'set pas null",actual);
        Assert.assertEquals("Il y a 4 catégories dans la Map",4,actual.size());
        Assert.assertNotNull("L'élement d'id 0 a des enfants",actual.get("0").getChildren());
        Assert.assertEquals("L'élement d'id 0 a 2 enfants",2,actual.get("0").getChildren().size());
        Assert.assertNotNull("L'élement d'id 2000 a un enfant",actual.get("2000").getChildren());
        Assert.assertEquals("L'élement d'id 2000 a un enfant",1,actual.get("2000").getChildren().size());
    }


    /**
     * Unit test of {@link CategoryHelper#extractChildren(String, Map)} with a null map
     */
    public void testExtractChildrenNull(){

        //WHen
        final List<Category> actual= CategoryHelper.extractChildren("0",null);

        //Then
        Assert.assertNull("Aucun enfant trouvé",actual);
    }

    /**
     * Unit test of {@link CategoryHelper#extractChildren(String, Map)} with an incorrect parentId
     */
    public void testExtractChildrenIncorrectParentId(){
        //Given
        final Map<String,Category> categories = CategoryHelper.answerToCategories(responseTwoLvls.getAnswer().getCategories());

        //WHen
        final List<Category> actual= CategoryHelper.extractChildren("-1",categories);

        //Then
        Assert.assertNull("Aucun enfant trouvé",actual);
    }

    /**
     * Unit test of {@link CategoryHelper#extractChildren(String, Map)} to extract the first level of children
     */
    public void testExtractChildrenFirstLvl(){
        //Given
        final Map<String,Category> categories = CategoryHelper.answerToCategories(responseTwoLvls.getAnswer().getCategories());

        //WHen
        final List<Category> actual= CategoryHelper.extractChildren("0",categories);

        //Then
        Assert.assertNotNull("L'element d'id 0 a des enfants",actual);
        Assert.assertEquals("L'element d'id 0 a deux enfants",2,actual.size());
    }


    /**
     * Unit test of {@link CategoryHelper#extractChildren(String, Map)} to extract the second level of children
     */
    public void testExtractChildrenSecondLvl(){
        //Given
        final Map<String,Category> categories = CategoryHelper.answerToCategories(responseTwoLvls.getAnswer().getCategories());

        //WHen
        final List<Category> actual= CategoryHelper.extractChildren("2000",categories);

        //Then
        Assert.assertNotNull("L'element d'id 2000 a des enfants",actual);
        Assert.assertEquals("L'element d'id 2000 a trois enfants",1,actual.size());
    }

    public void testGetFirstParentIdFirstParent(){
        //Given
        final Map<String,Category> categories = CategoryHelper.answerToCategories(responseThreeLvls.getAnswer().getCategories());

        //WHen
        final String actual= CategoryHelper.getFirstParent(Category.ID_FIRST_PARENT,categories);

        //Then
        Assert.assertEquals("Pas de plus haut parent pour ID_FIRST_PARENT",Category.ID_FIRST_PARENT,actual);
    }

    public void testGetFirstParentIdOneLevel(){
        //Given
        final Map<String,Category> categories = CategoryHelper.answerToCategories(responseThreeLvls.getAnswer().getCategories());

        //WHen
        final String actual= CategoryHelper.getFirstParent("3702",categories);

        //Then
        Assert.assertEquals("2000",actual);
    }

    public void testGetFirstParentIdTwoLevels(){
        //Given
        final Map<String,Category> categories = CategoryHelper.answerToCategories(responseThreeLvls.getAnswer().getCategories());

        //WHen
        final String actual= CategoryHelper.getFirstParent("4000",categories);

        //Then
        Assert.assertEquals("Pas de plus haut parent pour ID_FIRST_PARENT","2000",actual);
    }


}
