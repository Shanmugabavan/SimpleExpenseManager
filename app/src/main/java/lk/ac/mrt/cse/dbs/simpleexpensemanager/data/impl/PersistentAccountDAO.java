package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteDatabase db;
    private HashMap<String,Account> accounts=new HashMap<>();

    public PersistentAccountDAO(SQLiteDatabase db) {
        this.db = db;
        try{
            Cursor c=this.db.rawQuery("SELECT * FROM Account ",null);
            c.moveToFirst();

            while (c!=null){
                Account account=new Account(
                        c.getString(c.getColumnIndex("accountNumber")),
                        c.getString(c.getColumnIndex("bankName")),
                        c.getString(c.getColumnIndex("accountHolderName")),
                        Double.parseDouble(c.getString(c.getColumnIndex("balance")))
                );

                this.accounts.put(account.getAccountNo(),account);
                c.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> account_Numbers_List=new ArrayList<>();
        for (String accountNumber:accounts.keySet()) {
            account_Numbers_List.add(accountNumber);
        }

        return account_Numbers_List;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList=new ArrayList<>();
        for (Account account:accounts.values()){
            accountList.add(account);
        }
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return accounts.get(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        accounts.put(account.getAccountNo(),account);
        String sql = "INSERT INTO Account (accountNumber,bankName,accountHolderName,balance) VALUES (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);



        statement.bindString(1, account.getAccountNo());
        statement.bindString(2, account.getBankName());
        statement.bindString(3, account.getAccountHolderName());
        statement.bindDouble(4, account.getBalance());


        statement.executeInsert();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        accounts.remove(accountNo);

        String sqlquery = "DELETE FROM Account WHERE accountNumber = ?";
        SQLiteStatement statement = db.compileStatement(sqlquery);
        statement.bindString(1,accountNo);
        statement.executeUpdateDelete();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account=accounts.get(accountNo);
        String sqlquery = "UPDATE Account SET balance = balance + ?";
        SQLiteStatement statement = db.compileStatement(sqlquery);
        if(expenseType == ExpenseType.EXPENSE){
            account.setBalance(account.getBalance()-amount);
            statement.bindDouble(1,-amount);
        }else{
            account.setBalance(account.getBalance()+amount);
            statement.bindDouble(1,amount);
        }

        statement.executeUpdateDelete();
    }
}
