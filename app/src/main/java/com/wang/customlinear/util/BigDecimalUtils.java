package com.wang.customlinear.util;

import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by chenpengfei on 2016/11/23.
 */
public class BigDecimalUtils {

    public static float divide(int one, int two) {
        Log.e("CAHO", "divide: "+"one="+one +"weo"+two);
        return BigDecimal.valueOf(one).divide(BigDecimal.valueOf(two), 2, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
