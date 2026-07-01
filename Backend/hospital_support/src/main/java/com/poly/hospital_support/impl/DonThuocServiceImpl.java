package com.poly.hospital_support.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Element;
import com.poly.hospital_support.entity.ChiTietDonThuoc;
import com.poly.hospital_support.entity.DonThuoc;
import com.poly.hospital_support.repository.ChiTietDonThuocRepository;
import com.poly.hospital_support.repository.DonThuocRepository;
import com.poly.hospital_support.service.DonThuocService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonThuocServiceImpl implements DonThuocService {

    private final DonThuocRepository donThuocRepository;
    private final ChiTietDonThuocRepository chiTietDonThuocRepository;

    @Override
    public DonThuoc getPrescriptionByBenhAnId(Integer benhAnId) {
        return donThuocRepository.findByBenhAnId(benhAnId)
                .orElseThrow(() -> new RuntimeException("Đơn thuốc không tồn tại cho bệnh án này!"));
    }

    @Override
    public DonThuoc getPrescriptionById(Integer id) {
        return donThuocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn thuốc không tồn tại!"));
    }

    @Override
    public byte[] generatePrescriptionPdf(Integer id) {
        DonThuoc dt = getPrescriptionById(id);
        List<ChiTietDonThuoc> details = chiTietDonThuocRepository.findByDonThuocId(id);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitle;
            Font fontBold;
            Font fontNormal;
            try {
                com.lowagie.text.pdf.BaseFont bf = com.lowagie.text.pdf.BaseFont.createFont(
                        "C:\\Windows\\Fonts\\arial.ttf", 
                        com.lowagie.text.pdf.BaseFont.IDENTITY_H, 
                        com.lowagie.text.pdf.BaseFont.EMBEDDED);
                fontTitle = new Font(bf, 18, Font.BOLD);
                fontBold = new Font(bf, 12, Font.BOLD);
                fontNormal = new Font(bf, 12, Font.NORMAL);
            } catch (Exception e) {
                fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);
            }

            // Header/Title
            Paragraph title = new Paragraph("ĐƠN THUỐC HỖ TRỢ Y TẾ", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Patient Info
            document.add(new Paragraph("Mã đơn thuốc: " + dt.getId(), fontBold));
            document.add(new Paragraph("Họ và tên bệnh nhân: " + dt.getBenhNhan().getHoTen(), fontNormal));
            document.add(new Paragraph("Giới tính: " + dt.getBenhNhan().getGioiTinh() + "   -   Ngày sinh: " + (dt.getBenhNhan().getNgaySinh() != null ? dt.getBenhNhan().getNgaySinh().toString() : ""), fontNormal));
            document.add(new Paragraph("Địa chỉ: " + (dt.getBenhNhan().getDiaChi() != null ? dt.getBenhNhan().getDiaChi() : ""), fontNormal));
            document.add(new Paragraph("Bác sĩ kê đơn: " + dt.getBacSi().getHoTen(), fontNormal));
            document.add(new Paragraph("Ngày kê: " + (dt.getNgayTao() != null ? dt.getNgayTao().toString() : ""), fontNormal));
            document.add(new Paragraph("Trạng thái: " + dt.getTrangThai(), fontNormal));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------", fontNormal));

            // Table of Medicines
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);
            table.setWidths(new float[]{1f, 3f, 1.5f, 2f, 3f});

            // Headers
            PdfPCell cell;
            String[] headers = {"STT", "Tên thuốc", "Số lượng", "Đơn vị tính", "Hướng dẫn sử dụng"};
            for (String header : headers) {
                cell = new PdfPCell(new Phrase(header, fontBold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            int index = 1;
            for (ChiTietDonThuoc line : details) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(index++), fontNormal)));
                table.addCell(new PdfPCell(new Phrase(line.getThuoc().getTenThuoc(), fontNormal)));
                
                int qty = line.getSoLuongThucPhat() != null ? line.getSoLuongThucPhat() : line.getSoLuong();
                table.addCell(new PdfPCell(new Phrase(String.valueOf(qty), fontNormal)));
                table.addCell(new PdfPCell(new Phrase(line.getThuoc().getDonViTinh(), fontNormal)));
                table.addCell(new PdfPCell(new Phrase(line.getHuongDanSuDung() != null ? line.getHuongDanSuDung() : "", fontNormal)));
            }

            document.add(table);

            // Total price
            document.add(new Paragraph("Tổng tiền thanh toán: " + (dt.getTongTien() != null ? dt.getTongTien().toString() : "0") + " VND", fontBold));

            // Signatures
            Paragraph footer = new Paragraph("\n\nBác sĩ điều trị\n(Ký và ghi rõ họ tên)", fontBold);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF đơn thuốc: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DonThuoc> getPrescriptionsByPatientId(Integer patientId) {
        List<DonThuoc> list = donThuocRepository.findByBenhNhanId(patientId);
        if (list.isEmpty()) {
            list = donThuocRepository.findByBenhNhanTaiKhoanId(patientId);
        }
        return list;
    }
}
