package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.entity.Material;
import com.tidsec.sisgop_backend.entity.MaterialSupplier;
import com.tidsec.sisgop_backend.entity.Supplier;
import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.repository.IMaterialRepository;
import com.tidsec.sisgop_backend.repository.IMaterialSupplierRepository;
import com.tidsec.sisgop_backend.repository.ISupplierRepository;
import com.tidsec.sisgop_backend.service.IMaterialSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialSupplierServiceImpl  extends GenericServiceImpl<MaterialSupplier, UUID>
        implements IMaterialSupplierService {

    private final IMaterialSupplierRepository materialSupplierRepository;
    private final IMaterialRepository materialRepository;
    private final ISupplierRepository supplierRepository;

    @Override
    protected IGenericRepository<MaterialSupplier, UUID> getRepo() {
        return materialSupplierRepository;
    }

    @Override
    public List<MaterialSupplier> findByMaterial(UUID idMaterial) throws Exception {
        return materialSupplierRepository.findByMaterial_IdMaterialAndStatusNot(idMaterial, 0);
    }

    @Override
    public List<MaterialSupplier> findBySupplier(UUID idSupplier) throws Exception {
        return materialSupplierRepository.findBySupplier_IdSupplierAndStatusNot(idSupplier, 0);
    }

    @Override
    @Transactional
    public MaterialSupplier upsert(MaterialSupplier entity) throws Exception {
        MaterialSupplier saved = upsertCore(entity);
        return saved;
    }

    @Override
    @Transactional
    public List<MaterialSupplier> upsertBatch(List<MaterialSupplier> entities) throws Exception {
        List<MaterialSupplier> out = new ArrayList<>();
        for (MaterialSupplier e : entities) {
            out.add(upsertCore(e));
        }
        return out;
    }

    @Override
    public Page<MaterialSupplier> listPage(Pageable pageable) {
        return materialSupplierRepository.findAll(pageable);
    }

    private MaterialSupplier upsertCore(MaterialSupplier incoming) throws Exception {
        if (incoming == null || incoming.getMaterial() == null || incoming.getSupplier() == null) {
            throw new IllegalArgumentException("Se requieren material y supplier en la entidad MaterialSupplier.");
        }
        UUID idMaterial = incoming.getMaterial().getIdMaterial();
        UUID idSupplier = incoming.getSupplier().getIdSupplier();

        if (idMaterial == null) throw new IllegalArgumentException("idMaterial es requerido.");
        if (idSupplier == null) throw new IllegalArgumentException("idSupplier es requerido.");

        Material material = materialRepository.findById(idMaterial)
                .orElseThrow(() -> new ModelNotFoundException("Material no existe: " + idMaterial));
        Supplier supplier = supplierRepository.findById(idSupplier)
                .orElseThrow(() -> new ModelNotFoundException("Supplier no existe: " + idSupplier));

        MaterialSupplier entity = materialSupplierRepository
                .findByMaterial_IdMaterialAndSupplier_IdSupplier(idMaterial, idSupplier)
                .orElseGet(MaterialSupplier::new);

        entity.setMaterial(material);
        entity.setSupplier(supplier);
        entity.setPrice(incoming.getPrice());
        entity.setLeadTimeDays(incoming.getLeadTimeDays());
        entity.setPreferred(incoming.isPreferred());
        entity.setStatus(incoming.getStatus() != null ? incoming.getStatus() : 1);

        entity = materialSupplierRepository.save(entity);

        if (entity.isPreferred()) {
            materialSupplierRepository.unsetOthersPreferred(idMaterial, entity.getIdMaterialSupplier());
        }

        return entity;
    }
}
