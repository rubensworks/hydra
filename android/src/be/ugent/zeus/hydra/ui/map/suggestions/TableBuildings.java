/**
 *
 * @author Tom Naessens Tom.Naessens@UGent.be 3de Bachelor Informatica Universiteit Gent
 *
 */
package be.ugent.zeus.hydra.ui.map.suggestions;

import android.net.Uri;
import android.provider.BaseColumns;

public class TableBuildings {

    public static final String AUTHORITY = "be.ugent.zeus.hydra.ui.map.suggestions.BuildingSuggestionProvider";

    // BaseColumn contains _id.
    public static final class Buildings implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://be.ugent.zeus.hydra.ui.map.suggestions.BuildingSuggestionProvider");
        // Table column
        public static final String NAME = "NAME";
        public static final String DISTANCE = "DISTANCE";
    }
}
