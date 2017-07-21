package ru.croc.chromenative.service.hostmethod.scan.uploader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.croc.chromenative.service.hostmethod.scan.ScanApp;
import ru.sggr.scan.ImageUtils;
import ru.sggr.scan.uploader.UploadResponse;
import ru.sggr.scan.uploader.UploadResponseData;

/**
 * Класс загрузки отсканированного файла на сервер
 * @since  26.01.12 Time: 12:34
 */
public class DirectUploader {

    public static final String CHARACTER_ENCODING = "UTF-8";

    public static final int MILLS_IN_SEC = 1000;

    HttpPost post = null;

    private Gson gson = new Gson();

    private long start = 0;

    private URL documentBase = ScanApp.getDocBase();

    private URL uploadURL = null;

    private HttpClient client = null;

    private MultipartEntity entity;

    public DirectUploader() {
    }

    private String genFilename() {
        return UUID.randomUUID().toString() + ".JPG";
    }

    public UploadResponse upload(final Image image, final boolean isEnd, final int pageNumber, final String modelId)
            throws IOException, URISyntaxException, PrivilegedActionException {

        if (image == null) {
            UploadResponseData data = new UploadResponseData();
            data.setMsg("Нет отсканированного документа! Cохранение невозможно!");
            UploadResponse response = new UploadResponse();
            response.setData(data);
            response.setSuccess(false);
            return response;
        }

        if (start == 0) {
            start = System.currentTimeMillis();
            ScanApp.getApp().updateStatus("Сканирование и загрузка файлов на сервер...");
            uploadURL = new URL(documentBase, ScanApp.getUploadUrl());
            post = new HttpPost(uploadURL.toURI());
            entity = new MultipartEntity();
        }

        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        ImageIO.write(bufferedImage, "JPEG", imageOutputStream);
        imageOutputStream.flush();
        entity.addPart(pageNumber + "_" + genFilename(),
                new ByteArrayBody(imageOutputStream.toByteArray(), pageNumber + "_" + genFilename()));
        imageOutputStream.close();

        ScanApp.getApp().updateStatus("Сканирование и загрузка на сервер страницы: " + pageNumber);

        if (isEnd || pageNumber % ScanApp.getUploadbatchsize() == 0) {
            entity.addPart("user-name", new StringBody(ScanApp.getUserName()));
            entity.addPart("model-class", new StringBody(ScanApp.getModelClass()));
            entity.addPart("uploaded-from-scan-applet", new StringBody("true"));

            if (modelId != null) {
                entity.addPart("modelid", new StringBody(modelId));
            }

            post.setEntity(entity);
            client = new DefaultHttpClient();
            final HttpResponse response = client.execute(post);
            entity = new MultipartEntity();
            ScanApp.getApp().updateStatus("Загрузка завершена. " + ((System.currentTimeMillis() - start)
                    / MILLS_IN_SEC) + " сек");

            // Рефлешон использовать нельзя, потому регистрируем адаптер для типа
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(UploadResponse.class, new JsonDeserializer<UploadResponse>() {

                public UploadResponse deserialize(final JsonElement jsonElement,
                                                  final Type type,
                                                  final JsonDeserializationContext context) throws JsonParseException {
                    UploadResponse res = new UploadResponse();

                    JsonObject root = jsonElement.getAsJsonObject();
                    res.setSuccess(root.get("success").getAsBoolean());
                    JsonObject data = root.get("data").getAsJsonObject();

                    UploadResponseData resData = new UploadResponseData();
                    resData.setMsg(data.get("msg").getAsString());
                    String[] modelIds = context.deserialize(data.get("modelids"), String[].class);
                    resData.setModelids(Arrays.asList(modelIds));
                    resData.setMode("uploaddirect");
                    res.setData(resData);
                    return res;
                }
            });
            gson = builder.create();

            return gson.fromJson(
                    new JsonReader(new InputStreamReader(response.getEntity().getContent(), CHARACTER_ENCODING)),
                    UploadResponse.class);
        }

        return null;
    }

}
