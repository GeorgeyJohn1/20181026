package nyc.gj.com.a20180402_gj_nycschools.http;

import android.content.Context;

/**
 * Created by georgey on 16-01-2018.
 */

public class HttpManager {

    public static final String BASE_URL ="https://data.cityofnewyork.us/resource/";

    public static String get(Context context, String urlWithParam) {

        String paramFormatted = BASE_URL + urlWithParam;

        paramFormatted = paramFormatted.replace(" ", "%20");

        System.out.println(paramFormatted);

        if (!CheckConnectivity.check(context)) {
            return null;
        }

        return HttpRequest.get(paramFormatted)
                .accept("application/json").body();
    }

    public static String get(Context context, String urlWithParam, String authorization) {

        String paramFormatted = BASE_URL + urlWithParam;

        paramFormatted = paramFormatted.replace(" ", "%20");

        System.out.println(paramFormatted);

        if (!CheckConnectivity.check(context)) {
            return null;
        }

        return HttpRequest.get(paramFormatted)
                .accept("application/json")
                .authorization(authorization)
                .body();
    }

    public static String post(Context context, String url, String json) {

        System.out.println(BASE_URL + url + "<json>" + json);

        if (!CheckConnectivity.check(context)) {
            return null;
        }

        return HttpRequest.post(BASE_URL + url).contentType("application/json").send(json).body();
    }

    public static String post(Context context, String url, String json, String authorization) {

        System.out.println(BASE_URL + url + "<json>" + json);

        if (!CheckConnectivity.check(context)) {
            return null;
        }

        if (json != null) {
            return HttpRequest.post(BASE_URL + url)
                    .authorization(authorization)
                    .contentType("application/json")
                    .send(json)
                    .body();
        } else {
            return HttpRequest.post(BASE_URL + url)
                    .authorization(authorization)
                    .contentType("application/json")
                    .body();
        }
    }
}
