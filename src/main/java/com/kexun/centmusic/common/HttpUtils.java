package com.kexun.centmusic.common;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpUtils {


//    private static final String ENCODING = "utf-8";

    public static String doGet(String url, Map<String, String> param, String encoding) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), encoding == null ? "utf-8" : encoding);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null, null);
    }

    public static String doGet(String url, String encoding) {
        return doGet(url, null, encoding);
    }

    public static String doPost(String url, Map<String, String> param, String encoding) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, encoding);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null, "utf-8");
    }

    public static String doPostJson(String url, String json) {
        return doPostJson(url, null, json, "utf-8");
    }

    /**
     * 请求的参数类型为json
     *
     * @param url
     * @param json
     * @return {username:"",pass:""}
     */
    public static String doPostJson(String url, Map<String, String> headerMap, String json, String encoding) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            if (headerMap != null) {
                Iterator headerIterator = headerMap.entrySet().iterator();          //循环增加header
                while (headerIterator.hasNext()) {
                    Map.Entry<String, String> elem = (Map.Entry<String, String>) headerIterator.next();
                    httpPost.addHeader(elem.getKey(), elem.getValue());
                }
            }

            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * 请求的参数类型为xml,支持SOAP1.2
     *
     * @param url
     * @param xml
     * @return {username:"",pass:""}
     */
    public static String doPostXml(String url, String xml, String soapname, String encoding) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 设置SOAPAction，支持soap1.2
            httpPost.setHeader("SOAPAction", soapname);
            // 创建请求内容
            StringEntity entity = new StringEntity(xml, ContentType.TEXT_XML);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * 请求的参数类型为xml
     *
     * @param url
     * @param xml
     * @return {username:"",pass:""}
     */
    public static String doPostXml(String url, String xml, String encoding) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(xml, ContentType.TEXT_XML);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doPostWebservice(String url, String xml, String encoding) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            StringEntity data = new StringEntity(xml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), encoding);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://www.webxml.com.cn/webservices/qqOnlineWebService.asmx?wsdl";
        String xml = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:web=\"http://WebXml.com.cn/\">\r\n" +
                "   <soap:Header/>\r\n" +
                "   <soap:Body>\r\n" +
                "      <web:qqCheckOnline>\r\n" +
                "         <!--Optional:-->\r\n" +
                "         <web:qqCode>870053231</web:qqCode>\r\n" +
                "      </web:qqCheckOnline>\r\n" +
                "   </soap:Body>\r\n" +
                "</soap:Envelope>";
//        String rString = HttpUtils.doPostWebservice(url, xml);
//        System.out.println(rString);
    }

}
