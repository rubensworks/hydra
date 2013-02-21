/**
 *
 * @author Tom Naessens Tom.Naessens@UGent.be 3de Bachelor Informatica Universiteit Gent
 *
 */
package be.ugent.zeus.hydra.settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import be.ugent.zeus.hydra.AbstractSherlockActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.caches.AssociationsCache;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.XMLPropertyListParser;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssociationsFilter extends AbstractSherlockActivity implements AbsListView.OnScrollListener {

    private static final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
    public static final String FILTERED_ACTIVITIES = "FILTERED_ACTIVITIES";
    private int firstVisible;
    private StickyListHeadersListView stickyList;
    ArrayAdapter<PreferenceAssociation> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle(R.string.title_settings_associations_filter);

        stickyList = (StickyListHeadersListView) findViewById(R.id.list);
        stickyList.setOnScrollListener(this);

        // We can't put extra's in the HTML, so let's do it here.
        getIntent().putExtra("class", Settings.class.getCanonicalName());

        if (savedInstanceState != null) {
            firstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
        }
        NSArray assocations = null;
        try {
            assocations = (NSArray) XMLPropertyListParser.parse(getResources()
                .openRawResource(R.raw.assocations));
        } catch (Exception ex) {
            Logger.getLogger(AssociationsFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

        HashMap<String, String> centraal = new HashMap<String, String>();
        for (int i = 0; i < assocations.count(); i++) {
            NSDictionary association = (NSDictionary) assocations.objectAtIndex(i);
            if (((NSString) association.objectForKey("internalName")).toString()
                .equals(((NSString) association.objectForKey("parentAssociation")).toString())) {
                centraal.put(((NSString) association.objectForKey("internalName")).toString(),
                    ((NSString) association.objectForKey("displayName")).toString());
            }
        }

        HashSet<String> checkedAssociations = AssociationsCache.getInstance(this).get("associations");

        ArrayList<PreferenceAssociation> associationList = new ArrayList<PreferenceAssociation>();
        for (int i = 0; i < assocations.count(); i++) {
            NSDictionary item = (NSDictionary) assocations.objectAtIndex(i);

            String name;
            if (item.objectForKey("fullName") != null) {
                name = ((NSString) item.objectForKey("fullName")).toString();
            } else {
                name = ((NSString) item.objectForKey("displayName")).toString();
            }
            String internalName = ((NSString) item.objectForKey("internalName")).toString();

            boolean checked = false;
            if (checkedAssociations != null) {
                checked = checkedAssociations.contains(internalName);
            }

            PreferenceAssociation association =
                new PreferenceAssociation(
                name,
                internalName,
                centraal.get(((NSString) item.objectForKey("parentAssociation")).toString()),
                checked);

            associationList.add(association);
        }


        listAdapter = new AssociationsFilterListAdapter(this, 0, associationList);

        stickyList.setAdapter(listAdapter);
        stickyList.setSelection(firstVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getSupportMenuInflater().inflate(R.menu.building_search, menu);

        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                Log.i("YOUTIPED", newText);
                listAdapter.getFilter().filter(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                // this is your adapter that will be filtered
                Log.i("YOUTIPED", query);
                listAdapter.getFilter().filter(query);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LIST_POSITION, firstVisible);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
        int visibleItemCount, int totalItemCount) {
        this.firstVisible = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}