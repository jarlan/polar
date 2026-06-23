package com.polarsirkelrock.dancechallenge.dto;


import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PrizeDrawAnimationResponseDto {
  int threshold;
  List<PrizeDrawCandidateDto> candidates;
  PrizeDrawCandidateDto winner;
}
