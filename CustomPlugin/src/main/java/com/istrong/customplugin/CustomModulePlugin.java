package com.istrong.customplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CustomModulePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("模块插件输出");
    }
}