/**
 *
 * @author Tom Naessens Tom.Naessens@UGent.be 3de Bachelor Informatica Universiteit Gent
 *
 */
package be.ugent.zeus.hydra.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.Hydra;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.Activity;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ActivityListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Activity[] activities;
    private LayoutInflater inflater;
    private Context context;

    public ActivityListAdapter(Context context, List<Activity> items) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.activities = new Activity[items.size()];
        activities = items.toArray(activities);
        Arrays.sort(activities, new ActivityComparator());
    }

    @Override
    public int getCount() {
        return activities.length;
    }

    @Override
    public Object getItem(int position) {
        return activities[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_list_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.activity_item_title);
            holder.assocation = (TextView) convertView.findViewById(R.id.activity_item_association);
            holder.time = (TextView) convertView.findViewById(R.id.activity_item_time_location);
            holder.hilighted = (ImageView) convertView.findViewById(R.id.activity_hilighted_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Activity activity = activities[position];

        holder.title.setText(activity.title);
        holder.assocation.setText(activity.association.display_name);
        holder.time.setText(activity.start.substring(11, 16));

        if (activity.highlighted == 0) {
            holder.hilighted.setVisibility(ImageView.GONE);
        } else {
            holder.hilighted.setVisibility(ImageView.VISIBLE);
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.activity_list_header, parent, false);
            holder.header_text = (TextView) convertView.findViewById(R.id.header_text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        String headerChar = new SimpleDateFormat("dd MMMM", Hydra.LOCALE).format(activities[position].startDate);
        holder.header_text.setText(headerChar);
        return convertView;
    }

    //remember that these have to be static, postion=1 should walys return the same Id that is.
    @Override
    public long getHeaderId(int position) {
        return new SimpleDateFormat("dd MMMM").format(activities[position].startDate).hashCode();
    }

    class HeaderViewHolder {

        TextView header_text;
    }

    class ViewHolder {

        TextView title;
        TextView assocation;
        TextView time;
        ImageView hilighted;
    }

    private class ActivityComparator implements Comparator<Activity> {

        public int compare(Activity item1, Activity item2) {
            return item1.start.compareTo(item2.start);
        }
    }
}
