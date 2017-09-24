package fi.jamk.vko_38_picasso_demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jere on 23.9.2017.
 */

public class ImageFeedArrayAdapter extends ArrayAdapter<String> {
    // application context
    private Context context;
    // phone data (names)
    private ArrayList<String> imgPaths;

    private ArrayList<String> imgDescs;

    // get application context and phones data to adapter
    public ImageFeedArrayAdapter(Context context, ArrayList<String> paths, ArrayList<String> descs) {
        super(context, R.layout.image_layout, R.id.imageView, paths);
        this.context = context;
        imgPaths = paths;
        imgDescs = descs;
    }

    // populate every row in ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get row
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.image_layout, parent, false);
        // show phone icon/image
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        Picasso.with(context).load(imgPaths.get(position)).into(imageView);

        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        textView.setText(imgDescs.get(position));
        // return row view
        return rowView;
    }
}
