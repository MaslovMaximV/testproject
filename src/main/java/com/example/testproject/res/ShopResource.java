package com.example.testproject.res;

import com.example.testproject.filter.ShopFilter;
import com.example.testproject.dto.ShopDto;
import com.example.testproject.entity.Shop;
import com.example.testproject.map.ShopMapper;
import com.example.testproject.repo.ShopRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin-ui/shops")
public class ShopResource {

    private final ShopRepository shopRepository;

    private final ShopMapper shopMapper;

    private final ObjectMapper objectMapper;

    public ShopResource(ShopRepository shopRepository,
                        ShopMapper shopMapper,
                        ObjectMapper objectMapper) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public PagedModel<ShopDto> getList(@ModelAttribute ShopFilter filter, Pageable pageable) {
        Specification<Shop> spec = filter.toSpecification();
        Page<Shop> shops = shopRepository.findAll(spec, pageable);
        Page<ShopDto> shopDtoPage = shops.map(shopMapper::toDto);
        return new PagedModel<>(shopDtoPage);
    }

    @GetMapping("/{id}")
    public ShopDto getOne(@PathVariable Long id) {
        Optional<Shop> shopOptional = shopRepository.findById(id);
        return shopMapper.toDto(shopOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @GetMapping("/by-ids")
    public List<ShopDto> getMany(@RequestParam List<Long> ids) {
        List<Shop> shops = shopRepository.findAllById(ids);
        return shops.stream()
                .map(shopMapper::toDto)
                .toList();
    }

    @PostMapping
    public ShopDto create(@RequestBody ShopDto dto) {
        Shop shop = shopMapper.toEntity(dto);
        Shop resultShop = shopRepository.save(shop);
        return shopMapper.toDto(resultShop);
    }

    @PatchMapping("/{id}")
    public ShopDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        Shop shop = shopRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        ShopDto shopDto = shopMapper.toDto(shop);
        objectMapper.readerForUpdating(shopDto).readValue(patchNode);
        shopMapper.updateWithNull(shopDto, shop);

        Shop resultShop = shopRepository.save(shop);
        return shopMapper.toDto(resultShop);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        Collection<Shop> shops = shopRepository.findAllById(ids);

        for (Shop shop : shops) {
            ShopDto shopDto = shopMapper.toDto(shop);
            objectMapper.readerForUpdating(shopDto).readValue(patchNode);
            shopMapper.updateWithNull(shopDto, shop);
        }

        List<Shop> resultShops = shopRepository.saveAll(shops);
        return resultShops.stream()
                .map(Shop::getId)
                .toList();
    }

    @DeleteMapping("/{id}")
    public ShopDto delete(@PathVariable Long id) {
        Shop shop = shopRepository.findById(id).orElse(null);
        if (shop != null) {
            shopRepository.delete(shop);
        }
        return shopMapper.toDto(shop);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        shopRepository.deleteAllById(ids);
    }
}
