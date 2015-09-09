package com.bala.learning.learning;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        String s = "/rtb/bid/mopub?cid=82375897235&mode=live";
        System.out.println(s.contains("/rtb"));
        String text = "<img src='data:image/png$bo_commaceltra' style='display: none'";
        System.out.println(text.replace("$bo_comma", ","));
        double res = ((Double.parseDouble("0.001658028")/ 100.0) * 1.38169 < 0d ? 1
				: 1.38169);
        System.out.println(res);
        String clkId = "348336$BO48113$BO129.67.145.168$BOMozilla/5.0 (iPhone; CPU iPhone OS 8_3 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12F70$BOidfa$BO101B376E-C2EB-499D-9137-99E987FE6D41$BO$BO$BO";
        String[] clkIdSplit = StringUtils.splitByWholeSeparatorPreserveAllTokens(clkId, "$BO");
        System.out.println(Arrays.toString(clkIdSplit));
        
    }
}
