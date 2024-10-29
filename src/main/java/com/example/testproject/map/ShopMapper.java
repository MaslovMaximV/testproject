package com.example.testproject.map;

import com.example.testproject.dto.ShopDto;
import com.example.testproject.entity.Shop;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShopMapper {
    Shop toEntity(ShopDto shopDto);

    ShopDto toDto(Shop shop);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Shop partialUpdate(ShopDto shopDto, @MappingTarget Shop shop);

    Shop updateWithNull(ShopDto shopDto, @MappingTarget Shop shop);
}