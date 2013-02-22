/**
 *
 * @author Tom Naessens Tom.Naessens@UGent.be 3de Bachelor Informatica Universiteit Gent
 *
 */
package be.ugent.zeus.hydra.ui.map.suggestions;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import com.actionbarsherlock.widget.SearchView;

public class BuildingSuggestionsAdapter extends CursorAdapter {

    private static final int QUERY_LIMIT = 50;
    private LayoutInflater inflater;
    private SearchView searchView;
    private SearchableInfo searchable;

    public BuildingSuggestionsAdapter(Context context, SearchableInfo info, SearchView searchView) {
        super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.searchable = info;
        this.searchView = searchView;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        String name = c.getString(c.getColumnIndex(TableBuildings.Buildings.NAME));
        TextView namet = (TextView) v.findViewById(R.id.resto_search_name);
        namet.setText(name);

        String man = c.getString(c.getColumnIndex(TableBuildings.Buildings.DISTANCE));
        TextView manuf = (TextView) v.findViewById(R.id.resto_search_distance);
        manuf.setText(man);

        Toast.makeText(context, name + " " + man, Toast.LENGTH_LONG).show();
    }

    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        return this.inflater.inflate(R.layout.resto_search_suggestion_view, null);
    }

    /**
     * Use the search suggestions provider to obtain a live cursor. This will be called in a worker
     * thread, so it's OK if the query is slow (e.g. round trip for suggestions). The results will
     * be processed in the UI thread and changeCursor() will be called.
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String query = (constraint == null) ? "" : constraint.toString();
        /**
         * for in app search we show the progress spinner until the cursor is returned with the
         * results.
         */
        Cursor cursor = null;
        if (searchView.getVisibility() != View.VISIBLE
            || searchView.getWindowVisibility() != View.VISIBLE) {
            return null;
        }
        try {
            cursor = getSuggestions(searchable, query, QUERY_LIMIT);
            // trigger fill window so the spinner stays up until the results are copied over and
            // closer to being ready
            if (cursor != null) {
                cursor.getCount();
                return cursor;
            }
        } catch (RuntimeException e) {
        }
        // If cursor is null or an exception was thrown, stop the spinner and return null.
        // changeCursor doesn't get called if cursor is null
        return null;
    }

    public Cursor getSuggestions(SearchableInfo searchable, String query, int limit) {

        if (searchable == null) {
            return null;
        }

        String authority = searchable.getSuggestAuthority();
        if (authority == null) {
            return null;
        }

        Uri.Builder uriBuilder = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(authority)
            .query("")
            .fragment("");

        // if content path provided, insert it now
        final String contentPath = searchable.getSuggestPath();
        if (contentPath != null) {
            uriBuilder.appendEncodedPath(contentPath);
        }

        // append standard suggestion query path
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        // get the query selection, may be null
        String selection = searchable.getSuggestSelection();
        // inject query, either as selection args or inline
        String[] selArgs = null;
        if (selection != null) {    // use selection if provided
            selArgs = new String[]{query};
        } else {                    // no selection, use REST pattern
            uriBuilder.appendPath(query);
        }

        if (limit > 0) {
            uriBuilder.appendQueryParameter("limit", String.valueOf(limit));
        }

        Uri uri = uriBuilder.build();

        // finally, make the query
        return mContext.getContentResolver().query(uri, null, selection, selArgs, null);
    }
}
