package com.example.demo.plantDic.controller;

import com.example.demo.dto.Header;
import com.example.demo.plantDic.dto.request.*;
import com.example.demo.plantDic.dto.response.*;
import com.example.demo.plantDic.model.*;
import com.example.demo.plantDic.service.MyGardenAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Slf4j
public class OpenApiAdmin {/* 관리자 API */

    @Autowired
    MyGardenAdminService mygardenAdminService;

    /**관리자 API**/
    //저장
    @PostMapping("garden/admin/save")
    public Header<Long> PlantSave(@RequestPart(value="plantSaveDto", required = false) PlantSaveDto plantSaveDto,
                                  @RequestPart(value="file", required = false) MultipartFile file) {
        if(plantSaveDto == null){
            return Header.ERROR("다시 보내주세요");
        }
        MyGardenDtl myGardenDtl = mygardenAdminService.save(plantSaveDto, file);
        return Header.OK(myGardenDtl.getId());
    }

    //전체 조회
    @GetMapping("/garden/admin/search")
    public Header<List<PlantResponse>> PlantSearch(@PageableDefault(size = 12) Pageable pageable){
        List<MyGardenDtl> myGardenDtl = mygardenAdminService.searchAdmin(pageable);
        List<PlantResponse> plantResponses = myGardenDtl.stream().map(m -> new PlantResponse(m))
                .collect(Collectors.toList());
        return Header.OK(plantResponses);
    }

    // id 조회
    @GetMapping("/garden/admin/find/{id}")
    public Header<List<PlantResponse>> findPlant(@PathVariable(value = "id") Long id){
        List<MyGardenDtl> myGardenDtl = mygardenAdminService.findById(id);
        if(myGardenDtl.size() == 0){
            log.info("Not found id {} ", id);
            return Header.ERROR("Not found");
        }
        List<PlantResponse> plantResponses = myGardenDtl.stream().map(m -> new PlantResponse(m))
                .collect(Collectors.toList());
        return Header.OK(plantResponses);
    }

    //수정
    @PutMapping("garden/admin/update/{id}")
    public Header<Long> PlantUpdate(@RequestPart(value="id", required = false) Long id,
                                    @RequestPart(value="plantChangeDto", required = false) PlantChangeDto plantChangeDto,
                                    @RequestPart(value="file", required = false) MultipartFile file) throws IOException {
        MyGardenDtl myGardenDtl = mygardenAdminService.update(id, plantChangeDto, file);
        return Header.OK(myGardenDtl.getId());
    }

    //삭제
    @DeleteMapping("garden/admin/delete/{id}")
    public Header<Long> PlantDelete(@PathVariable("id")Long id) {
        Long entityId = mygardenAdminService.delete(id);
        return Header.OK(entityId);
    }

    //해충 저장
    @PostMapping("garden/admin/pest/save/{id}")
    public Header pestSave(@PathVariable("id")Long id,@RequestBody PestSaveDto pestSaveDto){
        mygardenAdminService.pestSave(id, pestSaveDto);
        return Header.OK();
    }

    //해충 수정
    @PutMapping("garden/admin/pest/update/{id}")
    public Header<PestDto> pestUpdate(@PathVariable("id")Long id, @RequestBody PestSaveDto pestSaveDto){
        Pest pest = mygardenAdminService.pestUpdate(id, pestSaveDto);
        PestDto pestDto = PestDto.of(pest);
        return Header.OK(pestDto);
    }

    //식물 & 해충 조회
    @GetMapping("garden/admin/pest/search")
    public Header<List<PestResponse>> pestSearch(@RequestParam(value = "name") String name) {
        List<MyGardenDtl> myGardenDtls = mygardenAdminService.plantSearch(name);
        List<PestResponse> pestResponses = myGardenDtls
                .stream().map(m -> new PestResponse(m)).collect(Collectors.toList());
        return Header.OK(pestResponses);
    }

    //해충 조회
    @GetMapping("garden/admin/pest/searchOne")
    public Header<List<PestDto>> pestOneSearch(@RequestParam(value = "pestCodeNm") String pestCodeNm) {
        List<Pest> pest = mygardenAdminService.pestNameSearch(pestCodeNm);
        List<PestDto> pestDto = pest.stream().map(p -> new PestDto(p)).collect(Collectors.toList());
        return Header.OK(pestDto);
    }

    //해충 삭제
    @DeleteMapping("garden/admin/pest/delete/{id}")
    public Header pestDelete(@PathVariable("id")Long id){
        mygardenAdminService.pestDelete(id);
        return Header.OK();
    }
    /** 관리자 API **/
}
