package yc.pointer.trip.network;

import android.util.Log;
import okhttp3.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by 张继
 * 2017/7/14
 * 10:50
 * 日志采集器
 */
public class LogInterceptor implements Interceptor {

    private static String TAG = "LogInterceptor-->";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration=endTime-startTime;
        MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Log.e(TAG,"\n");
        Log.e(TAG,"----------Start----------------");
        Log.e(TAG, "| Request:"+request.toString());

        String method=request.method();
        if("POST".equals(method)){
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                Log.e(TAG, "| RequestParams:{"+sb.toString()+"}");
            }
        }
        Log.e(TAG, "| Response body:" + content);
        Log.e(TAG,"----------End:"+duration+"毫秒----------");
        ResponseBody body = ResponseBody.create(mediaType, content);
        return  response.newBuilder()
                .body(body)
                .build();

    }
}
