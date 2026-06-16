package com.polarsirkelrock.dancechallenge.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    // Label dimensions for BT M110 (203 DPI, 40 mm wide label tape)
    // 384 × 240 px  ≈  48 mm × 30 mm  – landscape, fits name + QR side by side
    private static final int LABEL_W = 384;
    private static final int LABEL_H = 240;
    private static final int PADDING = 12;
    private static final int QR_SIZE = LABEL_H - 2 * PADDING;   // 216 px square

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
            generateLabelImage(p, content, filePath);

            String qrUrl = "/qr/" + fileName;
            p.setQrCodeUrl(qrUrl);
            participantRepository.save(p);
            count++;
        }
        return count;
    }

    /**
     * Generates a label image containing the QR code, participant name and ID.
     * Layout (landscape):
     *   [ QR code ]  |  Name (large)
     *                |  ID: 12345
     */
    private void generateLabelImage(Participant p, String qrContent, Path dest)
            throws WriterException, IOException {

        // 1. Render QR code to a BufferedImage
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix,
                new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF));

        // 2. Compose the label
        BufferedImage label = new BufferedImage(LABEL_W, LABEL_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = label.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, LABEL_W, LABEL_H);

        // Draw QR code on the left
        g.drawImage(qrImage, PADDING, PADDING, QR_SIZE, QR_SIZE, null);

        // Text area starts after the QR code
        int textX = PADDING + QR_SIZE + PADDING;
        int textW = LABEL_W - textX - PADDING;

        // Participant name – bold, as large as possible while fitting the text area
        Font nameFont = fitFont(g, p.getName(), textW, Font.BOLD, 44, 16);
        g.setFont(nameFont);
        g.setColor(Color.BLACK);
        FontMetrics nameFm = g.getFontMetrics();
        int nameY = PADDING + nameFm.getAscent();
        drawWrappedText(g, p.getName(), textX, nameY, textW, nameFm);

        // ID – monospaced, clearly readable
        int idFontSize = Math.max(18, nameFont.getSize() - 8);
        Font idFont = new Font(Font.MONOSPACED, Font.BOLD, idFontSize);
        g.setFont(idFont);
        FontMetrics idFm = g.getFontMetrics();
        String idLabel = "ID: " + p.getId();
        int idY = LABEL_H - PADDING - idFm.getDescent();
        g.drawString(idLabel, textX, idY);

        g.dispose();

        // 3. Save
        ImageIO.write(label, "PNG", dest.toFile());
    }

    /** Returns the largest font (between maxSize and minSize) that fits {@code text} within {@code maxWidth}. */
    private Font fitFont(Graphics2D g, String text, int maxWidth, int style, int maxSize, int minSize) {
        for (int size = maxSize; size >= minSize; size--) {
            Font font = new Font(Font.SANS_SERIF, style, size);
            FontMetrics fm = g.getFontMetrics(font);
            if (fm.stringWidth(text) <= maxWidth) {
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, style, minSize);
    }

    /** Draws text, wrapping onto a second line if necessary. */
    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth, FontMetrics fm) {
        if (fm.stringWidth(text) <= maxWidth) {
            g.drawString(text, x, y);
            return;
        }
        // Simple word-wrap: split on last space that still fits
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;
        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(candidate) <= maxWidth) {
                line = new StringBuilder(candidate);
            } else {
                if (!line.isEmpty()) {
                    g.drawString(line.toString(), x, lineY);
                    lineY += fm.getHeight();
                }
                line = new StringBuilder(word);
            }
        }
        if (!line.isEmpty()) {
            g.drawString(line.toString(), x, lineY);
        }
    }

    // Kept for direct use in QrCodeUtil / other callers
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
