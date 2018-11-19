package com.endava.contest.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BestBuy {

    private Double buyPoint;

    private Double sellPoint;

    private String batchName;
}