package com.salemate.controller;

import com.salemate.service.TagService;
import com.salemate.vo.TagListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/tag")
@Component
public class TagController {
    @Autowired
    private TagService tagService;
    @GetMapping("/list")
    public ResponseEntity list() {
       List<TagListVo> listVos =  tagService.listByVo();
        return ResponseEntity.ok(listVos);
    }
}
