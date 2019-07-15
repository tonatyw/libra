package tonatyw.libra.util;

import java.util.Locale;

public class LocaleFactory {
    public LocaleFactory(){}
    public static Locale getLocale(String lang,String country){
        Locale l = new Locale(lang, country);
        return l;
    }
}
