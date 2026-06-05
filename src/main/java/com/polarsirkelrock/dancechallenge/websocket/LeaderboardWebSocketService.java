package com.polarsirkelrock.dancechallenge.websocket;

import com.polarsirkelrock.dancechallenge.dto.LeaderboardEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastLeaderboard(List<LeaderboardEntryDto> leaderboard) {
        log.debug("Broadcasting leaderboard update: {} entries", leaderboard.size());
        messagingTemplate.convertAndSend("/topic/leaderboard", leaderboard);
    }
}
