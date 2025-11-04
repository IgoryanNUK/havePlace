package vk.haveplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vk.haveplace.services.admin.RegularEventService;
import vk.haveplace.services.objects.dto.BookingsRegularEventDto;
import vk.haveplace.services.objects.dto.RegularEventDTO;
import vk.haveplace.services.objects.requests.RegularEventRequest;
import vk.haveplace.services.objects.requests.RegularEventUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/admin/event")
public class RegularEventController {

    private final RegularEventService regularEventService;

    @Autowired
    public RegularEventController(RegularEventService regularEventService) {
        this.regularEventService = regularEventService;
    }

    @PostMapping("/add")
    public BookingsRegularEventDto addRegularEvent(@RequestBody @Validated RegularEventRequest request) {
        return regularEventService.add(request);
    }

    @PostMapping("/update")
    public BookingsRegularEventDto updateRegularEvent(@RequestBody @Validated RegularEventUpdateRequest request) {
        return regularEventService.update(request);
    }

    @DeleteMapping("/{id}")
    public int remove(@PathVariable int id) {
        return regularEventService.remove(id);
    }

    @GetMapping("")
    public List<RegularEventDTO> getAll() {
        return regularEventService.getAll();
    }
}
