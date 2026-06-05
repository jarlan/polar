package com.polarsirkelrock.dancechallenge.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.polarsirkelrock.dancechallenge.entity.Participant;
import com.polarsirkelrock.dancechallenge.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    private final ParticipantRepository participantRepository;

    @Value("${app.qr-output-dir:./generated-qr}")
    private String qrOutputDir;

    @Transactional
    public int generateAll(String baseUrl) throws Exception {
        List<Participant> participants = participantRepository.findAll();
        Path outputPath = Paths.get(qrOutputDir);
        Files.createDirectories(outputPath);

        int count = 0;
        for (Participant p : participants) {
            String content = baseUrl + "/register?partner=" + p.getId();
            String fileName = "qr_" + p.getId() + ".png";
            Path filePath = outputPath.resolve(fileName);
            generateQrCode(content, filePath.toString(), 300);

            String qrUrl = "/qr/" + fileName;
            p.setQrCodeUrl(qrUrl);
            participantRepository.save(p);
            count++;
        }
        return count;
    }

    public void generateQrCode(String content, String filePath, int size) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        MatrixToImageWriter.writeToPath(matrix, "PNG", Paths.get(filePath));
    }

    public byte[] generateZip() throws IOException {
        Path qrDir = Paths.get(qrOutputDir);
        if (!Files.exists(qrDir)) {
            throw new FileNotFoundException("QR directory not found. Generate QR codes first.");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.list(qrDir)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(p -> {
                        try {
                            zos.putNextEntry(new ZipEntry(p.getFileName().toString()));
                            Files.copy(p, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            log.error("Error adding file to ZIP: {}", p, e);
                        }
                    });
        }
        return baos.toByteArray();
    }
}
