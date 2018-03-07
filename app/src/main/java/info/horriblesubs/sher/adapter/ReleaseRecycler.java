package info.horriblesubs.sher.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.horriblesubs.sher.R;
import info.horriblesubs.sher.activity.Detail;
import info.horriblesubs.sher.model.Item;
import info.horriblesubs.sher.model.ReleaseItem;

/**
 * ReleaseRecycler
 */

public class ReleaseRecycler extends RecyclerView.Adapter<ReleaseRecycler.ViewHolder> {

    private Context context;
    private List<ReleaseItem> releaseItems;

    public ReleaseRecycler(Context context, List<ReleaseItem> releaseItems) {
        this.context = context;
        this.releaseItems = releaseItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_release_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ReleaseItem releaseItem = releaseItems.get(viewHolder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(releaseItem.title + " - " + releaseItem.number);
                if (releaseItem.link480 != null && !releaseItem.link480.isEmpty())
                    builder.setPositiveButton("480p", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDownload(releaseItem.link480);
                            dialog.dismiss();
                        }
                    });
                if (releaseItem.link720 != null && !releaseItem.link720.isEmpty())
                    builder.setNegativeButton("720p", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDownload(releaseItem.link720);
                            dialog.dismiss();
                        }
                    });
                if (releaseItem.link1080 != null && !releaseItem.link1080.isEmpty())
                    builder.setNeutralButton("1080p", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDownload(releaseItem.link1080);
                            dialog.dismiss();
                        }
                    });
                builder.create().show();
            }
        });
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ReleaseRecycler.ViewHolder holder, int position) {
        try {
            holder.textView1.setText(Html.fromHtml(releaseItems.get(position).title));
            holder.textView2.setText(releaseItems.get(position).number);
            if (releaseItems.get(position).link480 == null ||
                    releaseItems.get(position).link480.isEmpty())
                holder.textView3.setVisibility(View.GONE);
            if (releaseItems.get(position).link720 == null ||
                    releaseItems.get(position).link720.isEmpty())
                holder.textView4.setVisibility(View.GONE);
            if (releaseItems.get(position).link1080 == null ||
                    releaseItems.get(position).link1080.isEmpty())
                holder.textView5.setVisibility(View.GONE);
            final Item item = releaseItems.get(position);
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (item.link == null) {
                        Toast.makeText(context, "Page Unavailable", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    Intent intent = new Intent(context, Detail.class);
                    String[] s = item.link.split("/");
                    String link = s[s.length - 1];
                    intent.putExtra("link", link);
                    context.startActivity(intent);
                    return true;
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return releaseItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void startDownload(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }

    public void onQueryUpdate(List<ReleaseItem> releaseItems) {
        this.releaseItems = releaseItems;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        View layout;

        ViewHolder(View view) {
            super(view);
            layout = view;
            textView1 = layout.findViewById(R.id.textView1);
            textView2 = layout.findViewById(R.id.textView2);
            textView3 = layout.findViewById(R.id.textView3);
            textView4 = layout.findViewById(R.id.textView4);
            textView5 = layout.findViewById(R.id.textView5);
        }
    }
}