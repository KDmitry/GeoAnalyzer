package edu.Dmitry.geodownloader;

import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class InternetAccess {
    private static Logger logger = LogManager.getRootLogger();
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).build();

    public static String downloadPage(String pageLink) throws HttpResponseException, UnknownHostException, SocketTimeoutException {
        logger.info("Download page : " + pageLink);
        StringBuilder result = new StringBuilder();

        HttpGet httpGet = new HttpGet(pageLink);
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
             CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {

            StatusLine statusLine = httpResponse.getStatusLine();
            logger.info("Status code = " + statusLine.getStatusCode());
            if (statusLine.getStatusCode() != 200) {
                logger.error(pageLink + " - code status: " + statusLine.getStatusCode());
                throw new HttpResponseException(statusLine.getStatusCode(), httpResponse.getStatusLine().toString());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent()));
            String inputLine;
            logger.info("Start reading page");
            while ((inputLine = reader.readLine()) != null) {
                result.append(inputLine);
            }
            logger.info("End of reading page");
            reader.close();
        } catch (SocketTimeoutException timeoutException) {
            logger.error(timeoutException.getMessage());
            throw timeoutException;
        } catch (HttpResponseException exeption) {
            logger.error(exeption.getMessage());
            throw exeption;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new UnknownHostException();
        }

        return result.toString();
    }
}
