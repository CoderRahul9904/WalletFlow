package com.rsm.walletflow.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.rsm.walletflow.dto.IngestionResponse;
import com.rsm.walletflow.entity.DataIngestion;

@Mapper(componentModel= "spring")
public interface DataIngestionMapper {
    
    IngestionResponse toDto(DataIngestion dataIngestion);
    DataIngestion toEntity(IngestionResponse ingestionResponse);
    List<IngestionResponse> toDtoList(List<DataIngestion> dataIngestions);
}
