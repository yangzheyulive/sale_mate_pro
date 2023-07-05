package com.mate.sale;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.script.ScriptUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.salemate.mapper.MaterialHistoryMapper;
import com.salemate.model.MaterialHistory;
import com.salemate.model.MaterialText;
import com.salemate.service.MaterialHistoryService;
import com.salemate.service.MaterialImageService;
import com.salemate.service.MaterialTextService;
import com.salemate.service.MessageService;
import com.salemate.common.util.WxConstantKeys;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@SpringBootTest
public class FileUploadTest {
    @Autowired
    MaterialTextService materialTextService;
    @Autowired
    MaterialImageService materialImageService;
    @Autowired
    MaterialHistoryService materialHistoryService;
    @Autowired
    MaterialHistoryMapper materialHistoryMapper;
    @Autowired
    private MessageService messageService;

    @Test
//    @Transactional(rollbackFor = Exception.class)
    public void test() throws IOException {
        //读取excel

        XSSFWorkbook sheets = new XSSFWorkbook("/Users/song/Downloads/先看文档/请点击.xlsx");

            XSSFSheet sheetAt1 = sheets.getSheetAt(1);
            //遍历每一行
            for (int j = 0; j < sheetAt1.getLastRowNum(); j++) {
                XSSFCell cell = sheetAt1.getRow(j).getCell(0);
                if (cell == null) {
                    continue;
                }
                String aText = cell.getStringCellValue();
                File file = new File("/Users/song/Downloads/先看文档/C新编/"+(j +1));
                List<File> fileList = new ArrayList<>();
                MaterialText materialText = new MaterialText();
                materialText.setContent(aText);
                System.out.println(aText);
                if (StrUtil.isBlank(aText)){
                    continue;
                }
//                System.out.println(aText);
                materialTextService.save(materialText);
//                if (file.isDirectory()) {
//                    //获取文件夹下的所有文件
//                    fileList = Arrays.asList(file.listFiles());
//
//                }else{
//                    File image = new File("/Users/song/Downloads/先看文档/C新编/"+(j+1)+".jpg");
//                    if (file.exists() && file.isFile()){
//                        fileList.add(image);
//                    }
//
//                }
//
                MaterialHistory materialHistory = new MaterialHistory();
                materialHistory.setGroupId(IdUtil.fastUUID());
                materialHistory.setTextId(materialText.getId());
                materialHistory.setType(WxConstantKeys.MaterialType.TEXT.getValue());
                materialHistory.setUserId("SongJiaYu");
                materialHistory.setCorpId("ww2ed1275aedf3d2ce");
                materialHistory.setLabelType(WxConstantKeys.LabelType.INTRODUCE.getValue());
                materialHistoryService.save(materialHistory);
//                for (File file1 : fileList) {
//                    String upload ="/usr/local/upload/";
//                    //构建上传路径
//                    String uuid = IdUtil.fastUUID();
//                    String fileName = file1.getName();
//                    String path = upload + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "/" + uuid + "/" + fileName;
//                    System.out.println(path);
//                    byte[] bytes = FileUtil.readBytes(file1);
////                    FileUtil.createTempFile(new File(path));
//                    FileUtil.writeBytes(bytes, "/Users/song/Downloads"+path);
//
//                    MaterialImage materialImage = new MaterialImage();
//                    materialImage.setPath(path);
//                    materialImageService.save(materialImage);
//                    MaterialHistory imageHis = new MaterialHistory();
//                    imageHis.setGroupId(materialHistory.getGroupId());
//                    imageHis.setImageId(materialImage.getId());
//                    imageHis.setUserId("SongJiaYu");
//                    imageHis.setCorpId("ww2ed1275aedf3d2ce");
//                    imageHis.setType(WxConstantKeys.MaterialType.IMAGE.getValue());
//                    imageHis.setLabelType(WxConstantKeys.LabelType.NO_Buy.getValue());
//                    materialHistoryService.save(imageHis);
//                }
            }
        }
        @Test
        public void test01(){
            System.out.println(messageService.getList("ww2ed1275aedf3d2ce", "wmdGQHEgAAnj4pacZYMo7AFl1kzbRxUQ"));
        }


    public static void main(String[] args) {
        Object session_hash = ScriptUtil.eval("Math.random().toString(36).substring(2);");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()

                .url("wss://stabilityai-stable-diffusion.hf.space/queue/join")
                .method("GET", null)
                .build();
//        final String[] imageBase64 = {""};
        WebSocket webSocket = client.newWebSocket(request , new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // WebSocket连接已成功打开
                log.info("连接成功");
                JSONObject init = new JSONObject();
                init.put("session_hash",session_hash);
                init.put("fn_index",3);
                webSocket.send(init.toJSONString());
                JSONObject imageMessage = new JSONObject();
                imageMessage.put("fn_index",3);
                imageMessage.put("session_hash",session_hash);
                JSONArray arr = new JSONArray();
                arr.add("person");
                arr.add("");
                arr.add(9);
                imageMessage.put("data", arr);
                log.info("发送消息:{} " , imageMessage.toJSONString());
                webSocket.send(imageMessage.toJSONString());
            }
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 接收到WebSocket服务器发送的文本消息
                log.info("服务器消息:{} " , text);
                boolean valid = JSONObject.isValid(text);
                if (valid){
                    JSONObject response = JSONObject.parseObject(text);
                    JSONObject output = response.getJSONObject("output");
                    if (output == null){
                        return;
                    }
                    JSONArray data = output.getJSONArray("data");
                    if (data != null && data.size() > 0){
                        String base64 = data.getJSONArray(0).getString(0);

                    }
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                // WebSocket连接即将关闭
                log.info("连接即将关闭");
                webSocket.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // WebSocket连接已关闭
                log.info("连接关闭");
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // WebSocket连接发生错误
                log.info("连接失败");
                webSocket.close(1000, null);
            }
        });
    }


}
