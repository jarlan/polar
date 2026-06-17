package com.polarsirkelrock.dancechallenge.service;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QrCodeServiceTest {

    @Autowired QrCodeService qrCodeService;
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
    void deleteAllQrFiles_clearsQrCodeUrlOnParticipants() throws Exception {
        Participant p1 = participantRepository.save(
                Participant.builder().id(1L).name("Anne").qrCodeUrl("/qr/qr_1.png").build());
        Participant p2 = participantRepository.save(
                Participant.builder().id(2L).name("Ola").qrCodeUrl("/qr/qr_2.png").build());

        qrCodeService.deleteAllQrFiles();

        assertThat(participantRepository.findById(p1.getId()))
                .hasValueSatisfying(p -> assertThat(p.getQrCodeUrl()).isNull());
        assertThat(participantRepository.findById(p2.getId()))
                .hasValueSatisfying(p -> assertThat(p.getQrCodeUrl()).isNull());
    }

    @Test
    void deleteAllQrFiles_noFilesOnDisk_returnsZero() throws Exception {
        int deleted = qrCodeService.deleteAllQrFiles();
        assertThat(deleted).isZero();
    }

    @Test
    void deleteAllQrFiles_participantWithoutQr_notAffected() throws Exception {
        participantRepository.save(Participant.builder().id(1L).name("Anne").build());

        int deleted = qrCodeService.deleteAllQrFiles();

        assertThat(deleted).isZero();
        assertThat(participantRepository.findById(1L))
                .hasValueSatisfying(p -> assertThat(p.getQrCodeUrl()).isNull());
    }
}
