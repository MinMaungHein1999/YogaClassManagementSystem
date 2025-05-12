package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeCreateDto;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeDTO;
import com.yogiBooking.common.dto.nrc_codes.NrcCodeUpdateDto;
import com.yogiBooking.common.entity.NrcCode;
import com.yogiBooking.common.exception.ResourceAlreadyExistsException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.NrcCodeMapper;
import com.yogiBooking.common.repository.NrcCodeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NrcCodeService {

    private final GenericSearchService searchService;
    private final NrcCodeRepository nrcCodeRepository;
    private final NrcCodeMapper nrcCodeMapper;
    private final EntityManager entityManager;

    @Transactional
    public NrcCodeDTO createNrcCode(NrcCodeCreateDto nrcCodeCreateDto) {
        NrcCode nrcCode = nrcCodeMapper.toEntity(nrcCodeCreateDto);
        if (nrcCodeRepository.findByNameMmAndNameEnAndPrefixCode(nrcCode.getNameMm(), nrcCode.getNameEn(), nrcCode.getPrefixCode()) != null) {
            throw new ResourceAlreadyExistsException("NrcCode with nameMM %s nameEN %s and prefixCode %d already exists"
                    .formatted(nrcCode.getNameMm(), nrcCode.getNameEn(), nrcCode.getPrefixCode()));
        }
        nrcCode = nrcCodeRepository.save(nrcCode);
        entityManager.refresh(nrcCode);
        return nrcCodeMapper.toDto(nrcCode);
    }

    @Transactional
    public NrcCodeDTO updateNrcCode(Long id, NrcCodeUpdateDto nrcCodeUpdateDto) {
        NrcCode nrcCode = nrcCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NrcCode with Id %d not found".formatted(id)));
        nrcCode.setNameMm(nrcCodeUpdateDto.getNameMm());
        nrcCode.setNameEn(nrcCodeUpdateDto.getNameEn());
        nrcCode.setPrefixCode(nrcCodeUpdateDto.getPrefixCode());
        nrcCode.setDetails(nrcCodeUpdateDto.getDetails());
        NrcCode updatedNrcCode = nrcCodeRepository.save(nrcCode);
        nrcCodeRepository.flush();
        entityManager.refresh(updatedNrcCode);
        return nrcCodeMapper.toDto(updatedNrcCode);
    }

    @Transactional
    public boolean deleteNrcCode(Long id) {
        NrcCode nrcCode = nrcCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NrcCode with ID %d not found".formatted(id)));
        nrcCodeRepository.delete(nrcCode);
        return true;
    }

    @Transactional
    public NrcCodeDTO getNrcCodeById(Long id) {
        NrcCode nrcCode = nrcCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NrcCode With Id %d not found".formatted(id)));
        return nrcCodeMapper.toDto(nrcCode);
    }

    @Transactional
    public List<NrcCodeDTO> getAllNrcCodes() {
        return nrcCodeRepository.findAll().stream()
                .map(nrcCodeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<NrcCodeDTO> findNrcCodesByFilter(SearchRequest request, Pageable pageable) {
        Page<NrcCode> nrcCodes = searchService.search(request, nrcCodeRepository, pageable);
        return nrcCodes.map(nrcCodeMapper::toDto);
    }
}
