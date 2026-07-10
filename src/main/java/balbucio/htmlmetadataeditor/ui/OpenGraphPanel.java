package balbucio.htmlmetadataeditor.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import balbucio.htmlmetadataeditor.model.MetaTag;
import balbucio.htmlmetadataeditor.model.MetaTagType;

public class OpenGraphPanel extends JPanel {

    private static final Set<String> KNOWN_KEYS = Set.of(
            "og:title", "og:description", "og:image", "og:url",
            "og:type", "og:site_name", "og:locale"
    );

    private JTextField ogTitleField;
    private JTextField ogDescriptionField;
    private JTextField ogImageField;
    private JTextField ogUrlField;
    private JTextField ogTypeField;
    private JTextField ogSiteNameField;
    private JTextField ogLocaleField;
    private JTextArea otherArea;

    public OpenGraphPanel() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 4, 2, 4);

        int row = 0;
        ogTitleField = new JTextField(30);
        addFieldRow(form, gbc, row++, "og:title:", ogTitleField);

        ogDescriptionField = new JTextField();
        addFieldRow(form, gbc, row++, "og:description:", ogDescriptionField);

        ogImageField = new JTextField();
        addFieldRow(form, gbc, row++, "og:image:", ogImageField);

        ogUrlField = new JTextField();
        addFieldRow(form, gbc, row++, "og:url:", ogUrlField);

        ogTypeField = new JTextField();
        addFieldRow(form, gbc, row++, "og:type:", ogTypeField);

        ogSiteNameField = new JTextField();
        addFieldRow(form, gbc, row++, "og:site_name:", ogSiteNameField);

        ogLocaleField = new JTextField();
        addFieldRow(form, gbc, row++, "og:locale:", ogLocaleField);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel("Outros OG (property=content por linha):"), gbc);

        gbc.gridy = row++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        otherArea = new JTextArea(5, 30);
        otherArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        form.add(new JScrollPane(otherArea), gbc);

        add(new JScrollPane(form), BorderLayout.CENTER);
    }

    private void addFieldRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    public void loadFrom(List<MetaTag> tags) {
        ogTitleField.setText("");
        ogDescriptionField.setText("");
        ogImageField.setText("");
        ogUrlField.setText("");
        ogTypeField.setText("");
        ogSiteNameField.setText("");
        ogLocaleField.setText("");
        otherArea.setText("");

        for (MetaTag tag : tags) {
            switch (tag.getKey()) {
                case "og:title" -> ogTitleField.setText(tag.getContent());
                case "og:description" -> ogDescriptionField.setText(tag.getContent());
                case "og:image" -> ogImageField.setText(tag.getContent());
                case "og:url" -> ogUrlField.setText(tag.getContent());
                case "og:type" -> ogTypeField.setText(tag.getContent());
                case "og:site_name" -> ogSiteNameField.setText(tag.getContent());
                case "og:locale" -> ogLocaleField.setText(tag.getContent());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (MetaTag tag : tags) {
            if (!KNOWN_KEYS.contains(tag.getKey())) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(tag.getKey()).append("=").append(tag.getContent());
            }
        }
        otherArea.setText(sb.toString());
    }

    public List<MetaTag> getTags() {
        List<MetaTag> tags = new ArrayList<>();

        addIfNotEmpty(tags, "og:title", ogTitleField.getText().trim());
        addIfNotEmpty(tags, "og:description", ogDescriptionField.getText().trim());
        addIfNotEmpty(tags, "og:image", ogImageField.getText().trim());
        addIfNotEmpty(tags, "og:url", ogUrlField.getText().trim());
        addIfNotEmpty(tags, "og:type", ogTypeField.getText().trim());
        addIfNotEmpty(tags, "og:site_name", ogSiteNameField.getText().trim());
        addIfNotEmpty(tags, "og:locale", ogLocaleField.getText().trim());

        String other = otherArea.getText();
        if (other != null && !other.isBlank()) {
            for (String line : other.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String key = line.substring(0, eq).trim();
                    String val = line.substring(eq + 1).trim();
                    if (!key.isEmpty() && !val.isEmpty()) {
                        tags.add(new MetaTag(key, val, MetaTagType.OPEN_GRAPH));
                    }
                }
            }
        }

        return tags;
    }

    private void addIfNotEmpty(List<MetaTag> tags, String key, String value) {
        if (!value.isEmpty()) {
            tags.add(new MetaTag(key, value, MetaTagType.OPEN_GRAPH));
        }
    }
}
