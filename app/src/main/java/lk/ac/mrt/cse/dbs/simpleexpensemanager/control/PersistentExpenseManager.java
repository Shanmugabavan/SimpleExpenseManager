package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager {
    Context context;
    public PersistentExpenseManager(Context ctx)  {
        context=ctx;
        try {

            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }
    @Override

    public void setup() throws ExpenseManagerException {

        SQLiteDatabase db = context.openOrCreateDatabase("180596E", context.MODE_PRIVATE, null);

        // create the databases.
        db.execSQL("CREATE TABLE IF NOT EXISTS Account(" +
                "accountNumber VARCHAR PRIMARY KEY," +
                "bankName VARCHAR," +
                "accountHolderName VARCHAR," +
                "balance REAL" +
                " );");                              //dummy database structure create


        db.execSQL("CREATE TABLE IF NOT EXISTS Transactions(" +
                "transactionId INTEGER PRIMARY KEY," +
                "accountNumber VARCHAR," +
                "expenseType INT," +
                "amount REAL," +
                "date DATE," +
                "FOREIGN KEY (accountNumber) REFERENCES Account(accountNo)" +
                ");");




        PersistentAccountDAO accountDAO = new PersistentAccountDAO(db);

        setAccountsDAO(accountDAO);

        setTransactionsDAO(new PersistentTransactionDAO(db));
    }
}
