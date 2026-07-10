package balbucio.htmlmetadataeditor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import balbucio.htmlmetadataeditor.model.MetaTag;
import balbucio.htmlmetadataeditor.model.MetaTagType;
import balbucio.htmlmetadataeditor.service.HtmlDocument;
import balbucio.htmlmetadataeditor.service.HtmlMetadataService;

public class MainFrame extends JFrame {

    private final HtmlMetadataService service = new HtmlMetadataService();
    private HtmlDocument currentDoc;
    private File currentFile;
    private boolean isLoading;

    private JTextField fileField;
    private JButton loadButton;
    private StandardPanel standardPanel;
    private OpenGraphPanel ogPanel;
    private TwitterCardPanel twitterPanel;
    private JButton saveButton;
    private JLabel statusLabel;

    public MainFrame() {
        super("Editor de Metadados HTML");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 620);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));

        JPanel filePanel = new JPanel(new BorderLayout(8, 0));
        filePanel.setBorder(BorderFactory.createTitledBorder("Arquivo HTML"));
        fileField = new JTextField();
        fileField.setEditable(false);
        JButton browseButton = new JButton("Selecionar");
        loadButton = new JButton("Carregar");
        loadButton.setEnabled(false);

        browseButton.addActionListener(e -> browseFile());
        loadButton.addActionListener(e -> loadFile());

        JPanel fileButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        fileButtons.add(browseButton);
        fileButtons.add(loadButton);

        filePanel.add(fileField, BorderLayout.CENTER);
        filePanel.add(fileButtons, BorderLayout.EAST);

        add(filePanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        standardPanel = new StandardPanel();
        ogPanel = new OpenGraphPanel();
        twitterPanel = new TwitterCardPanel();

        tabbedPane.addTab("Standard", standardPanel);
        tabbedPane.addTab("Open Graph", ogPanel);
        tabbedPane.addTab("Twitter Cards", twitterPanel);

        add(tabbedPane, BorderLayout.CENTER);

        wirePropagation();

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        saveButton = new JButton("Salvar");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveFile());

        statusLabel = new JLabel(" ");

        bottomPanel.add(saveButton, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos HTML (*.html, *.htm)", "html", "htm"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileField.setText(chooser.getSelectedFile().getAbsolutePath());
            loadButton.setEnabled(true);
        }
    }

    private void loadFile() {
        String path = fileField.getText().trim();
        if (path.isEmpty()) return;

        File file = new File(path);
        if (!file.exists()) {
            statusLabel.setText("Arquivo não encontrado.");
            return;
        }

        try {
            currentDoc = service.parse(file);
            currentFile = file;
            populatePanels();
            saveButton.setEnabled(true);
            statusLabel.setText("Carregado: " + file.getName());
        } catch (IOException e) {
            statusLabel.setText("Erro ao carregar.");
            JOptionPane.showMessageDialog(this,
                    "Erro ao ler o arquivo:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populatePanels() {
        if (currentDoc == null) return;

        isLoading = true;
        try {
            List<MetaTag> standardTags = currentDoc.getTags().stream()
                    .filter(t -> t.getType() == MetaTagType.STANDARD)
                    .collect(Collectors.toList());
            List<MetaTag> ogTags = currentDoc.getTags().stream()
                    .filter(t -> t.getType() == MetaTagType.OPEN_GRAPH)
                    .collect(Collectors.toList());
            List<MetaTag> twitterTags = currentDoc.getTags().stream()
                    .filter(t -> t.getType() == MetaTagType.TWITTER)
                    .collect(Collectors.toList());

            standardPanel.loadFrom(currentDoc.getTitle(), standardTags);
            ogPanel.loadFrom(ogTags);
            twitterPanel.loadFrom(twitterTags);
        } finally {
            isLoading = false;
        }
    }

    private void wirePropagation() {
        standardPanel.onTitleChanged(() -> {
            if (!isLoading) {
                String title = standardPanel.getTitle();
                ogPanel.setTitleIfEmpty(title);
                twitterPanel.setTitleIfEmpty(title);
            }
        });
        standardPanel.onDescriptionChanged(() -> {
            if (!isLoading) {
                String desc = standardPanel.getDescription();
                ogPanel.setDescriptionIfEmpty(desc);
                twitterPanel.setDescriptionIfEmpty(desc);
            }
        });
    }

    private void saveFile() {
        if (currentDoc == null || currentFile == null) return;

        try {
            String title = standardPanel.getTitle();
            List<MetaTag> allTags = new ArrayList<>();
            allTags.addAll(standardPanel.getTags());
            allTags.addAll(ogPanel.getTags());
            allTags.addAll(twitterPanel.getTags());

            currentDoc.setTitle(title);
            currentDoc.setTags(allTags);

            service.save(currentFile, currentDoc);
            statusLabel.setText("Salvo com sucesso!");
        } catch (IOException e) {
            statusLabel.setText("Erro ao salvar.");
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar o arquivo:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
