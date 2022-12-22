package com.maxskilltrim;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
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

        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.add(comboBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openMaxSkillTrimFileFolderButton);
        buttonPanel.add(refreshMaxSkillTrims);
        buttonPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        layout.setHorizontalGroup(
                layout.createParallelGroup().
                        addComponent(buttonPanel).
                        addComponent(comboBoxPanel)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(buttonPanel)
                .addGap(10)
                .addComponent(comboBoxPanel)
        );
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
