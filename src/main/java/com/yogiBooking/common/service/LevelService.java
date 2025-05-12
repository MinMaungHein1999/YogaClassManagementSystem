package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.level.LevelCreateDTO;
import com.yogiBooking.common.dto.level.LevelDTO;
import com.yogiBooking.common.dto.level.LevelUpdateDTO;
import com.yogiBooking.common.entity.Level;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.LevelMapper;
import com.yogiBooking.common.repository.LevelRepository;
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
public class LevelService {

    private final LevelMapper levelMapper;
    private final LevelRepository levelRepository;
    private final GenericSearchService searchService;
    private final EntityManager entityManager;

    @Transactional
    public LevelDTO createLevel(LevelCreateDTO levelCreateDTO){
        Level level = levelMapper.toEntity(levelCreateDTO);
        level = levelRepository.save(level);
        entityManager.refresh(level);
        return levelMapper.toDTO(level);
    }

    @Transactional
    public LevelDTO updateLevel(Long id,LevelUpdateDTO levelUpdateDTO){
        Level level = levelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Level with " + id + " Not found!!"));
        level.setName(levelUpdateDTO.getName());
        level = levelRepository.save(level);
        levelRepository.flush();
        entityManager.refresh(level);
        return levelMapper.toDTO(level);
    }

    @Transactional
    public void deleteLevel(Long id){
        if(!levelRepository.existsById(id)){
            throw new ResourceNotFoundException("Level with id "+ id +" not found");
        }
        levelRepository.deleteById(id);
    }

    public LevelDTO findLevelById(Long id){
        return levelMapper.toDTO( levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level with this id Not found !!")));
    }

    public List<LevelDTO> getAllLevels(){
        return levelRepository.findAll().stream().map(levelMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public Page<LevelDTO> findLevelByFilter(SearchRequest request, Pageable pageable) {
        Page<Level> levels = searchService.search(request, levelRepository, pageable);
        return levels.map(levelMapper::toDTO);
    }

}
