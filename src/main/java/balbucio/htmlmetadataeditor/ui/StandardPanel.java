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

public class StandardPanel extends JPanel {

    private static final Set<String> KNOWN_KEYS = Set.of(
            "description", "keywords", "author", "viewport", "robots", "charset"
    );

    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField keywordsField;
    private JTextField authorField;
    private JTextField viewportField;
    private JTextField robotsField;
    private JTextField charsetField;
    private JTextArea otherArea;

    public StandardPanel() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 4, 2, 4);

        int row = 0;
        titleField = new JTextField(30);
        addFieldRow(form, gbc, row++, "Title:", titleField);

        descriptionField = new JTextField();
        addFieldRow(form, gbc, row++, "Description:", descriptionField);

        keywordsField = new JTextField();
        addFieldRow(form, gbc, row++, "Keywords:", keywordsField);

        authorField = new JTextField();
        addFieldRow(form, gbc, row++, "Author:", authorField);

        viewportField = new JTextField();
        addFieldRow(form, gbc, row++, "Viewport:", viewportField);

        robotsField = new JTextField();
        addFieldRow(form, gbc, row++, "Robots:", robotsField);

        charsetField = new JTextField();
        addFieldRow(form, gbc, row++, "Charset:", charsetField);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel("Outros metadados (name=valor por linha):"), gbc);

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

    public void loadFrom(String title, List<MetaTag> tags) {
        titleField.setText(title != null ? title : "");
        descriptionField.setText("");
        keywordsField.setText("");
        authorField.setText("");
        viewportField.setText("");
        robotsField.setText("");
        charsetField.setText("");
        otherArea.setText("");

        for (MetaTag tag : tags) {
            switch (tag.getKey()) {
                case "description" -> descriptionField.setText(tag.getContent());
                case "keywords" -> keywordsField.setText(tag.getContent());
                case "author" -> authorField.setText(tag.getContent());
                case "viewport" -> viewportField.setText(tag.getContent());
                case "robots" -> robotsField.setText(tag.getContent());
                case "charset" -> charsetField.setText(tag.getContent());
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

    public String getTitle() {
        return titleField.getText().trim();
    }

    public List<MetaTag> getTags() {
        List<MetaTag> tags = new ArrayList<>();

        addIfNotEmpty(tags, "description", descriptionField.getText().trim());
        addIfNotEmpty(tags, "keywords", keywordsField.getText().trim());
        addIfNotEmpty(tags, "author", authorField.getText().trim());
        addIfNotEmpty(tags, "viewport", viewportField.getText().trim());
        addIfNotEmpty(tags, "robots", robotsField.getText().trim());
        addIfNotEmpty(tags, "charset", charsetField.getText().trim());

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
                        tags.add(new MetaTag(key, val, MetaTagType.STANDARD));
                    }
                }
            }
        }

        return tags;
    }

    private void addIfNotEmpty(List<MetaTag> tags, String key, String value) {
        if (!value.isEmpty()) {
            tags.add(new MetaTag(key, value, MetaTagType.STANDARD));
        }
    }
}
