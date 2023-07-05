package com.salemate.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.Hutool;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salemate.bo.MaterialListBo;
import com.salemate.bo.MaterialSaveBo;
import com.salemate.common.config.WxProperties;
import com.salemate.common.util.PageUtils;
import com.salemate.common.util.WxApiUtil;
import com.salemate.common.util.WxConstantKeys;
import com.salemate.model.Enterprise;
import com.salemate.model.MaterialHistory;
import com.salemate.model.MaterialImage;
import com.salemate.model.MaterialText;
import com.salemate.service.EnterpriseService;
import com.salemate.service.MaterialHistoryService;
import com.salemate.service.MaterialImageService;
import com.salemate.vo.MaterialItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/material")
@RestController
@Slf4j
public class MaterialController  {
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private MaterialImageService materialImageService;

    @Autowired
    private MaterialHistoryService materialHistoryService;

    /**
     * 上传素材到企业微信临时素材库
     * @return
     */
    @RequestMapping("/upload")
    public ResponseEntity upload(MultipartFile uploadFile) throws IOException {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        Enterprise enterprise = enterpriseService.getOne(new QueryWrapper<Enterprise>().eq("corp_id",corpId).eq("suite_id",WxApiUtil.getWxProperties().getSuiteId()));
        log.info("corpId:{}" , corpId);
        log.info("enterprise:{}" , enterprise);
        log.info("###getPermanentCode:{}" , enterprise.getPermanentCode());
        JSONObject jsonObject = WxApiUtil.uploadTemp(uploadFile.getBytes(),uploadFile.getOriginalFilename(),corpId, enterprise.getPermanentCode());
        return ResponseEntity.ok(jsonObject);
    }

    /**
     * 上传素材到本地
     * @return
     */
    @PostMapping("/updateLocal")
    public ResponseEntity updateLocal(MultipartFile file){
        String uuid = IdUtil.fastSimpleUUID();
        String fileName = file.getOriginalFilename();
        WxProperties wxProperties = WxApiUtil.getWxProperties();
        String upload = wxProperties.getUpload();
        MaterialImage materialImage  = new MaterialImage();
        //构建上传路径
        String path = upload + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "/" + uuid + "/" + fileName;
        try {
            FileUtil.writeBytes(file.getBytes(), path);
            materialImage.setPath(path);
            boolean save = materialImageService.save(materialImage);
            if (!save){
                throw new RuntimeException("上传失败");
            }
            //保存
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        }
        return ResponseEntity.ok(materialImage);
    }

    @GetMapping("/getImage")
    public void getImage(String imageId, HttpServletResponse response) throws IOException {
        MaterialImage materialImage = materialImageService.getById(imageId);
        if (materialImage == null){
            throw new RuntimeException("图片不存在");
        }
        try {
            //返回图片
            byte[] bytes = FileUtil.readBytes(materialImage.getPath());
            String fileName = FileUtil.getName(materialImage.getPath());
            String[] split = fileName.split("\\.");
            //判断文件类型
            if ("png".equalsIgnoreCase(split[1])){
                response.addHeader("Content-Type", "image/png");
            }else if ("jpg".equalsIgnoreCase(split[1])){
                response.addHeader("Content-Type", "image/jpg");
            }

            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("图片不存在");
        }finally {
            response.getOutputStream().close();
        }
    }

    /**
     * 查询素材库
     */
    @GetMapping("/getMaterialList")
    public ResponseEntity getMaterialList(@RequestBody MaterialListBo bo){

      List<MaterialText> queryPage = materialHistoryService.queryPage();
      return ResponseEntity.ok(queryPage);
    }

    /**
     * 查询素材库单组图片
     */
    @GetMapping("/getMaterialByGroupImage")
    public ResponseEntity getMaterialByGroupImage( String groupId){
        List<MaterialImage> queryPage = materialHistoryService.getMaterialByGroupImage(groupId);
        return ResponseEntity.ok(queryPage);
    }


    /**
     * 查询素材库单组文本
     */
    @GetMapping("/getMaterialByGroupText")
    public ResponseEntity getMaterialByGroupText(String groupId){
        MaterialText materialByGroupText = materialHistoryService.getMaterialByGroupText(groupId);
        return ResponseEntity.ok(materialByGroupText);
    }

