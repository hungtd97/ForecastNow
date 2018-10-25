package com.example.hunghuc.forecastnow.Adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.hunghuc.forecastnow.CityChosenActivity;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.ListCityActivity;
import com.example.hunghuc.forecastnow.R;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserCityListAdapter extends BaseAdapter {

    private ListCityActivity listCityActivity;
    private ArrayList<City> listCity;
    private SQLiteOpenHelper mySql;

    public UserCityListAdapter(ListCityActivity listCityActivity, ArrayList<City> listCity) {
        this.listCityActivity = listCityActivity;
        this.listCity = listCity;
    }

    @Override
    public int getCount() {
        return listCity.size();
    }

    @Override
    public Object getItem(int position) {
        return listCity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHolder myholder = null;
        if(convertView == null){
            convertView = listCityActivity.getLayoutInflater().inflate(R.layout.listusercity, null);
            myholder = new MyHolder();
            myholder.txtCityName = convertView.findViewById(R.id.txtCityName);
            myholder.txtNationName = convertView.findViewById(R.id.txtNationName);
            myholder.btnDelete = convertView.findViewById(R.id.btnDelete);
            myholder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mySql == null) {
                        mySql = new SQLiteHelper(listCityActivity, "ForecastNow", 1);
                    }
                    SQLiteDatabase db = mySql.getReadableDatabase();
                    int cursorDelete = db.delete("City", "id=?", new String[]{listCity.get(position).getId() + ""});
                    db.close();
                    listCity.remove(position);
                    notifyDataSetChanged();

                }
            });
            convertView.setTag(myholder);

        }else{
            myholder = (MyHolder) convertView.getTag();
        }
        myholder.txtCityName.setText(listCity.get(position).getCity_name());
        myholder.txtNationName.setText(listCity.get(position).getNation_name());
        return convertView;
    }

    class MyHolder{
        TextView txtCityName;
        TextView txtNationName;
        Button btnDelete;
    }
}


