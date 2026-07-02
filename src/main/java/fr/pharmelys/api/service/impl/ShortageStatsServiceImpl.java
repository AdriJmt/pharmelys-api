package fr.pharmelys.api.service.impl;

import fr.pharmelys.api.service.ShortageStatsService;

import org.springframework.stereotype.Service;

import fr.pharmelys.api.dto.shortage.ShortageStatsDTO;
import fr.pharmelys.api.entity.StockStatus;
import fr.pharmelys.api.repository.StockShortageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortageStatsServiceImpl implements ShortageStatsService {

    private final StockShortageRepository stockShortageRepository;

    @Override
    public ShortageStatsDTO getStats() {
        long shortageCount = stockShortageRepository.countByStatus(StockStatus.SHORTAGE);
        long tensionCount = stockShortageRepository.countByStatus(StockStatus.TENSION);
        return new ShortageStatsDTO(shortageCount, tensionCount);
    }
}
