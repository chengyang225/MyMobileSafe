package com.testdemo.chanian.mymobilesafe;

import android.test.AndroidTestCase;

import java.util.Random;

import db.dao.BlackNumberDao;

/**
 * Created by ChanIan on 16/5/4.
 */
public class TestBlackDao extends AndroidTestCase {
    private BlackNumberDao mDao;

    @Override
    protected void tearDown() throws Exception {
        mDao = null;
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        mDao = new BlackNumberDao(getContext());
        super.setUp();
    }

    public void testAdd() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            String phone = "185" + i;
            String mode = "" + (random.nextInt(3) + 1);
            mDao.insert(phone, mode);
        }
    }
    public void testDelete(){
        mDao.delete("18580");
    }
}
