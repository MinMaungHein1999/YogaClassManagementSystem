package com.yogiBooking.common.service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.dto.user.UserDTO;
import com.yogiBooking.common.dto.yogi.MasterYogiDTO;
import com.yogiBooking.common.dto.yogi_nrc.MasterYogiNrcDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcCreateDTO;
import com.yogiBooking.common.dto.yogi_nrc.YogiNrcDTO;
import com.yogiBooking.common.entity.*;
import com.yogiBooking.common.exception.ResourceAlreadyExistsException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.UserMapper;
import com.yogiBooking.common.mapper.YogiNrcMapper;
import com.yogiBooking.common.repository.*;
import com.yogiBooking.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.yogi.YogiCreateDTO;
import com.yogiBooking.common.dto.yogi.YogiDTO;
import com.yogiBooking.common.dto.yogi.YogiUpdateDTO;
import com.yogiBooking.common.entity.constants.Status;
import com.yogiBooking.common.mapper.YogiMapper;

@Service
@RequiredArgsConstructor
public class YogiService {
    private final UserRepository userRepository;
    private final GenericSearchService genericSearchService;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final YogiRepository yogiRepository;
    private final UserService userService;
    private final YogiMapper yogiMapper;
    private final YogiNrcMapper yogiNrcMapper;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final LevelRepository levelRepository;
    private final YogiNrcService yogiNrcService;
    private final YogaClassRepository yogaClassRepository;

    @Transactional
    public YogiDTO createYogi(YogiCreateDTO yogiCreateDTO) {
        // 1. Validate input and handle potential errors early
        validateYogiInput(yogiCreateDTO);

        // 2. Map DTO to entity
        Yogi yogi = yogiMapper.toEntity(yogiCreateDTO);
        yogi.setStatus(Status.ACTIVE);
        clearNullReferences(yogi, yogiCreateDTO);

        // 3. Handle User creation within its own transaction
        User user = yogi.getLoginUser();
        UserCreateDTO userCreateDTO = userMapper.toCreateDTO(user);
        UserDTO userDTO = userService.createUser(userCreateDTO); // Assume this is transactional
        yogi.setLoginUser(userRepository.findById(userDTO.getId()).orElseThrow());  //Should not throw an exception, we just created it.

        // 4. Save Yogi (initial save)
        yogi = yogiRepository.save(yogi);

        // 5. Handle NRC creation if applicable
        if (isLocalYogi(yogiCreateDTO)) {
            createYogiNrc(yogi, yogiCreateDTO); //Extract this method
        }

        // 6. Save Yogi (final save, after NRC) - redundant if NRC creation always saves yogi.
        //yogi = yogiRepository.save(yogi); // Remove this line

        return yogiMapper.toDto(yogi);
    }

    private void validateYogiInput(YogiCreateDTO yogiCreateDTO) {
        checkYogiIdAlreadyExist(yogiCreateDTO.getYogiId(), null);
        checkYogiPassportIDAlreadyExist(yogiCreateDTO.getPassportID(), null);
        //Consider adding a check for the user.
    }


    private void createYogiNrc(Yogi yogi, YogiCreateDTO yogiCreateDTO) {
        MasterYogiNrcDTO masterYogiNrcDto = buildNrcDto(yogiCreateDTO);
        masterYogiNrcDto.setYogiId(yogi.getId());
        YogiNrcCreateDTO yogiNrcCreateDTO = yogiNrcMapper.toDto(masterYogiNrcDto);
        YogiNrcDTO yogiNrcDTO = yogiNrcService.create(yogiNrcCreateDTO); //Assume this is transactional.
        yogi.setNrc(yogiNrcDTO.getNrc());
        yogiRepository.save(yogi); // Save here, within the NRC transaction.
    }


    @Transactional
    public YogiDTO updateYogi(Long id, YogiUpdateDTO yogiUpdateDTO){
        Yogi yogi = yogiRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Yogi Not Found !!"));
        updateYogiAttributes(yogi, yogiUpdateDTO);
        yogi = yogiRepository.save(yogi);
        return yogiMapper.toDto(yogi);
    }

    @Transactional
    public void deleteYogi(Long id){
        Yogi yogi = yogiRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Yogi Not Found !!"));
        yogi.setStatus(Status.DELETED);
        yogiRepository.save(yogi);
    }

    @Transactional
    public YogiDTO findYogiById(Long id){
        Yogi yogi = yogiRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Yogi with this id Not Found !!"));
        return yogiMapper.toDto(yogi);
    }

    @Transactional
    public List<YogiDTO> getAllYogis(){
        return yogiRepository.findAll().stream().map(yogiMapper::toDto).toList();
    }

    @Transactional
    public Page<YogiDTO> findYogiByFilter(SearchRequest request, Pageable pageable){
        Page<Yogi> yogis = genericSearchService.search(request, yogiRepository,pageable);
        return yogis.map(yogiMapper::toDto);
    }

