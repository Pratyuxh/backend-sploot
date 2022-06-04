package com.sploot.api.controller;

import com.sploot.api.model.dto.*;
import com.sploot.api.model.entity.PetBreed;
import com.sploot.api.model.entity.PetProfile;
import com.sploot.api.model.mapper.DeviceMapper;
import com.sploot.api.model.mapper.PetBreedMapper;
import com.sploot.api.model.mapper.PetProfileMapper;
import com.sploot.api.service.DeviceService;
import com.sploot.api.service.PetService;
import com.sploot.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class PetsController {
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private PetBreedMapper petBreedMapper;

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @Autowired
    private PetProfileMapper petProfileMapper;

    @GetMapping("/v1/pets/breeds")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto getActivePetBreeds() {
        List<PetBreed> activePetBreeds = petService.getActivePetBreeds();
        if (CollectionUtils.isEmpty(activePetBreeds)) {
            return new ErrorResponseDto<>(null, "No Pet breeds found in data store");
        }
        activePetBreeds.sort(Comparator.comparing(PetBreed::getName));
        Map<String, List<PetBreed>> activePetBreedMap = activePetBreeds.stream().collect(Collectors.groupingBy(petBreed -> petBreed.getPetType().getDescription(), Collectors.toList()));
        return new SuccessResponseDto<>(activePetBreedMap, "Success in fetching Pet Breeds");
    }

    @GetMapping("/v2/pets/breeds")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto getActivePetBreedsSortedByPopularity() {
        Map<String, List<PetBreed>> petBreedsMap = petService.getActivePetBreedsSortedByPopularity();
        if (CollectionUtils.isEmpty(petBreedsMap)) return new ErrorResponseDto<>(null, "No Pet breeds found in data store");

        Map<String, List<PetBreedDto>> petBreedsDTOMap = new HashMap<>();
        petBreedsMap.keySet().forEach(key -> {
            List<PetBreed> petBreedList = petBreedsMap.get(key);
            petBreedsDTOMap.put(key, petBreedList.stream().map(petBreed -> petBreedMapper.entityToDTO(petBreed)).collect(Collectors.toList()));
        });
        return new SuccessResponseDto<>(petBreedsDTOMap, "Success in fetching Pet Breeds");
    }

    @GetMapping("/v1/profile/pet")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseDto getPetProfile(@RequestParam(required = false) Long petId) {
        try {
            if (petId != null) {
                PetProfile petProfile = petService.getPetDetailsById(petId);
                return new SuccessResponseDto(petProfileMapper.entityToResponseDTO(petProfile));
            }

            Long loggedInUserId = userService.getLoggedInUserId();
            List<PetProfile> petProfiles = petService.getPetProfilesByUserId(loggedInUserId);
            List<PetProfileDto> petProfileDtoList = petProfiles
                    .stream()
                    .map(petProfile -> petProfileMapper.entityToResponseDTO(petProfile))
                    .collect(Collectors.toList());
            return new SuccessResponseDto(petProfileDtoList);

        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in fetching pet profile");
        }
    }

    @PostMapping({"/v1/profile/pet"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseDto saveOrUpdateProfile(@RequestBody PetProfileRequestDTO petProfileRequestDTO) {
        try {
            PetProfile petProfile = petService.storePetProfile(petProfileMapper.requestDTOToEntity(petProfileRequestDTO));
            String responseMsg = (petProfileRequestDTO.getId() > 0 ? "Splooter info updated successfully!" : "Splooter activated!");
            return new SuccessResponseDto<>(petProfileMapper.entityToResponseDTO(petProfile), responseMsg);
        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in creating Pets profile");
        }
    }

    @RequestMapping(value = "/v1/user/pet", method = RequestMethod.GET)
    public ResponseDto<?> getUserPetProfiles(@RequestParam Long userId) {
        try {
            List<PetProfile> petProfiles = petService.getUserPetDetailsResponse(userId);
            List<PetProfileDto> petProfileResponseDtoList =
                    petProfiles.stream()
                            .map(petProfile -> petProfileMapper.entityToResponseDTO(petProfile))
                            .collect(Collectors.toList());
            return new SuccessResponseDto<>(petProfileResponseDtoList, "Pet Profiles fetched succefully");
        } catch (Exception e) {
            log.error("Error in fetching pets details for userID   : {}",userId, e);
            return new ErrorResponseDto<>(new ArrayList<>(),"Pet Profiles fetched succefully");
        }
    }
    @DeleteMapping(value = "/v1/profile/pet")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseDto saveOrUpdateProfile(@RequestParam long petId) {
        try {
            PetProfile petProfile = petService.deletePetProfile(petId);
            return new SuccessResponseDto<>(petProfileMapper.entityToResponseDTO(petProfile),
                    "Pet profile deleted successfully");

        } catch (Exception e) {
            log.error("Error in fetching primary role : {}", e);
            return new ErrorResponseDto("Error in deleting petprofile");
        }
    }
}
