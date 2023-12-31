package com.example.shop.controller;

import com.example.shop.dto.HoaDonCTTDTO;
import com.example.shop.dto.ThanhToanHoaDonDTO;
import com.example.shop.entity.HoaDon;
import com.example.shop.entity.LichSuHoaDon;
import com.example.shop.entity.Voucher;
import com.example.shop.repositories.HoaDonRepository;
import com.example.shop.repositories.KhachHangRepository;
import com.example.shop.repositories.NhanVienRepository;
import com.example.shop.service.HoaDonService;
import com.example.shop.service.LichSuHoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("hoa_don")
public class HoaDonController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private KhachHangRepository ssKH;

    @Autowired
    private NhanVienRepository ssNV;

    @Autowired
    private LichSuHoaDonService lichSuHoaDonService;

    @GetMapping("getHoaDons")
    public ResponseEntity<List<HoaDon>> getHoaDons(
//            @RequestParam(name = "page" , defaultValue = "0")Integer numPage
    ) {
        List<HoaDon> page = hoaDonService.getHoaDons();
        return ResponseEntity.ok(page);
    }

    @GetMapping("getHoaDons/{trangThai}")
    public ResponseEntity<List<HoaDon>> getHDs(
            @RequestParam(name = "page", defaultValue = "0") Integer numPage,
            @PathVariable Integer trangThai
    ) {
        List<HoaDon> page = hoaDonService.getHoaDons();
        if (trangThai != -1) {
            page = hoaDonService.getHDs(trangThai);
        }

        return ResponseEntity.ok(page);
    }

    @GetMapping("getHoaDon/{id}")
    public ResponseEntity<HoaDon> getHoaDon(@PathVariable("id") HoaDon hoaDon) {
        return ResponseEntity.ok(hoaDon);
    }

    @GetMapping("getHoaDonCTT")
    public ResponseEntity getHoaDonCTT() {
        List<Object[]> resultList = hoaDonRepository.getHDChuaTT();
        List<HoaDonCTTDTO> list = new ArrayList<>();
        for (Object[] row : resultList) {
            String ma = (String) row[0];
            String idKhachHang = (String) row[1];
            String idNhanVien = (String) row[2];
            BigDecimal soLuong = (BigDecimal) row[3];
            Integer loaiHD = (Integer) row[4];
            Integer trangThai = (Integer) row[5];

            HoaDonCTTDTO hoaDon = HoaDonCTTDTO.builder()
                    .ma(ma)
                    .idKhachHang(idKhachHang == null ? null : ssKH.findById(idKhachHang).get())
                    .idNhanVien(idNhanVien == null ? null : ssNV.findById(idNhanVien).get())
                    .soLuong(soLuong.intValue())
                    .loaiHd(loaiHD)
                    .trangThai(trangThai)
                    .build();
            list.add(hoaDon);
        }
        return ResponseEntity.ok(list);
    }


    @PostMapping("add")
    public ResponseEntity<HoaDon> addHoaDon(
            @RequestBody HoaDon hoaDon
    ) {
        HoaDon hoaDonSave = hoaDonService.addHoaDon(hoaDon);
        return new ResponseEntity<>(hoaDonSave, HttpStatus.CREATED);
    }

    @PostMapping("taoHoaDon")
    public ResponseEntity<HoaDon> taoHoaDon() {
        Integer maxMa = Integer.parseInt(hoaDonRepository.getMaxMa());
        HoaDon hoaDon = HoaDon.builder()
                .ma("HD" + (maxMa + 1))
                .trangThai(7)
                .deleted(1)
                .loaiHd(1)
                .nguoiTao("Đông")
                .ngayTao(new Date())
                .build();
        hoaDonRepository.save(hoaDon);
        return new ResponseEntity<>(hoaDon, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<HoaDon> updateHoaDon(
            @PathVariable("id") String id,
            @RequestBody HoaDon hoaDon
    ) {
        try {
            HoaDon hoaDon1 = hoaDonService.getHoaDon(id);
            if (hoaDon1 != null) {
                hoaDon.setId(hoaDon.getId());
                HoaDon updateHoaDon = hoaDonService.updateHoaDon(hoaDon);
                return new ResponseEntity<>(updateHoaDon, HttpStatus.CREATED);
            } else {
                throw new Exception("khong co id" + id);
            }
        } catch (Exception exception) {
            return null;
        }
    }

    @PutMapping("thanhToanHoaDon/{id}")
    public ResponseEntity thanhToanHoaDon(
            @PathVariable("id") String id,
            @RequestBody ThanhToanHoaDonDTO hoaDon
    ) {
        try {
            HoaDon hoaDon1 = hoaDonRepository.getHoaDonByMa(id);
            System.out.println(hoaDon.toString());
            if (hoaDon.getTrangThai().equals("1") && hoaDon.getLoaiHd().equals("0")) {
                LichSuHoaDon lichSuHoaDon = LichSuHoaDon.builder()
                        .id_hoa_don(hoaDon1)
                        .moTaHoaDon("Chờ xác nhận")
                        .deleted(1)
                        .nguoiTao("Đông")
                        .ngayTao(new Date(System.currentTimeMillis()))
                        .build();
                LichSuHoaDon lichSuHoaDon2 = LichSuHoaDon.builder()
                        .id_hoa_don(hoaDon1)
                        .moTaHoaDon("Xác nhận")
                        .deleted(1)
                        .nguoiTao("Đông")
                        .ngayTao(new Date(System.currentTimeMillis()))
                        .build();
                lichSuHoaDonService.addLichSuHoaDon(lichSuHoaDon);
                lichSuHoaDonService.addLichSuHoaDon(lichSuHoaDon2);
                System.out.println("true");
            }

            if (hoaDon1 != null) {
                hoaDon1.setTrangThai(4);
                hoaDon1.setLoaiHd(Integer.parseInt(hoaDon.getLoaiHd()));
                hoaDon1.setTrangThai(Integer.parseInt(hoaDon.getTrangThai()));
                hoaDon1.setDiaChi(hoaDon.getDiaChi());
                hoaDon1.setTenKhachHang(hoaDon.getTenKhachHang());
                hoaDon1.setSdt(hoaDon.getSdt());
                hoaDon1.setNgayTao(new Date());
                hoaDon1.setId_khach_hang(ssKH.findByMa(hoaDon.getMaKH()));
                hoaDon1.setTongTien(BigDecimal.valueOf(Double.parseDouble(hoaDon.getTongTien())));
                HoaDon updateHoaDon = hoaDonRepository.save(hoaDon1);
                return new ResponseEntity<>(updateHoaDon, HttpStatus.CREATED);
            } else {
                throw new Exception("khong co id" + id);
            }
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body("ERROR");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteHoaDon(@PathVariable("id") String id) {
        String mess = "";
        HoaDon hoaDon = hoaDonService.getHoaDon(id);
        if (hoaDon == null) {
            mess = "Not find hoa don with " + id;

        } else {
            hoaDon.setDeleted(0);
           hoaDonService.updateHoaDon(hoaDon);
            mess = "delete success";
        }
//        System.out.println(mess);
        return new ResponseEntity(mess, HttpStatus.OK);
    }

    @PostMapping("cancelHD/{id}")
    public ResponseEntity cancelHD(@PathVariable("id")String id , @RequestBody Object trangThai){
        System.out.println(trangThai);
        HoaDon hoaDon = hoaDonService.getHoaDon(id);
        hoaDon.setTrangThai(5);
        HoaDon don = hoaDonService.updateHoaDon(hoaDon);
        System.out.println(don);
//        System.out.println(mess);
        return new ResponseEntity(don , HttpStatus.OK);
    }


}
