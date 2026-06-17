package com.polarsirkelrock.dancechallenge.controller;

import com.polarsirkelrock.dancechallenge.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ParticipantService participantService;
    private final DanceService danceService;
    private final QrCodeService qrCodeService;
    private final PrizeDrawService prizeDrawService;

    @Value("${app.default-prize-threshold:10}")
    private int defaultThreshold;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("participants", participantService.findAll());
        model.addAttribute("totalDances", danceService.countTotal());
        model.addAttribute("leaderboard", participantService.getLeaderboard());
        model.addAttribute("drawHistory", prizeDrawService.getHistory());
        model.addAttribute("defaultThreshold", defaultThreshold);
        return "admin";
    }

    @PostMapping("/import-csv")
    public String importCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            int count = participantService.importCsv(file.getInputStream());
            model.addAttribute("importMessage", "Imported " + count + " participants.");
        } catch (Exception e) {
            model.addAttribute("importError", "Import failed: " + e.getMessage());
        }
        return "redirect:/admin?imported=true";
    }

    @PostMapping("/generate-qr")
    public String generateQr(HttpServletRequest request,
                              @RequestParam(required = false) String baseUrl) {
        try {
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() != 80 && request.getServerPort() != 443
                           ? ":" + request.getServerPort() : "");
            }
            baseUrl = baseUrl.stripTrailing().replaceAll("/+$", "");
            int count = qrCodeService.generateAll(baseUrl);
            return "redirect:/admin?qrGenerated=" + count;
        } catch (Exception e) {
            return "redirect:/admin?qrError=" + e.getMessage();
        }
    }

    @GetMapping("/download-qr")
    public ResponseEntity<byte[]> downloadQr() {
        try {
            byte[] zip = qrCodeService.generateZip();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qr-codes.zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zip);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/prize-draw")
    public String prizeDraw(@RequestParam(defaultValue = "10") int threshold) {
        try {
            var result = prizeDrawService.draw(threshold);
            return "redirect:/admin?winner=" + result.getWinner().getName();
        } catch (Exception e) {
            return "redirect:/admin?drawError=" + e.getMessage();
        }
    }

    @PostMapping("/delete-participants")
    public String deleteParticipants() {
        try {
            qrCodeService.deleteAllQrFiles();
            participantService.deleteAll();
            return "redirect:/admin?deletedParticipants=true";
        } catch (Exception e) {
            return "redirect:/admin?deleteError=" + e.getMessage();
        }
    }

    @PostMapping("/delete-statistics")
    public String deleteStatistics() {
        try {
            danceService.deleteAllDances();
            return "redirect:/admin?deletedStatistics=true";
        } catch (Exception e) {
            return "redirect:/admin?deleteError=" + e.getMessage();
        }
    }

    @PostMapping("/delete-qr")
    public String deleteQr() {
        try {
            int count = qrCodeService.deleteAllQrFiles();
            return "redirect:/admin?deletedQr=" + count;
        } catch (Exception e) {
            return "redirect:/admin?deleteError=" + e.getMessage();
        }
    }
}
