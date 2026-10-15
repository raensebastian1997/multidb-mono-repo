package com.ismarianto.microservice.lib;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestConsume {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T call(String urlStr, String method, Object body, Class<T> responseType) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(method.toUpperCase());
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(30_000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "RestConsume");
            conn.setRequestProperty("Connection", "keep-alive");

            if (body != null && !method.equalsIgnoreCase("GET")) { //dafult get response
                conn.setDoOutput(true);
                String jsonBody = (body instanceof String)
                        ? (String) body
                        : MAPPER.writeValueAsString(body);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseBody = readStream(is);

            if (status >= 400) {
                throw new RuntimeException("HTTP " + status + ": " + responseBody);
            }

            if (responseType == String.class) {
                return responseType.cast(responseBody);
            }
            log.info("detail response code : {}", status);
            log.info("detail response body : {}", responseBody);
            log.info("get ready response  : {}", MAPPER.readValue(responseBody, responseType));
            return MAPPER.readValue(responseBody, responseType);

        } catch (Exception e) {
            throw new RuntimeException("HTTP call gagal: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public static <T> T call(String url, String method, Class<T> responseType) {
        return call(url, method, null, responseType);
    }

    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}