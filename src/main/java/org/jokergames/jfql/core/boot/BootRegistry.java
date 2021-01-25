package org.jokergames.jfql.core.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BootRegistry {

    private final List<BootSection> sections;

    public BootRegistry() {
        this.sections = new ArrayList<>();
    }

    public void registerSection(BootSection section) {
        sections.add(section);
    }

    public void unregisterSection(BootSection section) {
        sections.remove(section);
    }

    public void invoke(String[] args) {
        List<BootArgument> arguments = new ArrayList<>();

        Arrays.stream(args).forEach(string -> {
            if (string.startsWith("-")) {
                arguments.add(new BootArgument(string.replaceFirst("-", ""), null));
            } else if (string.contains("=")) {
                String[] strings = string.split("=");

                if (strings.length == 2) {
                    arguments.add(new BootArgument(strings[0], strings[1]));
                } else {
                    arguments.add(new BootArgument(strings[0], ""));
                }

            }
        });

        int booted = 0;

        for (BootSection section : sections) {
            if (section.getArguments().equals(arguments) && section.getType() == BootSection.Type.EQUALS) {
                section.boot(arguments);
                booted++;
            }
        }

        if (booted == 0) {
            sections.stream().filter(section -> section.getType() == BootSection.Type.DEFAULT).forEach(section -> section.boot(arguments));
        }
    }

    public List<BootSection> getSections() {
        return sections;
    }
}
