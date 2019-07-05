package com.example.myvolleyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myvolleyapp.model.FollowerPojo;
import com.example.myvolleyapp.model.GithubUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username)
    TextView txt;
    @BindView(R.id.id)
    TextView id;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    MyAdapter adapter;
    ArrayList<FollowerPojo> followersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        callVolley();
        //callJsonObjectVolley();
    }

    private void callVolley(){
        String url = "https://api.github.com/users/cmpundhir";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //txt.setText(response);
                Gson gson = new Gson();
                GithubUser user = gson.fromJson(response,GithubUser.class);
                txt.setText(user.getLogin());
                id.setText(user.getId()+"");
                Glide.with(MainActivity.this).load(user.getAvatarUrl()).into(imageView);
                callJsonObjectVolley(user.getFollowersUrl());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                txt.setText("error : "+error);
                Toast.makeText(MainActivity.this, "erorr : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void callJsonObjectVolley(String url){
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                for(int i=0;i<response.length();i++){
                    try {
                        FollowerPojo followerPojo = gson.fromJson(response.get(i).toString(),FollowerPojo.class);
                        followersList.add(followerPojo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void requestFollowers(String url){
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //txt.setText(response);
                Gson gson = new Gson();
                followersList = gson.fromJson(response,followersList.getClass());
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                txt.setText("error : "+error);
                Toast.makeText(MainActivity.this, "erorr : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.follower_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FollowerPojo user = followersList.get(position);
            holder.un.setText(user.getLogin());
            holder.id.setText(user.getId()+"");
            Glide.with(MainActivity.this).load(user.getAvatarUrl()).into(holder.img);
        }

        @Override
        public int getItemCount() {
            return followersList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView un,id;
            ImageView img;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                un = itemView.findViewById(R.id.username);
                id = itemView.findViewById(R.id.id);
                img = itemView.findViewById(R.id.imageView2);
            }
        }
    }

}
