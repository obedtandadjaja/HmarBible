package com.example.obedtandadjaja.hmarbible;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class BookDataSource {

    private SQLiteDatabase database;
    private DataBaseHelper helper;
    private final String DB_NAME;
    private Context context;

    /**
     * CategoryDataSource constructor
     * initialize database
     * @param context
     */
    public BookDataSource(Context context) {
        this.DB_NAME = "meitei.sqlite";
        this.helper = new DataBaseHelper(context, this.DB_NAME);
        this.database = helper.openDataBase();
        this.context = context;
    }

    /**
     * Get All The Books
     * @param
     * @return
     */
    public ArrayList<Book> getBooks()
    {
        Cursor cursor = database.rawQuery("select book, num_books from BOOK", null);
        cursor.moveToFirst();
        ArrayList<Book> books = cursorToBook(cursor);
        return books;
    }

    public ArrayList<Book> getBooksAlphabetically()
    {
        Cursor cursor = database.rawQuery("select book, num_books from BOOK order by book asc;", null);
        cursor.moveToFirst();
        ArrayList<Book> books = cursorToBook(cursor);
        return books;
    }

    public ArrayList<Book> getRelatedBooks(String name)
    {
        Cursor cursor = database.rawQuery("select book, num_books from BOOK where book like (\"%"+name+"%\") order by book asc;", null);
        cursor.moveToFirst();
        ArrayList<Book> book_array = new ArrayList<Book>();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            String book_name = cursor.getString(0);
            int num_chapters = cursor.getInt(1);
            book_array.add(new Book(book_name, num_chapters));
            cursor.moveToNext();
        }
        return book_array;
    }

    public ArrayList<Book> getRelatedBooksAlphabetically(String name)
    {
        Cursor cursor = database.rawQuery("select book, num_books from BOOK where book like (\"%"+name+"%\");", null);
        cursor.moveToFirst();
        ArrayList<Book> book_array = new ArrayList<Book>();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            String book_name = cursor.getString(0);
            int num_chapters = cursor.getInt(1);
            book_array.add(new Book(book_name, num_chapters));
            cursor.moveToNext();
        }
        return book_array;
    }

    public ArrayList<Integer> getNumChapters(String name)
    {
        Cursor cursor = database.rawQuery("select num_books from BOOK where book = \""+name+"\";", null);
        cursor.moveToFirst();
        ArrayList<Integer> num_chapters = new ArrayList<Integer>();
        for(int i = 1; i < cursor.getInt(0)+1; i++)
        {
            num_chapters.add(i);
        }
        return num_chapters;
    }

    public ArrayList<String> getVerses(Chapter chapter)
    {
        Cursor cursor = database.rawQuery("select id from BOOK where book = \""+chapter.getBook_name()+"\";", null);
        cursor.moveToFirst();
        int book_id = cursor.getInt(0);
        if(book_id > 66)
        {
            book_id -= 66;
        }
        cursor = database.rawQuery("select meitei from BIBLE where book_id = "+book_id+" and chapter = "+chapter.getChapter()+";", null);
        cursor.moveToFirst();
        ArrayList<String> verse_array = new ArrayList<String>();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            String verse = cursor.getString(0);
            verse_array.add(verse);
            cursor.moveToNext();
        }
        return verse_array;
    }

    /**
     * private method to get the query data into its format
     * @param cursor
     * @param
     * @return
     */
    private ArrayList<Book> cursorToBook(Cursor cursor)
    {
        ArrayList<Book> def_array = new ArrayList<Book>();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            String name = cursor.getString(0);
            int num_chapters = cursor.getInt(1);
            def_array.add(new Book(name, num_chapters));
            cursor.moveToNext();
        }
        return def_array;
    }
}
