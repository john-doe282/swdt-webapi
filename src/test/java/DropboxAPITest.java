import io.restassured.RestAssured;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import org.apache.commons.io.FileUtils;

import java.io.*;

import java.net.URL;

class FileDownloader {
    public static void downloadImage(String imageAddress, String imageLocalPath) throws IOException {
        URL url = new URL(imageAddress);

        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] res = out.toByteArray();
        FileOutputStream fos = new FileOutputStream(imageLocalPath);
        fos.write(res);
        fos.close();
    }
}

public class DropboxAPITest {

    private final String token = "2OUrECyC26wAAAAAAAAAAX2Tb-GwQ-j-sgerWM4uzvUsked-OwICFj4gWDA1eFeX";
    private final String imageLocalPath = "cat.jpg";

    private final String uploadURL = "https://content.dropboxapi.com/2/files/upload";
    private final String getMetadataURL = "https://api.dropboxapi.com/2/files/get_metadata";
    private final String deleteURL = "https://api.dropboxapi.com/2/files/delete_v2";

    @Test
    public void a_testUpload() throws IOException {

        FileDownloader.downloadImage("https://cdn.designsmaz" +
                ".com/wp-content/uploads/2016/03/" +
                "Cat-with-Sunglasses-Background.jpg", imageLocalPath);

        JSONObject apiArg = new JSONObject();
        apiArg.put("mode","add");
        apiArg.put("autorename", true);
        apiArg.put("path", "/test/pic.jpg");

        File file = new File(imageLocalPath);


        RestAssured.given()
                .headers("Dropbox-API-Arg", apiArg.toJSONString(),
                        "Content-Type","text/plain; " +
                                "charset=dropbox-cors-hack",
                        "Authorization", "Bearer " + token)
                .body(FileUtils.readFileToByteArray(file))
                .when().post(uploadURL)
                .then().statusCode(200);

        file.delete();
    }

    @Test
    public void b_testGetMetadata(){
        JSONObject requestParam = new JSONObject();
        requestParam.put("path","/test/pic.jpg");
        requestParam.put("include_media_info",true);

        RestAssured.given()
                .headers("Authorization", "Bearer " + token,
                        "Content-Type","application/json")
                .body(requestParam.toJSONString())
                .when().post(getMetadataURL)
                .then().statusCode(200);
    }

    @Test
    public void c_testDelete(){
        JSONObject requestParam = new JSONObject();
        requestParam.put("path","/test/pic.jpg");

        RestAssured.given()
                .headers("Authorization", "Bearer " + token,
                        "Content-Type","application/json")
                .body(requestParam.toJSONString())
                .when().post(deleteURL)
                .then().statusCode(200);

    }

}