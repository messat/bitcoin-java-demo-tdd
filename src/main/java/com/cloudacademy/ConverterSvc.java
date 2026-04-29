package com.cloudacademy.bitcoin;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.NumberFormat;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ConverterSvc {

    private final String BITCOIN_CURRENTPRICE_URL = "https://api.coinbase.com/v2/prices/BTC-%s/spot";
    //private final HttpGet httpGet = new HttpGet(BITCOIN_CURRENTPRICE_URL);

    private CloseableHttpClient httpClient;

    public ConverterSvc() {
        this.httpClient = HttpClients.createDefault();
    }

    public ConverterSvc(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public enum Currency {
        USD, GBP, EUR
    }

    public double getExchangeRate(Currency currency){
        double rate = 0;
        
        String url = String.format(BITCOIN_CURRENTPRICE_URL, currency);
        HttpGet httpGet = new HttpGet(url);
        
        try (CloseableHttpResponse response = this.httpClient.execute(httpGet)){
            //ClosableHttpResponse response = this.httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() != 200){
                rate = -1;
                return rate;
            }

            //CloseableHttpResponse response = this.httpClient.execute(httpGet);  

            InputStream inputStream = response.getEntity().getContent();
            var json = new BufferedReader(new InputStreamReader(inputStream));
            
            @SuppressWarnings("deprecation")
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            String n = jsonObject.get("data").getAsJsonObject().get("amount").getAsString();
            NumberFormat nf = NumberFormat.getInstance();
            rate = nf.parse(n).doubleValue();
            return rate;
        
        } catch (Exception e) {
            rate = -1;
            return rate;
        }
        // if(currency.equals("USD")){
        //     return 100;
        // } else if(currency.equals("GBP")){
        //     return 200;
        // } else if(currency.equals("EUR")){
        //     return 300;
        // }
        // return 0;
    }

    public double convertBitcoins(Currency currency, double coins){
        double dollars = 0;

        if(coins < 0){
            throw new IllegalArgumentException("Number of coins cannot be negative");
        }
        if(coins == 0){
            return dollars;
        }

        var exchangeRate = getExchangeRate(currency);

        if(exchangeRate > 0){
            dollars = coins * exchangeRate;
        } else {
            dollars = -1;
        }

        return dollars;
    }
}