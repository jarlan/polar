package com.polarsirkelrock.dancechallenge.service;

import com.polarsirkelrock.dancechallenge.dto.LeaderboardEntryDto;
import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.repository.DanceRepository;
import com.polarsirkelrock.dancechallenge.repository.DrawResultRepository;
import com.polarsirkelrock.dancechallenge.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final DanceRepository danceRepository;
    private final DrawResultRepository drawResultRepository;

    public List<Participant> findAll() {
        return participantRepository.findAll();
    }

    public Optional<Participant> findById(Long id) {
        return participantRepository.findById(id);
    }

    @Transactional
    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }

    @Transactional
    public int importCsv(java.io.InputStream inputStream) throws Exception {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("id")) continue;
                String[] parts = line.split(";");
                if (parts.length < 2) continue;
                try {
                    Long id = Long.parseLong(parts[0].trim());
                    String name = parts[1].trim();
                    if (!participantRepository.existsById(id)) {
                        Participant p = Participant.builder()
                                .id(id)
                                .name(name)
                                .build();
                        participantRepository.save(p);
                        count++;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Skipping invalid CSV line: {}", line);
                }
            }
        }
        return count;
    }

    public List<LeaderboardEntryDto> getLeaderboard() {
        List<Object[]> raw = danceRepository.leaderboardRaw();
        List<LeaderboardEntryDto> result = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < raw.size(); i++) {
            Object[] row = raw.get(i);
            Long id = ((Number) row[0]).longValue();
            String name = (String) row[1];
            long cnt = ((Number) row[2]).longValue();
            if (i > 0) {
                long prevCnt = ((Number) raw.get(i - 1)[2]).longValue();
                if (cnt < prevCnt) {
                    rank = i + 1;
                }
            }
            result.add(new LeaderboardEntryDto(rank, id, name, cnt));
        }
        return result;
    }

    public List<Participant> findPartners(Long participantId) {
        return participantRepository.findPartners(participantId);
    }

    public long countUniquePartners(Long participantId) {
        return participantRepository.countUniquePartners(participantId);
    }

    public long countTotal() {
        return participantRepository.count();
    }

    @Transactional
    public void deleteAll() {
        drawResultRepository.deleteAll();
        danceRepository.deleteAll();
        participantRepository.deleteAll();
        log.info("All participants, dances and draw results deleted");
    }
}
