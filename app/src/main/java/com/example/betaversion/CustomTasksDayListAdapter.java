package com.example.betaversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * The type Custom tasks day list adapter.
 */
public class CustomTasksDayListAdapter extends BaseAdapter{

    Context context;

    ArrayList<String> lists;
    LayoutInflater inflter;
    ArrayList<TasksDay> list_values;

    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-M-dd", new Locale("he"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE dd MMM yyyy", new Locale("he"));

    /**
     * Instantiates a new Custom tasks day list adapter.
     *
     * @param applicationContext the application context
     * @param lists              the lists
     * @param list_values        the list values
     */
    public CustomTasksDayListAdapter(Context applicationContext, ArrayList<String> lists, ArrayList<TasksDay> list_values) {
        this.context = applicationContext;
        this.lists = lists;
        this.list_values=list_values;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return lists.size();
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

        view = inflter.inflate(R.layout.custom_listview_layout_tasks_day_list, null);
        TextView title = (TextView) view.findViewById(R.id.item_title);

        TextView tv_day = (TextView) view.findViewById(R.id.ca_tv_day);
        TextView tv_month = (TextView) view.findViewById(R.id.ca_tv_month);
        TextView tv_date = (TextView) view.findViewById(R.id.ca_tv_date);
        String get_date = list_values.get(i).getTasksDayDate();
        String[] date=convert_date(get_date);
        tv_day.setText(date[0]);
        tv_date.setText(date[1]);
        tv_month.setText(date[2]);

        title.setText(lists.get(i));
        return view;
    }

    /**
     * שינוי פורמט התאריך.
     *
     * @param date the date
     * @return the string [ ]
     */
// convert date from dd-MM-yyyy to english
    public String[] convert_date(String date)
    {
        String[] result=new String[3];

        try {
            Date result_date=inputDateFormat.parse(date);
            String outputDateString = dateFormat.format(result_date);
            String[] items1 = outputDateString.split(" ");
            result[0] = items1[2];
            result[1] = items1[1];
            result[2] = items1[0];

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return result;
    }
}
