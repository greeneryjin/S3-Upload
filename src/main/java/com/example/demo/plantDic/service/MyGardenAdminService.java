package com.example.demo.plantDic.service;

import com.example.demo.plantDic.dto.request.*;
import com.example.demo.plantDic.model.*;
import com.example.demo.plantDic.repository.*;
import com.example.demo.plantDic.s3.dto.ProfileDto;
import com.example.demo.plantDic.s3.service.AwsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyGardenAdminService {/* 관리자 API service */

    private final AwsService awsService;
    private final MyGardenDtlRepository myGardenDtlRepository;
    private final MyGardenDtlPictureRepository myGardenDtlPictureRepository;
    private final PestRepository pestRepository;

    /**관리자 API**/
    // MyGardenDtl id 조회
    public MyGardenDtl findId(Long id) {
        MyGardenDtl M = myGardenDtlRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 Id가 없습니다."));
        return M;
    }

    public MyGardenDtl save(PlantSaveDto plantSaveDto, MultipartFile file) throws IOException {
        ProfileDto pic = awsService.uploadOneProfile(file, "/picture");
        MyGardenDtl myGardenDtl = new MyGardenDtl();
        myGardenDtl.saveData(plantSaveDto);
        myGardenDtl.pic(pic.getFileName());
        MyGardenDtl mygardendtl = myGardenDtlRepository.save(myGardenDtl);
        return mygardendtl;
    }

    //전체 조회
    @Transactional(readOnly = true)
    public List<MyGardenDtl> searchAdmin(Pageable pageable) {
        List<MyGardenDtl> myGardenDtl = myGardenDtlRepository.findMyGardenDtlByAll(pageable);
        return myGardenDtl;
    }

    //단 건 조회
    @Transactional(readOnly = true)
    public List<MyGardenDtl> findById(Long id){
        List<MyGardenDtl> myGardenDtl = myGardenDtlRepository.findByGetId(id);
        return myGardenDtl;
    }

    // 수정
    @Transactional
    public MyGardenDtl update(Long id, PlantChangeDto plantChangeDto, MultipartFile file) throws IOException {
        ProfileDto pic = awsService.uploadOneProfile(file, "/picture");
        MyGardenDtl myGardenDtl = findId(id);

        //기존 컨텐츠 삭제(S3에서 삭제됨)
        awsService.deleteProfile("/picture", myGardenDtl.getPic());
        myGardenDtl.changeData(plantChangeDto);
        myGardenDtl.pic(pic.getFileName());
        return myGardenDtl;
    }

    //삭제
    public Long delete(Long id) {
        MyGardenDtl myGardenDtl = findId(id);
        myGardenDtlRepository.deleteById(myGardenDtl.getId());
        return myGardenDtl.getId();
    }

    //해충 정보 저장
    public void pestSave(Long id, PestSaveDto pestSaveDto) {
        MyGardenDtl myGardenDtl = findId(id);
        String pestName = pestSaveDto.getPestName();
        String[] arr = pestName.split(",");

        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equals("응애")) {
                Pest pest = new Pest();
                pest.setPestCodeNm(arr[i]);
                pest.savePestOne(pestSaveDto);
                myGardenDtl.addPest(pest);
                pestRepository.save(pest);
            } else if(arr[i].equals("깍지벌레(개각충)")) {
                Pest pest = new Pest();
                pest.setPestCodeNm(arr[i]);
                pest.savePestTwo(pestSaveDto);
                myGardenDtl.addPest(pest);
                pestRepository.save(pest);
            }  else if(arr[i].equals("온실가루이")) {
                Pest pest = new Pest();
                pest.setPestCodeNm(arr[i]);
                pest.savePestThree(pestSaveDto);
                myGardenDtl.addPest(pest);
                pestRepository.save(pest);
            }  else if(arr[i].equals("진딧물")) {
                Pest pest = new Pest();
                pest.setPestCodeNm(arr[i]);
                pest.savePestFour(pestSaveDto);
                myGardenDtl.addPest(pest);
                pestRepository.save(pest);
            }   else {
                Pest pest = new Pest();
                pest.setPestCodeNm(arr[i]);
                pest.savePestFive(pestSaveDto);
                myGardenDtl.addPest(pest);
                pestRepository.save(pest);
            }
        }
    }
    
    //해충 정보 수정
    @Transactional
    public Pest pestUpdate(Long id, PestSaveDto pestSaveDto) {
        Pest pest = pestFindId(id);
        pest.update(pestSaveDto);
        return pest;
    }

    //해충 정보 조회(식물&벌레)
    public List<MyGardenDtl> plantSearch(String name) {
        List<MyGardenDtl> myGardenDtls = myGardenDtlRepository.findByName(name);
        return myGardenDtls;
    }

    //해충 정보 조회
    public List<Pest> pestNameSearch(String pestCodeNm) {
        List<Pest> pest = pestRepository.findByName(pestCodeNm);
        return pest;
    }

    //해충 정보 삭제
    public void pestDelete(Long id) {
        pestRepository.deleteById(id);
    }

    public Pest pestFindId(Long id){
        Pest pest = pestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 Id가 없습니다."));
        return pest;
    }
    /**관리자 API**/
}
