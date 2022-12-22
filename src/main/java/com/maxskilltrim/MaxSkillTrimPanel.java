package com.maxskilltrim;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.LinkBrowser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Objects;

@Slf4j
@Singleton
public class MaxSkillTrimPanel extends PluginPanel {
    @Inject
    private MaxSkillTrimPlugin plugin;
    private final MaxSkillTrimConfig maxSkillTrimConfig;
    private JComboBox<MaxSkillTrimFile>  comboBox;

    @Inject
    public MaxSkillTrimPanel(MaxSkillTrimConfig config) {
        this.maxSkillTrimConfig = config;
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel buttonPanel = buildButtonPanel();
        JPanel getMoreTrimsPanel = buildGetMoreTrimsPanel();
        JPanel comboBoxPanel = buildComboBoxPanel();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addComponent(buttonPanel)
                    .addComponent(getMoreTrimsPanel)
                    .addComponent(comboBoxPanel)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(buttonPanel)
            .addGap(10)
            .addComponent(getMoreTrimsPanel)
            .addGap(10)
            .addComponent(comboBoxPanel)
        );
    }

    private JPanel buildComboBoxPanel() {
        comboBox = new JComboBox<>();
        refreshComboBoxOptions();

        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                if (e.getItem() instanceof MaxSkillTrimFile)
                {
                    MaxSkillTrimFile file = (MaxSkillTrimFile) e.getItem();
                    this.plugin.setImage(file);
                }
            }
        });

        JLabel currentTrim = new JLabel("Current trim: ");
        JPanel panel = new JPanel();

        panel.add(currentTrim, GroupLayout.Alignment.BASELINE);
        panel.add(comboBox, GroupLayout.Alignment.BASELINE);

        return panel;
    }

    private JPanel buildGetMoreTrimsPanel() {
        JButton getMoreTrimsButton = new JButton("Get more trims!");
        getMoreTrimsButton.addActionListener((ev) -> LinkBrowser.browse("https://github.com/NathanQuayle/max-skill-trim/tree/custom-trims"));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        panel.add(getMoreTrimsButton, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JButton openMaxSkillTrimFileFolderButton = new JButton("Open Folder");
        openMaxSkillTrimFileFolderButton.addActionListener((ev) -> {
            try {
                Desktop.getDesktop().open(MaxSkillTrimPlugin.MAXSKILLTRIMS_DIR);
            } catch(Exception ex) {
                log.warn(null, ex);
            }
        });

        JButton refreshMaxSkillTrims = new JButton("Refresh");
        refreshMaxSkillTrims.addActionListener((ev) -> {
            refreshComboBoxOptions();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));

        panel.add(openMaxSkillTrimFileFolderButton);
        panel.add(refreshMaxSkillTrims);

        return panel;
    }

    private void refreshComboBoxOptions() {
        comboBox.removeAllItems();

        for(File f : Objects.requireNonNull(MaxSkillTrimPlugin.MAXSKILLTRIMS_DIR.listFiles())) {
            String fileName = f.getName();
            MaxSkillTrimFile maxSkillTrimFile = new MaxSkillTrimFile(
                    fileName.substring(0, 1).toUpperCase() + fileName.substring(1, fileName.length() - 4),
                    fileName);
            comboBox.addItem(maxSkillTrimFile);

            if(maxSkillTrimConfig.selectedMaxSkillTrimFilename().equals(fileName)) {
                comboBox.setSelectedItem(maxSkillTrimFile);
            }
        }
    }
}
