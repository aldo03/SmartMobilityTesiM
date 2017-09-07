package utils.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BBC on 10/02/2017.
 */

public class HttpUtils {

    private static final String REQUEST = "http://127.0.0.1:8000";

    public static String POST(String msg) throws IOException {
        String response = "";
        URL url = new URL(REQUEST);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
        wr.write(msg);
        wr.flush();
        wr.close();

        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        System.out.println("InStream");
        response = readStream(in);
        in.close();
        urlConnection.disconnect();

        return response;
    }

    private static String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String content;
        while ((content = reader.readLine()) != null) {
            sb.append(content);
        }
        return sb.toString();
    }

}
