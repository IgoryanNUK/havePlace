package vk.haveplace.controllers;

import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vk.haveplace.database.entities.Role;
import vk.haveplace.services.AdminBookingReadService;
import vk.haveplace.services.AdminBookingWriteService;
import vk.haveplace.services.AdminService;
import vk.haveplace.services.objects.dto.AdminDTO;
import vk.haveplace.services.objects.requests.*;

import java.util.List;

@RestController
@RequestMapping("/admin/manage")
public class ManageController {

    private final AdminService adminService;
    private final AdminBookingWriteService bookingWriteService;
    private final AdminBookingReadService bookingReadService;

    public ManageController(AdminService adminService, AdminBookingWriteService bookingWService,
                            AdminBookingReadService bookingRService) {
        this.adminService = adminService;
        this.bookingWriteService = bookingWService;
        this.bookingReadService = bookingRService;
    }

    @PostMapping("/add")
    public AdminDTO add(@RequestBody @Validated AdminRequest request) {
        return adminService.add(request);
    }

    @PostMapping("/update")
    public AdminDTO update(@RequestBody @Validated AdminUpdateRequest req) {
        return adminService.update(req);
    }

    @PostMapping("/role")
    public AdminDTO updateRole(@RequestBody @Validated AdminRoleUpdateRequest req) {
        return adminService.updateRole(req.getId(), req.getRole());
    }

    @GetMapping("")
    public List<AdminDTO> get() {
        return adminService.getAll();
    }

    @DeleteMapping("/{id}")
    public boolean remove(@PathVariable int id) {
        return adminService.removeById(id);
    }

    @PostMapping("/shift")
    public Boolean setShift(@RequestBody @Validated ShiftRequest req) {
        return bookingWriteService.setAdminShift(req.getAdminId(), req.getShift());
    }

    @GetMapping("/shift")
    public AdminDTO getAdminByShift(@RequestBody @Validated DateAndTimesRequest shift) {
        return bookingReadService.getAdminByShift(shift);
    }

}
