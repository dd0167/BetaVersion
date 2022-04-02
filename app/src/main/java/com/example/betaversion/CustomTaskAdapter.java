package com.example.betaversion;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CustomTaskAdapter extends BaseAdapter{
    Context context;
    ArrayList<String> tasks;
    LayoutInflater inflter;
    ArrayList<Task> task_values;
//    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
//    SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-M-dd", new Locale("he"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE dd MMM yyyy", new Locale("he"));

    SimpleDateFormat dateFormat_before = new SimpleDateFormat("dd-MM-yyyy", new Locale("he"));
    SimpleDateFormat dateFormat_after = new SimpleDateFormat("yyyy-MM-dd", new Locale("he"));

    public CustomTaskAdapter(Context applicationContext, ArrayList<String> tasks, ArrayList<Task> task_values) {
        this.context = applicationContext;
        this.tasks = tasks;
        this.task_values=task_values;
        inflter = (LayoutInflater.from(applicationContext));
    }



    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.custom_listview_layout_tasks, null);
        TextView title = (TextView) view.findViewById(R.id.item_title);
        ImageView iv_task_image=(ImageView) view.findViewById(R.id.iv_task_image);
        TextView tv_time_of_the_task=(TextView) view.findViewById(R.id.tv_time_of_the_task);
        TextView tv_task_completion_date=(TextView) view.findViewById(R.id.tv_task_completion_date);
        TextView tv_task_notes=(TextView) view.findViewById(R.id.tv_task_notes);
        TextView tv_task_address=(TextView) view.findViewById(R.id.tv_task_address);
        View task_color_view=(View) view.findViewById(R.id.task_color_view);

        TextView tv_day = (TextView) view.findViewById(R.id.ca_tv_day);
        TextView tv_month = (TextView) view.findViewById(R.id.ca_tv_month);
        TextView tv_date = (TextView) view.findViewById(R.id.ca_tv_date);
        String get_date = task_values.get(i).getTaskCreationDate();
        String[] date=convert_date(get_date);
        tv_day.setText(date[0]);
        tv_date.setText(date[1]);
        tv_month.setText(date[2]);

        title.setText(tasks.get(i));
        Glide.with(iv_task_image.getContext()).load(task_values.get(i).getTaskPictureUid()).into(iv_task_image);
        tv_time_of_the_task.setText("שעת יעד: "+task_values.get(i).getTaskHour());

        String target_date=task_values.get(i).getTaskDay();
        try {
            Date result_date = dateFormat_after.parse(target_date);
            target_date = dateFormat_before.format(result_date);
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        tv_task_completion_date.setText("תאריך יעד: "+target_date);

        if (!task_values.get(i).getTaskNotes().isEmpty())
        {
            tv_task_notes.setText("הערות: "+task_values.get(i).getTaskNotes());
        }
        else
        {
            tv_task_notes.setText("");
        }
        if (!task_values.get(i).getTaskAddress().isEmpty())
        {
            tv_task_address.setText("כתובת המטלה: "+task_values.get(i).getTaskAddress());
        }
        else
        {
            tv_task_address.setText("");
        }
        String color=task_values.get(i).getTaskColor();
        task_color_view.setBackgroundColor(Color.parseColor(color));

        return view;
    }

    // convert date from dd-MM-yyyy to english
    public String[] convert_date(String date)
    {
        String[] result=new String[3];

        try {
            Date result_date=inputDateFormat.parse(date);
            String outputDateString = dateFormat.format(result_date);
            String[] items1 = outputDateString.split(" ");
            String day = items1[2];
            String dd = items1[1];
            String mon = items1[0];

            result[0]=day;
            result[1]=dd;
            result[2]=mon;
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return result;
    }
}
