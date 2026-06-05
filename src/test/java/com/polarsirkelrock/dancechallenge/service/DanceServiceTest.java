package com.polarsirkelrock.dancechallenge.service;

import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.repository.DanceRepository;
import com.polarsirkelrock.dancechallenge.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DanceServiceTest {

    @Autowired DanceService danceService;
    @Autowired ParticipantRepository participantRepository;
    @Autowired DanceRepository danceRepository;

    @BeforeEach
    void setup() {
        danceRepository.deleteAll();
        participantRepository.deleteAll();
        participantRepository.save(Participant.builder().id(1L).name("Anne").build());
        participantRepository.save(Participant.builder().id(2L).name("Ola").build());
        participantRepository.save(Participant.builder().id(3L).name("Per").build());
    }

    @Test
    void registerDance_success() {
        boolean result = danceService.registerDance(1L, 2L);
        assertThat(result).isTrue();
        assertThat(danceRepository.count()).isEqualTo(1);
    }

    @Test
    void registerDance_duplicate_returnsFalse() {
        danceService.registerDance(1L, 2L);
        boolean result = danceService.registerDance(2L, 1L);
        assertThat(result).isFalse();
        assertThat(danceRepository.count()).isEqualTo(1);
    }

    @Test
    void registerDance_withSelf_throws() {
        assertThrows(IllegalArgumentException.class, () -> danceService.registerDance(1L, 1L));
    }

    @Test
    void registerDance_normalizesOrder() {
        danceService.registerDance(3L, 1L);
        var dance = danceRepository.findAll().get(0);
        assertThat(dance.getParticipantA().getId()).isEqualTo(1L);
        assertThat(dance.getParticipantB().getId()).isEqualTo(3L);
    }
}
