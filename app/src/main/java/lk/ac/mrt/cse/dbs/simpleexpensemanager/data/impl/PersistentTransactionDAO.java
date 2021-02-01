package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase db;
    private ArrayList<Transaction> transactions=new ArrayList<>();

    public PersistentTransactionDAO(SQLiteDatabase db) {
        this.db = db;
        try{
            Cursor c=this.db.rawQuery("SELECT * FROM Transactions ",null);
            c.moveToFirst();

            while (c!=null){
                Transaction transaction=new Transaction(
                        new Date(c.getLong(c.getColumnIndex("date"))),
                        c.getString(c.getColumnIndex("accountNumber")),
                        (c.getInt(c.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        Double.parseDouble(c.getString(c.getColumnIndex("amount")))
                );

                this.transactions.add(transaction);
                c.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction=new Transaction(date,accountNo,expenseType,amount);
        transactions.add(transaction);
        String insert_query = "INSERT INTO Transactions (date,accountNumber,expenseType,amount) VALUES (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(insert_query);

        statement.bindString(2,accountNo);
        statement.bindLong(3,(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(4,amount);
        statement.bindLong(1,date.getTime());

        statement.executeInsert();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transdetail = new ArrayList<>();
        String TRANS_DETAIL_SELECT_QUERY = "SELECT * FROM Transactions LIMIT"+limit;
        Cursor cursor = db.rawQuery(TRANS_DETAIL_SELECT_QUERY, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction trans=new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getString(cursor.getColumnIndex("accountNumber")),
                        (cursor.getInt(cursor.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        cursor.getDouble(cursor.getColumnIndex("amount")));
                transdetail.add(trans);

            } while (cursor.moveToNext());
        }

        return  transdetail;
    }
}
