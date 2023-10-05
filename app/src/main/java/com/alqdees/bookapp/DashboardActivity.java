package com.alqdees.bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.alqdees.bookapp.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;
    private ActivityDashboardBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        setupViewPagerAdapter(binding.viewPager);
        binding.tableLayout.setupWithViewPager(binding.viewPager);
        binding.powerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
             startActivity( new Intent(DashboardActivity.this,MainActivity.class));
             finish();
            }
        });

        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,ProfileActivity.class));
            }
        });
    }
    private void setupViewPagerAdapter(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);
                categoryArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                ModelCategory modelAll = new ModelCategory("01","الكل","","1");
                ModelCategory modelMostView  = new ModelCategory("02","الأكثر مشاهدة","","1");
                ModelCategory modelMostDownload  = new ModelCategory("03","الاأكثر تنزيل","","1");
                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostView);
                categoryArrayList.add(modelMostDownload);
                viewPagerAdapter.addFragment(BookUserFragment.newInstance(
                        ""+modelAll.getId()
                        ,""+modelAll.getCategory(),
                        ""+modelAll.getUid()),modelAll.getCategory()
                );
                ////////////
                viewPagerAdapter.addFragment(BookUserFragment.newInstance(
                        ""+modelMostView.getId()
                        ,""+modelMostView.getCategory(),
                        ""+modelMostView.getUid()),modelMostView.getCategory()
                );
                ///////////////
                viewPagerAdapter.addFragment(BookUserFragment.newInstance(
                        ""+modelMostDownload.getId()
                        ,""+modelMostDownload.getCategory(),
                        ""+modelMostDownload.getUid()),modelMostDownload.getCategory()
                );

                viewPagerAdapter.notifyDataSetChanged();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Log.d("onDataChange: _____", "onDataChange: "+ds.getValue());
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);
                    viewPagerAdapter.addFragment(BookUserFragment.newInstance(""+model.getId()
                            ,""+model.getCategory()
                            ,""+model.getUid()),model.getCategory());
                    viewPagerAdapter.notifyDataSetChanged();
//                    AdapterPdfUser adapterPdfUser = new AdapterPdfUser(DashboardActivity.this,categoryArrayList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        viewPager.setAdapter(viewPagerAdapter);

    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<BookUserFragment> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        private void addFragment(BookUserFragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }


    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            binding.tvSubtitle.setText("لم تقم بتسجيل الدخول");
        }else{
            String email =firebaseUser.getEmail();
            binding.tvSubtitle.setText(email);
        }
    }
}