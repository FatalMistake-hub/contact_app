package com.example.contactapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.contactapp.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int NEW_EDIT_CONTACT_ACTIVITY_REQUEST_CODE = 1;
    private static final int DETAIL_CONTACT_ACTIVITY_REQUEST_CODE = 2;
    private List<Contact> contacts;
    private ContactAdapter contactsAdapter;

    private AppDatabase appDatabase;
    private ContactDAO contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Contacts");

        appDatabase = AppDatabase.getInstance(this);
        contactDao = appDatabase.contactDao();



        loadData();

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rvContacts.addItemDecoration(itemDecoration);

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewContactFormIntent();
            }
        });
    }

    private void loadData() {
        contacts = contactDao.getAllContacts();
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                return contact.getName().compareToIgnoreCase(t1.getName());
            }
        });
        contactsAdapter = new ContactAdapter(contacts, new ContactAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                Intent intent = new Intent(MainActivity.this, DetailContact.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object_contact", contact) ;
                intent.putExtras(bundle);
                startActivityForResult(intent, DETAIL_CONTACT_ACTIVITY_REQUEST_CODE);
            }
        });
        binding.rvContacts.setAdapter(contactsAdapter);
        binding.rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_contact_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search contacts");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void openAddNewContactFormIntent() {
        Intent intent = new Intent(MainActivity.this, EditContact.class);
        intent.putExtra("is_edit_mode", false);
        startActivityForResult(intent, NEW_EDIT_CONTACT_ACTIVITY_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_EDIT_CONTACT_ACTIVITY_REQUEST_CODE || requestCode == DETAIL_CONTACT_ACTIVITY_REQUEST_CODE) {
            contacts.clear();
            loadData();
        }
    }
}