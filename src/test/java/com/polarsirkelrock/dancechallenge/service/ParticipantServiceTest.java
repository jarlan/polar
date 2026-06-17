package com.polarsirkelrock.dancechallenge.service;

import com.polarsirkelrock.dancechallenge.dto.LeaderboardEntryDto;
import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.repository.DanceRepository;
import com.polarsirkelrock.dancechallenge.repository.DrawResultRepository;
import com.polarsirkelrock.dancechallenge.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParticipantServiceTest {

    @Autowired ParticipantService participantService;
    @Autowired DanceService danceService;
    @Autowired ParticipantRepository participantRepository;
    @Autowired DanceRepository danceRepository;
    @Autowired DrawResultRepository drawResultRepository;

    @BeforeEach
    void setup() {
        drawResultRepository.deleteAll();
        danceRepository.deleteAll();
        participantRepository.deleteAll();
    }

    @Test
    void importCsv_success() throws Exception {
        String csv = "1;Anne\n2;Ola\n3;Per\n";
        int count = participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertThat(count).isEqualTo(3);
        assertThat(participantRepository.count()).isEqualTo(3);
    }

    @Test
    void importCsv_skipsHeader() throws Exception {
        String csv = "id;name\n1;Anne\n2;Ola\n";
        int count = participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertThat(count).isEqualTo(2);
    }

    @Test
    void importCsv_skipsDuplicates() throws Exception {
        participantRepository.save(Participant.builder().id(1L).name("Anne").build());
        String csv = "1;Anne\n2;Ola\n";
        int count = participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        assertThat(count).isEqualTo(1);
    }

    @Test
    void leaderboard_sortsDescending() throws Exception {
        String csv = "1;Anne\n2;Ola\n3;Per\n4;Kari\n";
        participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        danceService.registerDance(1L, 2L);
        danceService.registerDance(1L, 3L);
        danceService.registerDance(1L, 4L);
        danceService.registerDance(2L, 3L);

        var leaderboard = participantService.getLeaderboard();
        assertThat(leaderboard.getFirst().getName()).isEqualTo("Anne");
        assertThat(leaderboard.getFirst().getUniquePartners()).isEqualTo(3);
    }

    @Test
    void leaderboard_tiedParticipantsShareRank() throws Exception {
        String csv = "1;Anne\n2;Ola\n3;Per\n4;Kari\n";
        participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        // Anne og Ola får begge 2 partnere, Per og Kari får 1 partner
        danceService.registerDance(1L, 2L); // Anne-Ola
        danceService.registerDance(1L, 3L); // Anne-Per
        danceService.registerDance(2L, 4L); // Ola-Kari

        var leaderboard = participantService.getLeaderboard();
        // Anne og Ola er begge #1
        assertThat(leaderboard.stream().filter(e -> e.getUniquePartners() == 2)
                .map(LeaderboardEntryDto::getRank).distinct().toList()).containsExactly(1);
        // Per og Kari hopper til #3 (to deler #1, så neste er #3)
        assertThat(leaderboard.stream().filter(e -> e.getUniquePartners() == 1)
                .map(LeaderboardEntryDto::getRank).distinct().toList()).containsExactly(3);
    }

    @Test
    void countUniquePartners() throws Exception {
        String csv = "1;Anne\n2;Ola\n3;Per\n";
        participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        danceService.registerDance(1L, 2L);
        danceService.registerDance(1L, 3L);

        long count = participantService.countUniquePartners(1L);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void deleteAll_removesAllParticipantsAndDances() throws Exception {
        String csv = "1;Anne\n2;Ola\n3;Per\n";
        participantService.importCsv(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
        danceService.registerDance(1L, 2L);
        danceService.registerDance(2L, 3L);

        participantService.deleteAll();

        assertThat(participantRepository.count()).isZero();
        assertThat(danceRepository.count()).isZero();
    }
}
