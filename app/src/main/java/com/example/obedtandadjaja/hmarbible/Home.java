package com.example.obedtandadjaja.hmarbible;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by obedtandadjaja on 8/4/15.
 */
public class Home extends Fragment {

    ToggleButton sort;
    ListView book_list;
    EditText book_search;
    BookDataSource book_data_source;
    BookListAdapter book_list_adapter;
    SharedPreferencesLibrary spl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sort = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
        book_list = (ListView) rootView.findViewById(R.id.listView);
        book_search = (EditText) rootView.findViewById(R.id.editText);

        book_data_source = new BookDataSource(getActivity());
        ArrayList<Book> book_array = new ArrayList<Book>();

        spl = new SharedPreferencesLibrary(getActivity());

        // Populating Data
        sort.setChecked(spl.getSort());

        if (sort.isChecked()) {
            book_array = book_data_source.getBooksAlphabetically();
            book_list_adapter = new BookListAdapter(book_array, getActivity());
            book_list.setAdapter(book_list_adapter);
        } else {
            book_array = book_data_source.getBooks();
            book_list_adapter = new BookListAdapter(book_array, getActivity());
            book_list.setAdapter(book_list_adapter);
        }

        book_search.addTextChangedListener(new TextWatcher() {

            ArrayList<Book> book_array = new ArrayList<Book>();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sort.isChecked()) {
                    book_array = book_data_source.getRelatedBooksAlphabetically(s.toString());
                    book_list_adapter = new BookListAdapter(book_array, getActivity());
                    book_list.setAdapter(book_list_adapter);
                } else {
                    book_array = book_data_source.getRelatedBooks(s.toString());
                    book_list_adapter = new BookListAdapter(book_array, getActivity());
                    book_list.setAdapter(book_list_adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<Book> book_array = new ArrayList<Book>();

                if (book_search.getText().length() > 0) {
                    if (sort.isChecked()) {
                        spl.setSort(true);
                        book_array = book_data_source.getRelatedBooksAlphabetically(book_search.getText().toString());
                        book_list_adapter = new BookListAdapter(book_array, getActivity());
                        book_list.setAdapter(book_list_adapter);
                    } else {
                        spl.setSort(false);
                        book_array = book_data_source.getRelatedBooks(book_search.getText().toString());
                        book_list_adapter = new BookListAdapter(book_array, getActivity());
                        book_list.setAdapter(book_list_adapter);
                    }
                } else {
                    if (sort.isChecked()) {
                        spl.setSort(true);
                        book_array = book_data_source.getBooksAlphabetically();
                        book_list_adapter = new BookListAdapter(book_array, getActivity());
                        book_list.setAdapter(book_list_adapter);
                    } else {
                        spl.setSort(false);
                        book_array = book_data_source.getBooks();
                        book_list_adapter = new BookListAdapter(book_array, getActivity());
                        book_list.setAdapter(book_list_adapter);
                    }
                }
            }
        });

        book_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView text = (TextView) view.findViewById(R.id.textView);

                final String book_name = text.getText().toString();

                ArrayList<Integer> chapter_array = book_data_source.getNumChapters(text.getText().toString());

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.choose_chapter);

                GridView chapter_list = (GridView) dialog.findViewById(R.id.listView);

                ChooseChapterListAdapter chapter_list_adapter = new ChooseChapterListAdapter(chapter_array, getActivity());
                chapter_list.setAdapter(chapter_list_adapter);

                dialog.show();

                chapter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Button button = (Button) view.findViewById(R.id.button);
                        button.setBackgroundColor(Color.parseColor("#ffffff"));

                        spl.setBibleIndex(new Chapter(book_name, Integer.parseInt(button.getText().toString())));

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, new Bible());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                        dialog.dismiss();
                    }
                });
            }
        });
        return rootView;
    }
}
