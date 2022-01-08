package com.example.sms_receiver;

import static com.example.sms_receiver.MainActivity.ChosenApps;
import static com.example.sms_receiver.MainActivity.loadSharedPreferencesLogList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<AppInfo> fields;
    final PackageManager pm;
    Context OurContext;

    AppsAdapter(Context context, List<AppInfo> fields) {
        this.inflater = LayoutInflater.from(context);
        this.fields = fields;
        this.pm = context.getPackageManager();
        OurContext = context;
    }

    @NonNull
    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.app_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppsAdapter.ViewHolder holder, int position) {
        AppInfo field = fields.get(position);
        holder.packageName.setText(field.label);
        holder.logo.setImageDrawable(field.logo);
        holder.button.setChecked(field.isChosen);
        holder.button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                field.isChosen = !field.isChosen;
                ChosenApps.remove(field.packageName);
                ChosenApps.put(field.packageName, field.isChosen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        final TextView packageName;
        final ImageView logo;
        final CheckBox button;
        ViewHolder(View view){
            super(view);
            packageName = view.findViewById(R.id.package_name);
            logo = view.findViewById(R.id.logo);
            button = view.findViewById(R.id.choose_button);
        }
    }
}
