package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.content.Context;

import com.accenture.dansmarue.mvp.models.Category;
import com.accenture.dansmarue.mvp.views.CategoryView;
import com.accenture.dansmarue.utils.CategoryHelper;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PK on 12/04/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CategoryHelper.class)
public class CategoryPresenterTest extends TestCase {

    private static final String ID_PARENT_THREE_CHILDREN = "0";
    private static final String ID_PARENT_TWO_CHILDREN ="1" ;
    private static final String ID_PARENT_ONE_CHILD ="2" ;
    private static final String ID_PARENT_NO_CHILDREN ="3" ;


    private CategoryPresenter categoryPresenter;

    @Mock
    private Application application;
    @Mock
    private Context context;
    @Mock
    private CategoryView view;
    @Mock
    private PrefManager prefManager;

    private Map<String,Category> allCategories=generateCategories();


    @Override
    protected void setUp() throws Exception {
        //Init        json file for loadCategories
        FileInputStream fis = new FileInputStream(new File(getClass().getResource("/" + Constants.FILE_CATEGORIES_JSON).toURI()));

        MockitoAnnotations.initMocks(this);
        Mockito.when(application.getApplicationContext()).thenReturn(context);
        Mockito.when(context.openFileInput(Constants.FILE_CATEGORIES_JSON)).thenReturn(fis);
        Mockito.when(prefManager.getIsAgent()).thenReturn(false);
        categoryPresenter = new CategoryPresenter(application, view, prefManager);

        Whitebox.setInternalState(categoryPresenter, "allCategories",allCategories );
    }

    /**
     * Unit test of categoryPresenter#extractChildren.
     * Check that the view gets updated with children data
     */
    public void testExtractChildren() {
        //Given
        final String parentId = ID_PARENT_THREE_CHILDREN;
        final List<Category> children =allCategories.get(parentId).getChildren();

        //When
        categoryPresenter.extractChildren(parentId);

        //Then
        Mockito.verify(view).updateListView(children,false);
    }

    /**
     * Unit test of categoryPresenter#onCategorySelected when the selected category has children.
     * Check that the view gets updated with the selected ID
     */
    public void testOnParentCategorySelected() {
        //Given
        final String selectedId = ID_PARENT_THREE_CHILDREN;

        //When
        categoryPresenter.onCategorySelected(selectedId);

        //Then
        Mockito.verify(view).onParentSelected(allCategories.get(selectedId));
        Mockito.verify(view,Mockito.never()).onChildSelected((Category) Mockito.any());
    }

    /**
     * Unit test of categoryPresenter#onCategorySelected when the selected category does not have any child.
     * Check that the view gets updated with the selected Category
     */
    public void testOnChildCategorySelected() {
        //Given
        final String selectedId = ID_PARENT_NO_CHILDREN;

        //When
        categoryPresenter.onCategorySelected(selectedId);

        //Then

        Mockito.verify(view,Mockito.never()).onParentSelected(allCategories.get(selectedId));

    }

    /**
     * Unit test of categoryPresenter#loadCategories
     * Check that the categories got reloaded with and the view gets notified
     */
    public void testLoadCategories(){
        //Given
        final Map<String,Category> oldCategories = (Map<String, Category>) Whitebox.getInternalState(categoryPresenter,"allCategories");

        //When
        categoryPresenter.loadCategories();

        //Then
        Mockito.verify(view).categoriesReady();
        final Map<String,Category> newCategories = (Map<String, Category>) Whitebox.getInternalState(categoryPresenter,"allCategories");
        //Check that we had 3 categories before loading and now 8
        Assert.assertEquals("categories were initialized properly",allCategories.size(),oldCategories.size());
        Assert.assertEquals("categories got updated properly",8,newCategories.size());
    }


    /**
     * Generate a stubbed data set
     * @return a category map
     */
    private Map<String, Category> generateCategories(){
        final Map<String, Category> result = new HashMap<>();
        result.put(ID_PARENT_THREE_CHILDREN,generateCategoryWithChildren(ID_PARENT_THREE_CHILDREN,null,ID_PARENT_TWO_CHILDREN, ID_PARENT_ONE_CHILD,ID_PARENT_NO_CHILDREN));
        result.put(ID_PARENT_TWO_CHILDREN,generateCategoryWithChildren(ID_PARENT_TWO_CHILDREN,ID_PARENT_THREE_CHILDREN,"11","12"));
        result.put(ID_PARENT_ONE_CHILD,generateCategoryWithChildren(ID_PARENT_ONE_CHILD,ID_PARENT_THREE_CHILDREN,"21"));
        result.put(ID_PARENT_NO_CHILDREN,generateCategory(ID_PARENT_NO_CHILDREN,ID_PARENT_THREE_CHILDREN));
        return result;
    }

    private Category generateCategory(String id, String idParent){
        final Category result = new Category();
        result.setId(id);
        result.setParentId(idParent);
        return result;
    }

    private Category generateCategoryWithChildren(String id, String idParent,String... childrenIds){
        final Category result =generateCategory(id,idParent);
        for (String childId:childrenIds) {
            result.getChildren().add(generateCategory(childId,id));

        }
        return result;
    }

}
