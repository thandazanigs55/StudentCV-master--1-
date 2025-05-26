package com.example.studentcv;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance(String apiKey) {
        if (retrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Add API key dynamically to all requests
            Interceptor apiKeyInterceptor = chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + apiKey)
                        .build();
                return chain.proceed(request);
            };

            // Intercept and sanitize response to handle missing 'license' field
            Interceptor responseInterceptor = chain -> {
                Response originalResponse = chain.proceed(chain.request());
                String rawJson = originalResponse.body().string();
                String sanitizedJson = sanitizeResponse(rawJson);

                return originalResponse.newBuilder()
                        .body(okhttp3.ResponseBody.create(sanitizedJson, originalResponse.body().contentType()))
                        .build();
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(apiKeyInterceptor)
                    .addInterceptor(responseInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.gemini.com/") // Replace with Gemini's actual base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static String sanitizeResponse(String jsonResponse) {
        try {
            com.google.gson.JsonObject jsonObject = com.google.gson.JsonParser.parseString(jsonResponse).getAsJsonObject();
            com.google.gson.JsonArray candidates = jsonObject.getAsJsonArray("candidates");

            for (com.google.gson.JsonElement candidate : candidates) {
                if (candidate.getAsJsonObject().has("citationMetadata")) {
                    com.google.gson.JsonObject citationMetadata = candidate.getAsJsonObject().getAsJsonObject("citationMetadata");
                    com.google.gson.JsonArray citationSources = citationMetadata.getAsJsonArray("citationSources");

                    for (com.google.gson.JsonElement source : citationSources) {
                        com.google.gson.JsonObject sourceObject = source.getAsJsonObject();
                        if (!sourceObject.has("license")) {
                            sourceObject.addProperty("license", "Unknown License");
                        }
                    }
                }
            }
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return jsonResponse; // Return the original response if sanitization fails
        }
    }
}