    @GetMapping("/getMaterialByGroup")
    public ResponseEntity getMaterialByGroup( Integer labelType){
        log.info("labelType:{}",labelType);
        String groupId = materialHistoryService.getMaterialByGroup(labelType);
        return ResponseEntity.ok(groupId);
    }


    @GetMapping("/randomImage")
    public ResponseEntity randomImage(){
        MaterialImage materialImage = materialHistoryService.randomImage();
        return ResponseEntity.ok(materialImage);
    }

    @GetMapping("/aiImage")
    public ResponseEntity aiImage(){
        String[] keyWord = {"茶叶","茶杯","茶园"};
        //随机获取一个关键词
        int i = (int) (Math.random() * keyWord.length);
        String s = keyWord[i];
        HttpResponse response = HttpUtil.createPost("https://api-inference.modelscope.cn/api-inference/v1/models/damo/cv_diffusion_text-to-image-synthesis")
                .header("Authorization", "Bearer 5a39f006-1686-4b70-a903-c3c7f519561a")
                .body(JSONUtil.parseObj("{\"input\":\"" + s + "\"}").toString())
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        if (jsonObject.getInteger("Code") != 200) {
            throw new RuntimeException("获取图片失败");
        }
        String uuid = IdUtil.fastSimpleUUID();
        String url = jsonObject.getJSONObject("Data").getJSONArray("output_imgs").getString(0);
        byte[] bytes = HttpUtil.downloadBytes(url);


        WxProperties wxProperties = WxApiUtil.getWxProperties();
        String upload = wxProperties.getUpload();
        MaterialImage materialImage  = new MaterialImage();
        //构建上传路径
        String path = upload + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "/" + uuid + "/ai.jpg";
        try {
            FileUtil.writeBytes(bytes, path);
            materialImage.setPath(path);
            boolean save = materialImageService.save(materialImage);
            if (!save){
                throw new RuntimeException("上传失败");
            }
            //保存
        } catch (Exception e) {
            throw new RuntimeException("上传失败");
        }
        return ResponseEntity.ok(materialImage);

    }


    @PostMapping("/save")
    public ResponseEntity save(@RequestBody MaterialSaveBo bo){
        materialHistoryService.saveMaterial(bo);
        return ResponseEntity.ok("保存成功");
    }

    @PostMapping("/getList")
    public ResponseEntity getList(@RequestBody MaterialListBo bo) {
        Object loginId = StpUtil.getLoginId();
        Page<MaterialHistory> page = materialHistoryService.page(new Page(bo.getPageNum(),bo.getPageSize()),new QueryWrapper<MaterialHistory>()
                .eq("user_id", loginId)
                .eq("label_type", bo.getLabelType())
                .eq("type", WxConstantKeys.MaterialType.TEXT.getValue())
                .orderByDesc("create_date"));
        List<MaterialItemVo> voList = new ArrayList<>();
        for (MaterialHistory materialHistory : page.getRecords()) {
            //50-100随机数
            int cjl = RandomUtil.randomInt(5, 30);
            int hxl = RandomUtil.randomInt(50, 100);
            int fg = RandomUtil.randomInt(200, 300);

            MaterialText materialByGroupText = materialHistoryService.getMaterialByGroupText(materialHistory.getGroupId());
            List<MaterialImage> materialByGroupImage = materialHistoryService.getMaterialByGroupImage(materialHistory.getGroupId());
            List<String> idList = CollUtil.getFieldValues(materialByGroupImage, "id",String.class);
            MaterialItemVo materialItemVo = new MaterialItemVo();
            materialItemVo.setContent(materialByGroupText.getContent());
            materialItemVo.setLabelType(materialItemVo.getLabelType());
            materialItemVo.setSendType(materialHistory.getSendType());
            materialItemVo.setTextId(materialByGroupText.getId());
            materialItemVo.setImagesIds(idList);
            materialItemVo.setGroupId(materialHistory.getGroupId());
            materialItemVo.setCjl(cjl);
            materialItemVo.setHxl(hxl);
            materialItemVo.setFgrs(fg);
            voList.add(materialItemVo);
        }
        return ResponseEntity.ok(new PageUtils(voList,page.getTotal()));
    }






}
