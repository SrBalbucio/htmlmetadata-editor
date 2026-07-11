package balbucio.htmlmetadataeditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import balbucio.htmlmetadataeditor.model.PreviewData;

public class PreviewCard extends JPanel {

    private static final int CARD_WIDTH = 440;
    private static final int IMG_HEIGHT = 247;

    private final ImagePanel imagePanel;
    private final JLabel siteLabel;
    private final JLabel titleLabel;
    private final JLabel descLabel;

    public PreviewCard() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new CardBorder());
        setAlignmentX(LEFT_ALIGNMENT);

        imagePanel = new ImagePanel();
        add(imagePanel);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        siteLabel = new JLabel(" ");
        siteLabel.setFont(siteLabel.getFont().deriveFont(Font.PLAIN, 11f));
        siteLabel.setForeground(new Color(120, 120, 120));
        textPanel.add(siteLabel);

        textPanel.add(Box.createVerticalStrut(4));

        titleLabel = new JLabel(" ");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(new Color(30, 30, 30));
        textPanel.add(titleLabel);

        textPanel.add(Box.createVerticalStrut(4));

        descLabel = new JLabel(" ");
        descLabel.setFont(descLabel.getFont().deriveFont(12f));
        descLabel.setForeground(new Color(100, 100, 100));
        textPanel.add(descLabel);

        add(textPanel);
    }

    public void update(PreviewData data) {
        imagePanel.setImageUrl(data.getImageUrl());

        String domain = "";
        try {
            if (data.getUrl() != null && !data.getUrl().isEmpty()) {
                domain = URI.create(data.getUrl()).getHost();
            }
        } catch (Exception ignored) {}

        String sitePart = data.getSiteName() != null && !data.getSiteName().isEmpty()
                ? data.getSiteName() : "";

        if (!sitePart.isEmpty() && !domain.isEmpty()) {
            siteLabel.setText(sitePart + "  \u00b7  " + domain);
        } else if (!sitePart.isEmpty()) {
            siteLabel.setText(sitePart);
        } else if (domain != null && !domain.isEmpty()) {
            siteLabel.setText(domain);
        } else {
            siteLabel.setText(" ");
        }

        String title = data.getTitle();
        titleLabel.setText(title != null && !title.isEmpty() ? title : " ");

        String desc = data.getDescription();
        if (desc != null && !desc.isEmpty()) {
            descLabel.setText("<html><body style='width:400px'>" + escapeHtml(desc) + "</body></html>");
        } else {
            descLabel.setText(" ");
        }

        revalidate();
        repaint();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\n", "<br>");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CARD_WIDTH, super.getPreferredSize().height);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension d = getPreferredSize();
        return new Dimension(d.width, Short.MAX_VALUE);
    }

    private static class CardBorder implements Border {

        private static final Color ACCENT = new Color(88, 101, 242);
        private static final Color BORDER_COLOR = new Color(220, 220, 220);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(ACCENT);
            g.fillRect(x, y, 4, height);
            g.setColor(BORDER_COLOR);
            g.drawRect(x, y, width - 1, height - 1);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 5, 1, 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }

    private class ImagePanel extends JPanel {

        private String imageUrl;
        private BufferedImage image;
        private boolean loading;

        void setImageUrl(String url) {
            this.imageUrl = url;
            this.image = null;
            this.loading = false;
            if (url != null && !url.isEmpty()) {
                loadAsync(url);
            }
            repaint();
        }

        private void loadAsync(String urlStr) {
            loading = true;
            repaint();
            new Thread(() -> {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (compatible; MetadataPreview/1.0)");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    BufferedImage img = ImageIO.read(conn.getInputStream());
                    SwingUtilities.invokeLater(() -> {
                        image = img;
                        loading = false;
                        repaint();
                    });
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> loading = false);
                }
            }).start();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(CARD_WIDTH, IMG_HEIGHT);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            if (image != null) {
                double scale = Math.max((double) w / image.getWidth(),
                        (double) h / image.getHeight());
                int iw = (int) (image.getWidth() * scale);
                int ih = (int) (image.getHeight() * scale);
                int x = (w - iw) / 2;
                int y = (h - ih) / 2;
                g2.drawImage(image, x, y, iw, ih, null);
            } else if (loading) {
                g2.setColor(new Color(60, 60, 60));
                g2.fillRect(0, 0, w, h);
                g2.setColor(Color.WHITE);
                String text = "Carregando imagem...";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (w - fm.stringWidth(text)) / 2,
                        (h + fm.getAscent()) / 2);
            } else {
                g2.setColor(new Color(50, 50, 50));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(180, 180, 180));
                g2.setStroke(new BasicStroke(2));
                int cx = w / 2, cy = h / 2, r = 28;
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.fillOval(cx - 4, cy - 4, 8, 8);
                String text = "Sem imagem";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (w - fm.stringWidth(text)) / 2,
                        cy + r + 18);
            }

            g2.dispose();
        }
    }
}