    private void updateYogiAttributes(Yogi yogi, YogiUpdateDTO dto) {
        clearNullReferences(yogi, dto);
        checkYogiIdAlreadyExist(dto.getYogiId(), yogi.getId());
        checkYogiPassportIDAlreadyExist(dto.getPassportID(), yogi.getId());

        if(isLocalYogi(dto)) {
            updateYogiNRC(yogi, dto);
        }

        // Update simple attributes
        yogi.setYogiId(dto.getYogiId());
        yogi.setPassportID(dto.getPassportID());
        yogi.setName(dto.getName());
        yogi.setGenderType(dto.getGenderType());
        yogi.setBirthDate(dto.getBirthDate());
        yogi.setPhone(dto.getPhone());
        yogi.setAddress(dto.getAddress());
        yogi.setForeignYogi(dto.isForeignYogi());
    }

    private Country findCountry(Long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Country with Id %d not found!", countryId)));
    }


    private City findCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("City with Id %d not found!", id)));
    }

    private Region findRegion(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Region with Id %d not found!", id)));
    }

    private Level findLevel(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Level with Id %d not found!", id)));
    }

    private void updateYogiNRC(Yogi yogi, YogiUpdateDTO yogiUpdateDTO) {
        YogiNrcDTO yogiNrcDTO = null;
        Optional<YogiNrcDTO> opYogiNrcDTO = yogiNrcService.getYogiNrcByYogiId(yogi.getId());

        MasterYogiNrcDTO masterYogiNrcDTO = new MasterYogiNrcDTO();

        masterYogiNrcDTO.setYogiId(yogi.getId());
        masterYogiNrcDTO.setNrcCodeId(yogiUpdateDTO.getNrcCodeId());
        masterYogiNrcDTO.setType(yogiUpdateDTO.getNrcType());
        masterYogiNrcDTO.setPostFixDigit(yogiUpdateDTO.getPostFixDigit());

        if(opYogiNrcDTO.isPresent()){
            yogiNrcDTO = opYogiNrcDTO.get();
            yogiNrcService.update(yogiNrcDTO.getId(), masterYogiNrcDTO);
            yogi.setNrc(yogiNrcDTO.getNrc());
        } else {
            YogiNrcDTO createdYogiNrcDTO = yogiNrcService.create(masterYogiNrcDTO);
            yogi.setNrc(createdYogiNrcDTO.getNrc());
        }
    }

    public List<YogiDTO> getYogisNotInYogaClass(Long classId) {
        yogaClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Yoga Class with Id %d not found!".formatted(classId)));

        List<Yogi> yogis = yogiRepository.findYogisNotInYogaClass(classId).orElse(Collections.emptyList());

        return yogis.stream()
                .map(yogiMapper::toDto)
                .toList();
    }

    private void checkYogiPassportIDAlreadyExist(String passportId, Long id) {
        if (StringUtils.isNullOrBlank(passportId)) {
            return;
        }

        List<Yogi> yogis = (id == null)
                ? yogiRepository.findByPassportId(passportId).orElse(Collections.emptyList())
                : yogiRepository.findByPassportIdAndIdNot(passportId, id).orElse(Collections.emptyList());

        if (!yogis.isEmpty()) {
            throw new ResourceAlreadyExistsException(
                    "PassportId is already existed with Yogi ID %s".formatted(id)
            );
        }
    }


    private void checkYogiIdAlreadyExist(String yogiId, Long id) {
        if (StringUtils.isNullOrBlank(yogiId)) {
            return;
        }

        Optional<List<Yogi>> yogiList;
        if(id == null){
            yogiList = yogiRepository.findByYogiId(yogiId);
        }else{
            yogiList = yogiRepository.findByYogiIdAndIdNot(yogiId, id);
        }

        boolean exist = yogiList.isPresent() && !yogiList.get().isEmpty();
        if(exist){
            throw new ResourceAlreadyExistsException("Yogi Id is already existed with Yogi ID %s"
                    .formatted(yogiId));
        }
    }

    private boolean isLocalYogi(MasterYogiDTO dto) {
        return !dto.isForeignYogi() || dto.getNrcCodeId() != null;
    }

    private void clearNullReferences(Yogi yogi, MasterYogiDTO dto) {
        setIfNotNullOrElseNull(dto.getRegionId(), this::findRegion, yogi::setRegion);
        setIfNotNullOrElseNull(dto.getCityId(), this::findCity, yogi::setCity);
        setIfNotNullOrElseNull(dto.getCountryId(), this::findCountry, yogi::setCountry);
        setIfNotNullOrElseNull(dto.getLevelId(), this::findLevel, yogi::setLevel);
    }

    private <T, R> void setIfNotNullOrElseNull(Long id, Function<Long, R> finder, Consumer<R> setter) {
        if (id == null) {
            setter.accept(null);
        } else {
            setter.accept(finder.apply(id));
        }
    }

    private MasterYogiNrcDTO buildNrcDto(YogiCreateDTO yogiCreateDTO) {
        MasterYogiNrcDTO dto = new MasterYogiNrcDTO();
        dto.setNrcCodeId(yogiCreateDTO.getNrcCodeId());
        dto.setType(yogiCreateDTO.getNrcType());
        dto.setPostFixDigit(yogiCreateDTO.getPostFixDigit());
        return dto;
    }
}