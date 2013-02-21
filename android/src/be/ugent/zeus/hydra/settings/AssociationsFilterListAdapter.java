/**
 *
 * @author Tom Naessens Tom.Naessens@UGent.be 3de Bachelor Informatica Universiteit Gent
 *
 */
package be.ugent.zeus.hydra.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.caches.AssociationsCache;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AssociationsFilterListAdapter extends ArrayAdapter<PreferenceAssociation> implements StickyListHeadersAdapter {

    private LayoutInflater inflater;
    private AssociationsCache cache;
    private List<PreferenceAssociation> associations;

    public AssociationsFilterListAdapter(Context context, int iets, List<PreferenceAssociation> assocations) {
        super(context, iets, assocations);

        this.associations = assocations;
        inflater = LayoutInflater.from(context);

        cache = AssociationsCache.getInstance(context);
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
            convertView = inflater.inflate(R.layout.settings_filter_list_item, parent, false);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    PreferenceAssociation association = (PreferenceAssociation) cb.getTag();
                    association.setSelected(cb.isChecked());

                    HashSet<String> checked = cache.get("associations");;
                    if (checked == null) {
                        checked = new HashSet<String>();
                    }

                    if (cb.isChecked()) {
                        checked.add(association.getInternalName());
                    } else {
                        checked.remove(association.getInternalName());
                    }

                    cache.put("associations", checked);
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PreferenceAssociation preferenceAssociation = super.getItem(position);

        holder.checkBox.setText(preferenceAssociation.getName());
        holder.checkBox.setChecked(preferenceAssociation.isSelected());
        holder.internalName = preferenceAssociation.getInternalName();
        holder.checkBox.setTag(preferenceAssociation);

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

        holder.header_text.setText(super.getItem(position).getParentAssociation());
        return convertView;
    }

    //remember that these have to be static, postion=1 should walys return the same Id that is.
    @Override
    public long getHeaderId(int position) {
        return super.getItem(position).getParentAssociation().hashCode();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                associations = (List<PreferenceAssociation>) results.values;
                AssociationsFilterListAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<PreferenceAssociation> filteredResults = new ArrayList<PreferenceAssociation>();

                for (PreferenceAssociation preferenceAssociation : associations) {
                    if(preferenceAssociation.getName().contains(constraint)) {
                        filteredResults.add(preferenceAssociation);
                    }
                }
                
                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }
    
    class HeaderViewHolder {

        TextView header_text;
    }

    class ViewHolder {

        CheckBox checkBox;
        String internalName;
    }
}
