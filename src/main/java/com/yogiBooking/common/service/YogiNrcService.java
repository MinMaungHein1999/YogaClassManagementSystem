package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yogi_nrc.MasterYogiNrcDTO;
import com.yogiBooking.common.entity.*;
import com.yogiBooking.common.exception.ResourceAlreadyExistsException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.YogiNrcMapper;
import com.yogiBooking.common.repository.NrcCodeRepository;
import com.yogiBooking.common.repository.YogiNrcRepository;
import com.yogiBooking.common.repository.YogiRepository;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcDTO;
import com.yogiBooking.common.entity.NrcCode;
import com.yogiBooking.common.entity.Yogi;
import com.yogiBooking.common.entity.YogiNrc;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YogiNrcService {

    private final NrcCodeRepository nrcCodeRepository;
    private final YogiRepository yogiRepository;
    private final YogiNrcRepository yogiNrcRepository;
    private final GenericSearchService searchService;
    private final YogiNrcMapper yogiNrcMapper;
    private final EntityManager entityManager;

    @Transactional
    public List<YogiNrcDTO> getAll() {
        return yogiNrcRepository.findAll().stream()
                .map(yogiNrcMapper::toDto)
                .toList();
    }

    @Transactional
    public Optional<YogiNrcDTO> getYogiNrcByYogiId(long yogiId) {
        Optional<List<YogiNrc>> yogiNrcListOpt = yogiNrcRepository.findYogiNrcByYogiId(yogiId);

        if (yogiNrcListOpt.isPresent() && !yogiNrcListOpt.get().isEmpty()) {
            YogiNrc yogiNrc = yogiNrcListOpt.get().get(0);
            YogiNrcDTO dto = yogiNrcMapper.toDto(yogiNrc);
            dto.setNrc(yogiNrc.getMmNrc());
            dto.setNrcEn(yogiNrc.getEnNrc());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Transactional
    public YogiNrcDTO getById(long id) {
        YogiNrc yogiNrc = yogiNrcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("YogiNrc with ID %d not found.".formatted(id)));
        return yogiNrcMapper.toDto(yogiNrc);
    }

    @Transactional
    public YogiNrcDTO create(MasterYogiNrcDTO masterYogiNrcDTO) {
        Long nrcCodeId = masterYogiNrcDTO.getNrcCodeId();
        Long yogiId = masterYogiNrcDTO.getYogiId();

        // Validate inputs early to prevent NullPointerException
        if (nrcCodeId == null) {
            throw new IllegalArgumentException("NRC Code ID must not be null.");
        }

        if (yogiId == null) {
            throw new IllegalArgumentException("Yogi ID must not be null.");
        }

        // Ensure both referenced entities exist
        nrcCodeRepository.findById(nrcCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("NRC Code not found with ID: " + nrcCodeId));

        yogiRepository.findById(yogiId)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi not found with ID: " + yogiId));

        // Check for duplicates
        checkDuplicateNrc(masterYogiNrcDTO);

        // Map, save and refresh
        YogiNrc yogiNrc = yogiNrcMapper.toEntity(masterYogiNrcDTO);
        yogiNrc = yogiNrcRepository.save(yogiNrc);
        entityManager.refresh(yogiNrc);

        // Prepare DTO
        YogiNrcDTO dto = yogiNrcMapper.toDto(yogiNrc);
        dto.setNrc(yogiNrc.getMmNrc());

        return dto;
    }


    @Transactional
    public YogiNrcDTO update(Long id, MasterYogiNrcDTO masterYogiNrcDTO) {
        YogiNrc yogiNrc = yogiNrcRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yogi NRC not found with ID: " + id));
        NrcCode nrcCode = nrcCodeRepository.findById(masterYogiNrcDTO.getNrcCodeId())
                .orElseThrow(() -> new ResourceNotFoundException("NRC Code not found with ID: " + masterYogiNrcDTO.getNrcCodeId()));
        Yogi yogi = yogiRepository.findById(masterYogiNrcDTO.getYogiId())
                .orElseThrow(() -> new ResourceNotFoundException("Yogi not found with ID: " + masterYogiNrcDTO.getYogiId()));

        checkDuplicateNrc(masterYogiNrcDTO);

        yogiNrc.setPostFixDigit(masterYogiNrcDTO.getPostFixDigit());
        yogiNrc.setType(masterYogiNrcDTO.getType());
        yogiNrc.setNrcCode(nrcCode);
        yogiNrc.setYogi(yogi);

        YogiNrc updatedYogiNrc = yogiNrcRepository.save(yogiNrc);
        yogiNrcRepository.flush();
        entityManager.refresh(updatedYogiNrc);
        return yogiNrcMapper.toDto(updatedYogiNrc);
    }

    private void checkDuplicateNrc(MasterYogiNrcDTO masterYogiNrcDTO) {
      boolean exist  = yogiNrcRepository.findYogiNrcByNRCNotID(masterYogiNrcDTO.getPostFixDigit(), masterYogiNrcDTO.getType(), masterYogiNrcDTO.getNrcCodeId(), masterYogiNrcDTO.getYogiId());
      if(exist){
          throw new ResourceAlreadyExistsException("Yogi NRC already exists for the given details.");
      }
    }

    @Transactional
    public boolean delete(Long id) {
        YogiNrc yogiNrc = yogiNrcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Yogi NRC not found with ID: " + id));
        yogiNrcRepository.delete(yogiNrc);
        return true;
    }

    @Transactional
    public Page<YogiNrcDTO> findYogiNrcByFilter(SearchRequest request, Pageable pageable) {
        Page<YogiNrc> yogiNrcs = searchService.search(request, yogiNrcRepository, pageable);
        return yogiNrcs.map(yogiNrcMapper::toDto);
    }
}
