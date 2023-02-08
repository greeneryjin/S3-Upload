# S3-Upload
aws-s3와 spring을 연동해서 사진 업로드(저장, 조회, 수정, 삭제) 구현

front-end
- react

사용 언어
- JAVA 8


사용 기술
- spring-boot
- spring
- Mysql
- S3
- Querydsl
- spring data jpa
- spring jpa


라이브러리
- lombok
- gradle


핵심 코드 
-> S3에 업로드될 때, UUID를 사용해서 랜덤 값으로 저장해야 사진 이름에서 중복이 발생할 확률이 적어집니다.  
```java
    private String createStoreFileName(String originalFilename) {
        // 서버에 저장하는 파일 명
        String uuid = UUID.randomUUID().toString();
        // 사진 포맷 추출
        String ext = extractExt(originalFilename);
        String storeFileName = uuid + "." + ext;
        return storeFileName;
    }
```

핵심 코드
-> 클라이언트가 올린 파일을 변환해서 올려야 파일이 깨지는 것을 막을 수 있습니다. 
```java
    private Optional<File> convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        if(convertedFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertedFile);
        }

        return Optional.empty();
    }
```

핵심 코드
-> 사진을 저장하고 다시 수정할 때는 s3에 있던 사진을 지우고 새로 저장해야 s3 요금을 조금이라도 절약할 수 있습니다. 
```java
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
 ```
 
 사진 피드
 
 <img width="669" alt="ss" src="https://user-images.githubusercontent.com/87289562/217510145-e19e68a5-4e27-46cd-8396-c3b1fa4e630b.PNG">
 
 
 
 동영상 피드
 
 <img width="669" alt="aa" src="https://user-images.githubusercontent.com/87289562/217510252-fac40396-4b91-4393-bf52-14d156e5016b.PNG">
 
