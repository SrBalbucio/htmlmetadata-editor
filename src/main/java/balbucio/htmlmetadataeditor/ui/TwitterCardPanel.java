package balbucio.htmlmetadataeditor.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import balbucio.htmlmetadataeditor.model.MetaTag;
import balbucio.htmlmetadataeditor.model.MetaTagType;

public class TwitterCardPanel extends JPanel {

    private static final Set<String> KNOWN_KEYS = Set.of(
            "twitter:card", "twitter:site", "twitter:creator",
            "twitter:title", "twitter:description", "twitter:image",
            "twitter:image:alt"
    );

    private JTextField twCardField;
    private JTextField twSiteField;
    private JTextField twCreatorField;
    private JTextField twTitleField;
    private JTextField twDescriptionField;
    private JTextField twImageField;
    private JTextField twImageAltField;
    private JTextArea otherArea;

    public TwitterCardPanel() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 4, 2, 4);

        int row = 0;
        twCardField = new JTextField(30);
        addFieldRow(form, gbc, row++, "twitter:card:", twCardField);

        twSiteField = new JTextField();
        addFieldRow(form, gbc, row++, "twitter:site:", twSiteField);

        twCreatorField = new JTextField();
        addFieldRow(form, gbc, row++, "twitter:creator:", twCreatorField);

        twTitleField = new JTextField();
        addFieldRow(form, gbc, row++, "twitter:title:", twTitleField);

        twDescriptionField = new JTextField();
        addFieldRow(form, gbc, row++, "twitter:description:", twDescriptionField);

        twImageField = new JTextField();
        addFieldRow(form, gbc, row++, "twitter:image:", twImageField);

        twImageAltField = new JTextField();
        addFieldRow(form, gbc, row++, "twitter:image:alt:", twImageAltField);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel("Outros Twitter (property=content por linha):"), gbc);

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
        twCardField.setText("");
        twSiteField.setText("");
        twCreatorField.setText("");
        twTitleField.setText("");
        twDescriptionField.setText("");
        twImageField.setText("");
        twImageAltField.setText("");
        otherArea.setText("");

        for (MetaTag tag : tags) {
            switch (tag.getKey()) {
                case "twitter:card" -> twCardField.setText(tag.getContent());
                case "twitter:site" -> twSiteField.setText(tag.getContent());
                case "twitter:creator" -> twCreatorField.setText(tag.getContent());
                case "twitter:title" -> twTitleField.setText(tag.getContent());
                case "twitter:description" -> twDescriptionField.setText(tag.getContent());
                case "twitter:image" -> twImageField.setText(tag.getContent());
                case "twitter:image:alt" -> twImageAltField.setText(tag.getContent());
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

        addIfNotEmpty(tags, "twitter:card", twCardField.getText().trim());
        addIfNotEmpty(tags, "twitter:site", twSiteField.getText().trim());
        addIfNotEmpty(tags, "twitter:creator", twCreatorField.getText().trim());
        addIfNotEmpty(tags, "twitter:title", twTitleField.getText().trim());
        addIfNotEmpty(tags, "twitter:description", twDescriptionField.getText().trim());
        addIfNotEmpty(tags, "twitter:image", twImageField.getText().trim());
        addIfNotEmpty(tags, "twitter:image:alt", twImageAltField.getText().trim());

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
                        tags.add(new MetaTag(key, val, MetaTagType.TWITTER));
                    }
                }
            }
        }

        return tags;
    }

    private void addIfNotEmpty(List<MetaTag> tags, String key, String value) {
        if (!value.isEmpty()) {
            tags.add(new MetaTag(key, value, MetaTagType.TWITTER));
        }
    }
}
