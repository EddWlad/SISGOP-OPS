package com.tidsec.sisgop_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tidsec.sisgop_backend.dto.CompanyDTO;
import com.tidsec.sisgop_backend.entity.Company;
import com.tidsec.sisgop_backend.service.ICompanyService;
import com.tidsec.sisgop_backend.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final Cloudinary cloudinary;
    private final ICompanyService companyService;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> findAll() throws Exception  {
        List<CompanyDTO> list = mapperUtil.mapList(companyService.findAll(), CompanyDTO.class);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> findById(@PathVariable("id") String id) throws Exception  {
        CompanyDTO obj = mapperUtil.map(companyService.findById(UUID.fromString(id)), CompanyDTO.class);
        return ResponseEntity.ok(obj);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<CompanyDTO> save(@RequestBody CompanyDTO companyDTO) throws Exception  {

        Company obj = companyService.save(mapperUtil.map(companyDTO, Company.class));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(obj.getIdCompany()).toUri();

        return ResponseEntity.ok(mapperUtil.map(obj, CompanyDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(@PathVariable("id") String id, @RequestBody CompanyDTO companyDTO) throws Exception  {
        Company obj = companyService.update(mapperUtil.map(companyDTO, Company.class), UUID.fromString(id));
        return ResponseEntity.ok(mapperUtil.map(obj, CompanyDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception  {
        companyService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('upload')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> upload(@RequestParam("file") MultipartFile multipartFile) throws Exception {

        File f = convertToFile(multipartFile);
        Map<String, Object> response =  cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));
        JSONObject json = new JSONObject(response);
        String url = json.getString("url");

        System.out.println(url);

        return ResponseEntity.ok(Collections.singletonList(url));

    }

    private File convertToFile(MultipartFile multipartFile) throws Exception{
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(multipartFile.getBytes());
        outputStream.close();
        return file;
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<Company>> listPage(Pageable pageable) throws Exception {
        Page<Company> page = companyService.listPage(pageable);
        return ResponseEntity.ok(page);
    }
}
