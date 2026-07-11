package balbucio.htmlmetadataeditor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import balbucio.htmlmetadataeditor.model.PreviewData;
import balbucio.htmlmetadataeditor.service.HtmlMetadataService;

public class PreviewFrame extends JFrame {

    private final HtmlMetadataService service;
    private final JTextField urlField;
    private final PreviewCard card;
    private final JLabel statusLabel;

    public PreviewFrame(HtmlMetadataService service) {
        super("Pr\u00e9-visualiza\u00e7\u00e3o");
        this.service = service;
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(520, 560);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout(4, 4));
        topPanel.setBorder(BorderFactory.createTitledBorder("Preview por URL"));
        urlField = new JTextField();
        JButton previewUrlBtn = new JButton("Preview URL");
        JPanel urlRow = new JPanel(new BorderLayout(4, 0));
        urlRow.add(urlField, BorderLayout.CENTER);
        urlRow.add(previewUrlBtn, BorderLayout.EAST);
        topPanel.add(urlRow, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        card = new PreviewCard();
        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        previewUrlBtn.addActionListener(e -> fetchUrl());
        urlField.addActionListener(e -> fetchUrl());
    }

    public void showPreview(PreviewData data) {
        card.update(data);
        statusLabel.setText(" ");
        setVisible(true);
        toFront();
    }

    public void focusUrlField() {
        urlField.setText("");
        urlField.requestFocus();
        card.update(new PreviewData(" ", " ", null, null, null));
        statusLabel.setText(" ");
        setVisible(true);
        toFront();
    }

    private void fetchUrl() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) return;

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
            urlField.setText(url);
        }

        statusLabel.setText("Buscando metadados...");
        String finalUrl = url;

        new SwingWorker<PreviewData, Void>() {
            @Override
            protected PreviewData doInBackground() throws Exception {
                return service.fetchPreview(finalUrl);
            }

            @Override
            protected void done() {
                try {
                    PreviewData data = get();
                    card.update(data);
                    statusLabel.setText(" ");
                } catch (Exception e) {
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    statusLabel.setText("Erro: " + msg);
                }
            }
        }.execute();
    }
}
