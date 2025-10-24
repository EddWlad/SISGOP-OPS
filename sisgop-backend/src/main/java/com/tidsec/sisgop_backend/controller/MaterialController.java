package com.tidsec.sisgop_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tidsec.sisgop_backend.dto.MaterialDTO;
import com.tidsec.sisgop_backend.entity.Material;
import com.tidsec.sisgop_backend.service.IMaterialService;
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
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialController {
    private final Cloudinary cloudinary;
    private final IMaterialService materialService;
    private final MapperUtil mapperUtil;

    @PreAuthorize("@authorizeLogic.hasAccess('findAll')")
    @GetMapping
    public ResponseEntity<List<MaterialDTO>> findAll() throws Exception {
        List<MaterialDTO> list = mapperUtil.mapList(materialService.findAll(), MaterialDTO.class);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('findById')")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> findById(@PathVariable("id") String id) throws Exception {
        MaterialDTO obj = mapperUtil.map(materialService.findById(UUID.fromString(id)), MaterialDTO.class);
        return ResponseEntity.ok(obj);
    }

    @PreAuthorize("@authorizeLogic.hasAccess('save')")
    @PostMapping
    public ResponseEntity<MaterialDTO> save(@RequestBody MaterialDTO materialDTO) throws Exception {

        Material obj = materialService.save(mapperUtil.map(materialDTO, Material.class));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(obj.getIdMaterial()).toUri();

        return ResponseEntity.ok(mapperUtil.map(obj, MaterialDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('update')")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialDTO> update(@PathVariable("id") String id, @RequestBody MaterialDTO materialDTO) throws Exception {
        Material obj = materialService.update(mapperUtil.map(materialDTO, Material.class), UUID.fromString(id));
        return ResponseEntity.ok(mapperUtil.map(obj, MaterialDTO.class));
    }

    @PreAuthorize("@authorizeLogic.hasAccess('delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception {
        materialService.delete(UUID.fromString(id));
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

    private File convertToFile(MultipartFile multipartFile) throws Exception {
        // 1) Construir un sufijo correcto para el temp (extensión del archivo o .tmp)
        String original = multipartFile.getOriginalFilename();
        String suffix = ".tmp";
        if (original != null && original.contains(".")) {
            suffix = original.substring(original.lastIndexOf("."));
            if (suffix.length() > 10) { // edge case raro de nombres sin extensión "normales"
                suffix = ".tmp";
            }
        }

        // 2) Crear archivo en directorio temporal del sistema (NO en el proyecto)
        Path temp = Files.createTempFile("sisgop_", suffix);

        // 3) Escribir bytes (sin dejarlo abierto)
        try (OutputStream os = Files.newOutputStream(
                temp, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(multipartFile.getBytes());
        }

        File file = temp.toFile();

        // 4) Marcar para eliminar al cerrar la JVM (fallback)
        file.deleteOnExit();

        // 5) Programar una limpieza proactiva (daemon) por si nadie lo borra antes
        scheduleTempDeletion(file, Duration.ofMinutes(10));
        // puedes bajar a 2–5 minutos si quieres más agresivo

        return file;
    }

    private void scheduleTempDeletion(File file, Duration delay) {
        Thread cleaner = new Thread(() -> {
            try {
                Thread.sleep(delay.toMillis());
                try {
                    // intenta borrar; si está en uso, reintenta un par de veces
                    for (int i = 0; i < 3 && file.exists(); i++) {
                        if (!file.delete()) {
                            Thread.sleep(1500);
                        } else {
                            break;
                        }
                    }
                } catch (InterruptedException ignored) {}
            } catch (InterruptedException ignored) {
                // Si interrumpen el sleep, igual intentamos borrar una vez
                if (file.exists()) file.delete();
            }
        }, "sisgop-temp-file-cleaner");
        cleaner.setDaemon(true); // no bloquea el cierre de la app
        cleaner.start();
    }

    @PreAuthorize("@authorizeLogic.hasAccess('pageable')")
    @GetMapping("/pageable")
    public ResponseEntity<Page<Material>> listPage(Pageable pageable) throws Exception {
        Page<Material> page = materialService.listPage(pageable);
        return ResponseEntity.ok(page);
    }
}
